package org.mobicents.protocols.asn;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * Shared-nothing concurrency stress test — verifies that per-thread ThreadLocal
 * pools of BerCursor and BerWriter are truly isolated with zero cross-thread
 * corruption.
 *
 * <p>Each thread independently encodes → decodes → validates in a tight loop.
 * Different threads use different data patterns to detect cross-contamination.
 * A checksum verifier catches any byte corruption from shared-state bugs.</p>
 *
 * <pre>mvn test -pl asn/asn-api -Dtest=BerCursorConcurrencyTest -DfailIfNoTests=false</pre>
 */
public class BerCursorConcurrencyTest {

    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    private static final int ITERATIONS_PER_THREAD = 50000;
    private static final int WARMUP_PER_THREAD = 2000;

    // ══════════════════════════════════════════════════════════
    // Test 1: Multi-thread BerCursor decode — no cross-corruption
    // ══════════════════════════════════════════════════════════

    @Test
    public void testMultiThreadDecodeNoCorruption() throws Exception {
        int numThreads = Math.max(4, THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        LongAdder ops = new LongAdder();
        AtomicInteger errors = new AtomicInteger(0);

        CountDownLatch latch = new CountDownLatch(numThreads);
        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    // Each thread encodes unique data pattern
                    byte[] data = encodeTestSequence(threadId);
                    int expectedSum = computeExpectedChecksum(threadId);
                    int batchSize = ITERATIONS_PER_THREAD / 10;

                    // Warmup
                    for (int i = 0; i < WARMUP_PER_THREAD; i++) {
                        int sum = decodeAndChecksum(data, threadId);
                        if (sum != expectedSum) errors.incrementAndGet();
                    }

                    // Measure
                    long batchSum = 0;
                    for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                        int sum = decodeAndChecksum(data, threadId);
                        if (sum != expectedSum) errors.incrementAndGet();
                        batchSum += sum;
                        if (i > 0 && i % batchSize == 0) {
                            ops.add(batchSize);
                            batchSum = 0;
                        }
                    }
                    ops.add(ITERATIONS_PER_THREAD % batchSize);
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(60, TimeUnit.SECONDS), "timeout");
        executor.shutdown();

