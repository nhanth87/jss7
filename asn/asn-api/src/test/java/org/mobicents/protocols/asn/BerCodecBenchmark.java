package org.mobicents.protocols.asn;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

/**
 * Simple micro-benchmark — BerCursor/BerWriter (new zero-copy codec) vs
 * AsnInputStream/AsnOutputStream (legacy). Run with:
 * <pre>mvn test -pl asn/asn-api -Dtest=BerCodecBenchmark -am</pre>
 *
 * <p>Not a JMH benchmark; uses System.nanoTime() for rough comparison.
 * Real JMH benchmarks should be in a separate jmh module with proper deps.</p>
 */
public class BerCodecBenchmark {

    private static final int WARMUP = 500;
    private static final int ITERATIONS = 5000;

    // ── Static test data ──────────────────────────────────────────────

    private static final byte[] SIMPLE_TLV = {0x02, 0x01, 0x2A};

    private static final byte[] NESTED_SEQ;
    private static final byte[] LARGE_OCTET;
    private static final byte[] KB_DATA = new byte[1024];

    static {
        // SEQUENCE { INTEGER(100), INTEGER(200), INTEGER(300),
        //            INTEGER(400), INTEGER(500) }
        BerWriter w = BerWriter.get();
        int mark = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 100);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 200);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 300);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 400);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 500);
        w.endSequence(mark, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        NESTED_SEQ = w.toByteArray();

        // Large OCTET STRING (1KB)
        LARGE_OCTET = new byte[1024 + 4];
        LARGE_OCTET[0] = 0x04;
        LARGE_OCTET[1] = (byte) 0x82;
        LARGE_OCTET[2] = 0x04;
        LARGE_OCTET[3] = 0x00;
        for (int i = 0; i < 1024; i++) LARGE_OCTET[4 + i] = (byte) (i & 0xFF);

        for (int i = 0; i < 1024; i++) KB_DATA[i] = (byte) (i & 0xFF);
    }

    // ==================================================================
    // FUNCTIONAL TESTS (ensure both codecs produce same results)
    // ==================================================================

    @Test
    public void testDecodeSimpleTlvNewEqualsOld() throws Exception {
        int newVal = decodeSimpleTlvBerCursor(SIMPLE_TLV);
        long oldVal = decodeSimpleTlvAsnStream(SIMPLE_TLV);
        assertEquals(newVal, (int) oldVal);
    }

    @Test
    public void testDecodeNestedSeqNewEqualsOld() throws Exception {
        int newSum = decodeNestedSeqBerCursor(NESTED_SEQ);
        long oldSum = decodeNestedSeqAsnStream(NESTED_SEQ);
        assertEquals(newSum, (int) oldSum);
    }

    @Test
    public void testEncodeInt32NewEqualsOld() throws Exception {
        byte[] newEnc = encodeInt32BerWriter(42);
        byte[] oldEnc = encodeInt32AsnStream(42);
        assertEquals(newEnc, oldEnc);
    }

    @Test
    public void testRoundtripNewEqualsOld() throws Exception {
        int newVal = roundtripBerWriterToCursor(42);
        long oldVal = roundtripAsnStreams(42);
        assertEquals(newVal, (int) oldVal);
    }

    @Test
    public void testDecodeLargeOctetNewEqualsOld() throws Exception {
        byte[] newData = decodeLargeOctetBerCursor(LARGE_OCTET);
        byte[] oldData = decodeLargeOctetAsnStream(LARGE_OCTET);
        assertEquals(newData, oldData);
    }

    @Test
    public void testEncodeLargeOctetNewEqualsOld() throws Exception {
        byte[] newEnc = encodeLargeOctetBerWriter(KB_DATA);
        byte[] oldEnc = encodeLargeOctetAsnStream(KB_DATA);
        assertEquals(newEnc, oldEnc);
    }

    // ==================================================================
    // PERFORMANCE COMPARISON (with timing output)
    // ==================================================================

    @Test
    public void benchmarkAll() throws Exception {
        System.out.println("\n=== BerCodec Performance Comparison ===");
        System.out.printf("Warmup: %,d iterations, Measure: %,d iterations%n%n",
                WARMUP, ITERATIONS);

        // 1. Simple TLV decode
        bench("Decode simple TLV (BerCursor)", () -> {
            for (int i = 0; i < ITERATIONS; i++) decodeSimpleTlvBerCursor(SIMPLE_TLV);
        }, () -> {
            for (int i = 0; i < ITERATIONS; i++) decodeSimpleTlvAsnStream(SIMPLE_TLV);
        });

        // 2. Nested SEQUENCE decode
        bench("Decode nested SEQUENCE (BerCursor)", () -> {
            for (int i = 0; i < ITERATIONS; i++) decodeNestedSeqBerCursor(NESTED_SEQ);
        }, () -> {
            for (int i = 0; i < ITERATIONS; i++) decodeNestedSeqAsnStream(NESTED_SEQ);
        });

        // 3. Encode INTEGER
        bench("Encode INTEGER(42) (BerWriter)", () -> {
            for (int i = 0; i < ITERATIONS; i++) encodeInt32BerWriter(42);
        }, () -> {
            for (int i = 0; i < ITERATIONS; i++) encodeInt32AsnStream(42);
        });

        // 4. Roundtrip
        bench("Roundtrip INTEGER (BerWriter→Cursor)", () -> {
            for (int i = 0; i < ITERATIONS; i++) roundtripBerWriterToCursor(42);
        }, () -> {
            for (int i = 0; i < ITERATIONS; i++) roundtripAsnStreams(42);
        });

        // 5. Decode large OCTET STRING
        bench("Decode 1KB OCTET STRING (BerCursor)", () -> {
            for (int i = 0; i < ITERATIONS; i++) decodeLargeOctetBerCursor(LARGE_OCTET);
        }, () -> {
            for (int i = 0; i < ITERATIONS; i++) decodeLargeOctetAsnStream(LARGE_OCTET);
        });

        // 6. Encode large OCTET STRING
        bench("Encode 1KB OCTET STRING (BerWriter)", () -> {
            for (int i = 0; i < ITERATIONS; i++) encodeLargeOctetBerWriter(KB_DATA);
        }, () -> {
            for (int i = 0; i < ITERATIONS; i++) encodeLargeOctetAsnStream(KB_DATA);
        });
    }

    // ==================================================================
    // IMPLEMENTATIONS
    // ==================================================================

    private int decodeSimpleTlvBerCursor(byte[] data) {
        try {
            BerCursor c = BerCursor.wrap(data, 0, data.length);
            try { c.readTag(); return c.readInt32(); }
            finally { c.release(); }
        } catch (AsnException e) { throw new RuntimeException(e); }
    }

    private long decodeSimpleTlvAsnStream(byte[] data) {
        try {
            AsnInputStream ais = new AsnInputStream(data);
            ais.readTag();
            return ais.readInteger();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private int decodeNestedSeqBerCursor(byte[] data) {
        try {
            BerCursor c = BerCursor.wrap(data, 0, data.length);
            try {
                c.readTag();
                BerCursor body = c.openConstructed();
                try {
                    int sum = 0;
                    while (body.hasMore()) { body.readTag(); sum += body.readInt32(); }
                    return sum;
                } finally { body.release(); }
            } finally { c.release(); }
        } catch (AsnException e) { throw new RuntimeException(e); }
    }

    private long decodeNestedSeqAsnStream(byte[] data) {
        try {
            AsnInputStream outer = new AsnInputStream(data);
            outer.readTag();
            byte[] seqData = outer.readSequence();
            AsnInputStream body = new AsnInputStream(seqData);
            long sum = 0;
            while (body.available() > 0) {
                body.readTag();
                sum += body.readInteger();
            }
            return sum;
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private byte[] encodeInt32BerWriter(int value) {
        BerWriter w = BerWriter.get();
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, value);
        return w.toByteArray();
    }

    private byte[] encodeInt32AsnStream(int value) {
        try {
            AsnOutputStream aos = new AsnOutputStream();
            aos.writeInteger(value);
            return aos.toByteArray();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private int roundtripBerWriterToCursor(int value) {
        try {
            BerWriter w = BerWriter.get();
            w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, value);
            byte[] enc = w.toByteArray();
            BerCursor c = BerCursor.wrap(enc, 0, enc.length);
            try { c.readTag(); return c.readInt32(); }
            finally { c.release(); }
        } catch (AsnException e) { throw new RuntimeException(e); }
    }

    private long roundtripAsnStreams(int value) {
        try {
            AsnOutputStream aos = new AsnOutputStream();
            aos.writeInteger(value);
            byte[] enc = aos.toByteArray();
            AsnInputStream ais = new AsnInputStream(enc);
            ais.readTag();
            return ais.readInteger();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private byte[] decodeLargeOctetBerCursor(byte[] data) {
        try {
            BerCursor c = BerCursor.wrap(data, 0, data.length);
            try { c.readTag(); return c.getOctetString(); }
            finally { c.release(); }
        } catch (AsnException e) { throw new RuntimeException(e); }
    }

    private byte[] decodeLargeOctetAsnStream(byte[] data) {
        try {
            AsnInputStream ais = new AsnInputStream(data);
            ais.readTag();
            return ais.readOctetString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private byte[] encodeLargeOctetBerWriter(byte[] data) {
        BerWriter w = BerWriter.get();
        w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING, data, 0, data.length);
        return w.toByteArray();
    }

    private byte[] encodeLargeOctetAsnStream(byte[] data) {
        try {
            AsnOutputStream aos = new AsnOutputStream();
            aos.writeOctetString(data);
            return aos.toByteArray();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    // ==================================================================
    // BENCHMARK HELPER
    // ==================================================================

    @FunctionalInterface
    private interface ThrowingRunnable { void run() throws Exception; }

    private static void bench(String name, ThrowingRunnable newPath, ThrowingRunnable oldPath) throws Exception {
        // Warmup
        for (int i = 0; i < WARMUP; i++) newPath.run();
        for (int i = 0; i < WARMUP; i++) oldPath.run();

        // Measure new
        System.gc();
        long t0 = System.nanoTime();
        newPath.run();
        long newNs = System.nanoTime() - t0;

        // Measure old
        System.gc();
        t0 = System.nanoTime();
        oldPath.run();
        long oldNs = System.nanoTime() - t0;

        double speedup = (double) oldNs / newNs;
        System.out.printf("%-55s | New: %,7d μs | Old: %,7d μs | %.2fx %s%n",
                name,
                newNs / 1000, oldNs / 1000,
                speedup >= 1.0 ? speedup : 1.0 / speedup,
                speedup >= 1.0 ? "FASTER" : "SLOWER");
    }
    // ==================================================================
    // BER WRITER POOL TIER BENCHMARK — TINY/SMALL/MEDIUM vs 8KB Default
    // ==================================================================

    @Test
    public void benchmarkWriterPoolTiers() throws Exception {
        int iters = 50_000;
        int warmup = 5_000;
        System.out.println("\n=== BerWriterPool Tier Comparison ===");
        System.out.printf("Iterations: %,d, Warmup: %,d\n\n", iters, warmup);

        // Tier names and sizes
        String[] names = {"TINY (256B)", "SMALL (1KB)", "MEDIUM (4KB)", "LARGE (16KB)", "XLARGE (64KB)", "DEFAULT (8KB TL)"};
        int[] tiers = {BerWriterPool.TINY, BerWriterPool.SMALL, BerWriterPool.MEDIUM, BerWriterPool.LARGE, BerWriterPool.XLARGE, -1};

        // Benchmark 1: small payload (single INTEGER = 3 bytes)
        System.out.println("--- Single INTEGER(42) ---");
        for (int t = 0; t < tiers.length; t++) {
            // Warmup
            for (int i = 0; i < warmup; i++) {
                BerWriter w = tiers[t] >= 0 ? BerWriter.get(tiers[t]) : BerWriter.get();
                w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 42);
            }
            System.gc();
            Thread.sleep(20);
            long t0 = System.nanoTime();
            for (int i = 0; i < iters; i++) {
                BerWriter w = tiers[t] >= 0 ? BerWriter.get(tiers[t]) : BerWriter.get();
                w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 42);
            }
            long ns = System.nanoTime() - t0;
            System.out.printf("  %-18s: %,8.0f μs | %,.1f ns/op\n", names[t], ns / 1000.0, ns / (double) iters);
        }

        // Benchmark 2: medium payload (SEQUENCE with 5 INTs + 100B OCTET)
        System.out.println("\n--- SEQUENCE { 5xINT + 100B OCTET } ---");
        byte[] octet100 = new byte[100];
        for (int i = 0; i < 100; i++) octet100[i] = (byte) i;

        for (int t = 0; t < tiers.length; t++) {
            for (int i = 0; i < warmup; i++) {
                BerWriter w = tiers[t] >= 0 ? BerWriter.get(tiers[t]) : BerWriter.get();
                int mark = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
                // BerWriter backward: write in reverse order
                w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING, octet100, 0, 100);
                for (int j = 5; j >= 1; j--) w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, j * 100);
                w.endSequence(mark, BerTag.UNIVERSAL, BerTag.SEQUENCE);
            }
            System.gc();
            Thread.sleep(20);
            long t0 = System.nanoTime();
            for (int i = 0; i < iters; i++) {
                BerWriter w = tiers[t] >= 0 ? BerWriter.get(tiers[t]) : BerWriter.get();
                int mark = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
                w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING, octet100, 0, 100);
                for (int j = 5; j >= 1; j--) w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, j * 100);
                w.endSequence(mark, BerTag.UNIVERSAL, BerTag.SEQUENCE);
            }
            long ns = System.nanoTime() - t0;
            System.out.printf("  %-18s: %,8.0f μs | %,.1f ns/op\n", names[t], ns / 1000.0, ns / (double) iters);
        }

        // Benchmark 3: large payload (1024 B OCTET STRING)
        System.out.println("\n--- 1KB OCTET STRING ---");
        byte[] kb = new byte[1024];
        for (int i = 0; i < 1024; i++) kb[i] = (byte) (i & 0xFF);

        for (int t = 0; t < tiers.length; t++) {
            for (int i = 0; i < warmup; i++) {
                BerWriter w = tiers[t] >= 0 ? BerWriter.get(tiers[t]) : BerWriter.get();
                w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING, kb, 0, 1024);
            }
            System.gc();
            Thread.sleep(20);
            long t0 = System.nanoTime();
            for (int i = 0; i < iters; i++) {
                BerWriter w = tiers[t] >= 0 ? BerWriter.get(tiers[t]) : BerWriter.get();
                w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING, kb, 0, 1024);
            }
            long ns = System.nanoTime() - t0;
            System.out.printf("  %-18s: %,8.0f μs | %,.1f ns/op\n", names[t], ns / 1000.0, ns / (double) iters);
        }
    }

}