        assertEquals(errors.get(), 0, "Checksum errors detected — cross-thread corruption!");
        System.out.printf("%n=== Shared-Nothing Concurrency Test ===%n");
        System.out.printf("Threads: %d, iterations/thread: %,d%n", numThreads, ITERATIONS_PER_THREAD);
        System.out.printf("Total ops: %,d, errors: %d%n", ops.sum(), errors.get());
        System.out.printf("Result: PASS — zero cross-thread corruption%n");
    }

    // ══════════════════════════════════════════════════════════
    // Test 2: Multi-thread BerWriter pool — zero contention
    // ══════════════════════════════════════════════════════════

    @Test
    public void testMultiThreadBerWriterPool() throws Exception {
        int numThreads = Math.max(4, THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        LongAdder ops = new LongAdder();
        LongAdder totalBytes = new LongAdder();
        AtomicInteger errors = new AtomicInteger(0);

        CountDownLatch latch = new CountDownLatch(numThreads);
        long start = System.nanoTime();

        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    int estimatedSize = 256 + (threadId % 3) * 512; // vary sizes across threads
                    for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
                        try {
                            BerWriter w = BerWriter.get(estimatedSize);
                            int mark = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
                            // BerWriter encodes backward → write in REVERSE order
                            // so forward read yields INTEGER first, OCTET_STRING second
                            w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING,
                                    new byte[]{(byte) threadId, (byte) (i & 0xFF), 0x7F}, 0, 3);
                            w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, threadId * 1000 + i);
                            w.endSequence(mark, BerTag.UNIVERSAL, BerTag.SEQUENCE);

                            // Roundtrip verify
                            BerCursor c = w.resultAsCursor();
                            c.readTag();
                            BerCursor body = c.openConstructed();
                            body.readTag();
                            int decodedInt = body.readInt32();
                            if (decodedInt != threadId * 1000 + i) errors.incrementAndGet();
                            c.release();
                            body.release();
                            totalBytes.add(w.encodedLength());
                            ops.increment();
                        } catch (Exception e) {
                            errors.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(120, TimeUnit.SECONDS), "timeout");
        long elapsed = System.nanoTime() - start;
        executor.shutdown();

        assertEquals(errors.get(), 0, "Decode errors in multi-thread BerWriter pool!");
        double tps = ops.sum() * 1_000_000_000.0 / elapsed;
        double mbs = totalBytes.sum() * 1_000_000_000.0 / elapsed / (1024 * 1024);
        System.out.printf("%n=== BerWriterPool Shared-Nothing Benchmark ===%n");
        System.out.printf("Threads: %d, iterations/thread: %,d%n", numThreads, ITERATIONS_PER_THREAD);
        System.out.printf("Throughput: %,.0f encode+decode/s (roundtrip)%n", tps);
        System.out.printf("Bandwidth:  %,.1f MB/s%n", mbs);
        System.out.printf("Errors: %d%n", errors.get());

        assertTrue(tps > 100_000, "Throughput too low: " + tps + " (expected > 100K roundtrip/s)");
    }

    // ══════════════════════════════════════════════════════════
    // Test 3: BerWriter pool vs no-pool comparison
    // ══════════════════════════════════════════════════════════

    @Test
    public void testWriterPoolVsNoPool() throws Exception {
        int iterations = 100_000;
        int warmup = 10_000;

        // Warmup: JIT + thread-local pool init
        for (int i = 0; i < warmup; i++) {
            BerWriter w = BerWriter.get(BerWriterPool.TINY);
            w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, i);
        }
        for (int i = 0; i < warmup; i++) {
            BerWriter w = BerWriter.get();
            w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, i);
        }

        // Run multiple rounds and take best (account for JVM noise)
        long bestPool = Long.MAX_VALUE, bestNoPool = Long.MAX_VALUE;
        for (int round = 0; round < 5; round++) {
            System.gc();
            Thread.sleep(50);
            long t0 = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                BerWriter w = BerWriter.get(BerWriterPool.TINY);
                w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, i);
                w.resultAsSlice();
            }
            bestPool = Math.min(bestPool, System.nanoTime() - t0);

            System.gc();
            Thread.sleep(50);
            t0 = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                BerWriter w = BerWriter.get();
                w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, i);
                w.resultAsSlice();
            }
            bestNoPool = Math.min(bestNoPool, System.nanoTime() - t0);
        }

        double poolUs = bestPool / 1000.0;
        double noPoolUs = bestNoPool / 1000.0;
        double speedup = (double) bestNoPool / bestPool;

        System.out.printf("%n=== Pool vs No-Pool (%,d iterations, best of 5) ===%n", iterations);
        System.out.printf("Pool (TINY 256B):  %,8.0f μs | %,7.1f ns/op%n", poolUs, bestPool / (double) iterations);
        System.out.printf("No-Pool (8KB TL):  %,8.0f μs | %,7.1f ns/op%n", noPoolUs, bestNoPool / (double) iterations);
        System.out.printf("Speedup: %.2fx %s%n", speedup >= 1.0 ? speedup : 1.0 / speedup,
                speedup >= 1.0 ? "POOL FASTER" : "NO-POOL FASTER");

        // Pool should be at least 70% as fast as no-pool (TINY 256B vs 8KB)
        // If pool is faster than no-pool, that's a bonus
        assertTrue(speedup >= 0.7, 
            String.format("Pool too slow: %.2fx (min 0.70x). pool=%dns noPool=%dns", 
                speedup, bestPool, bestNoPool));
    }

    // ══════════════════════════════════════════════════════════
    // Test 4: Zero-copy forward — BerSlice → business logic
    // ══════════════════════════════════════════════════════════

    @Test
    public void testZeroCopyForwardPipeline() throws Exception {
        // Simulates: MAP/CAP decode → extract hot fields → forward raw payload
        byte[] ussdRequest = buildSimulatedUssdRequest();
        int iterations = 50_000;

        // Copy path: decode → byte[] copy → forward
        System.gc();
        long t0 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            processUssdRequestCopy(ussdRequest);
        }
        long copyNs = System.nanoTime() - t0;

        // Zero-copy path: decode → BerSlice → forward
        System.gc();
        t0 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            processUssdRequestZeroCopy(ussdRequest);
        }
        long zcNs = System.nanoTime() - t0;

        double speedup = (double) copyNs / zcNs;
        System.out.printf("%n=== Zero-Copy Forward Pipeline (%,d ops) ===%n", iterations);
        System.out.printf("Copy path:      %,8.0f μs | %,.1f ns/op%n", copyNs / 1000.0, copyNs / (double) iterations);
        System.out.printf("Zero-copy path: %,8.0f μs | %,.1f ns/op%n", zcNs / 1000.0, zcNs / (double) iterations);
        System.out.printf("Speedup: %.2fx %s%n", speedup >= 1.0 ? speedup : 1.0 / speedup,
                speedup >= 1.0 ? "ZERO-COPY" : "COPY");
    }

    // ══════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════

    private byte[] encodeTestSequence(int seed) {
        BerWriter w = BerWriter.get(1024);
        int mark = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, seed);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, seed * 31);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, seed * 127);
        w.endSequence(mark, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        return w.toByteArray();
    }

    private int computeExpectedChecksum(int seed) {
        return seed + seed * 31 + seed * 127;
    }

    private int decodeAndChecksum(byte[] data, int threadId) {
        try {
            BerCursor c = BerCursor.wrapHeap(data, 0, data.length);
            c.readTag();
            BerCursor body = c.openConstructed();
            int sum = 0;
            for (int j = 0; j < 3; j++) {
                body.readTag();
                sum += body.readInt32();
            }
            c.release();
            body.release();
            return sum;
        } catch (Exception e) {
            return -1;
        }
    }

    /** Simulated USSD request: SEQUENCE { invokeId(INT=1), opCode(OID), ussdString(OCTET~160B) } */
    private byte[] buildSimulatedUssdRequest() {
        BerWriter w = BerWriter.get(512);
        // BerWriter encodes backward — write in reverse order for correct decode
        byte[] ussdPayload = new byte[160];
        for (int i = 0; i < 160; i++) ussdPayload[i] = (byte) (i & 0xFF);

        int mark = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING, ussdPayload, 0, 160);
        w.writeOID(BerTag.UNIVERSAL, BerTag.OID, new int[]{0, 0, 17, 773, 1, 1, 1});
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 1);
        w.endSequence(mark, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        return w.toByteArray();
    }

    // Copy path — allocates byte[] for USSD string
    private String processUssdRequestCopy(byte[] data) {
        try {
            BerCursor c = BerCursor.wrapHeap(data, 0, data.length);
            c.readTag();
            BerCursor body = c.openConstructed();
            body.readTag(); int invokeId = body.readInt32();      // hot field
            body.readTag(); body.skipValue();                      // OID (skip)
            body.readTag(); byte[] ussdBytes = body.getOctetString(); // COPY 160B
            c.release();
            body.release();
            return invokeId + ":" + new String(ussdBytes, 0, Math.min(10, ussdBytes.length));
        } catch (Exception e) { return "error"; }
    }

    // Zero-copy path — forwards BerSlice, no byte[] allocation
    private String processUssdRequestZeroCopy(byte[] data) {
        try {
            BerCursor c = BerCursor.wrapHeap(data, 0, data.length);
            c.readTag();
            BerCursor body = c.openConstructed();
            body.readTag(); int invokeId = body.readInt32();       // hot field
            body.readTag(); body.skipValue();                       // OID
            body.readTag(); BerSlice ussdSlice = body.getValueSlice(); // ZERO COPY
            // Simulate: business logic reads only first 10 bytes from slice
            StringBuilder sb = new StringBuilder();
            sb.append(invokeId).append(':');
            for (int i = 0; i < Math.min(10, ussdSlice.length()); i++) {
                sb.append((char) (ussdSlice.byteAt(i) & 0xFF));
            }
            c.release();
            body.release();
            return sb.toString();
        } catch (Exception e) { return "error"; }
    }
}
