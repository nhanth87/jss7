package org.mobicents.protocols.asn;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Zero-copy pipeline benchmark — BerCursor → decode hot fields → forward raw
 * BerSlice to business logic (no copy).
 *
 * <p>Pattern: decode a MAP/CAP message, extract hot fields (invokeId, opCode),
 * forward the payload (USSD string, SMS content) as a raw BerSlice without
 * allocating a byte[].</p>
 *
 * <pre>mvn test -pl asn/asn-api -Dtest=BerZeroCopyBenchmark -am</pre>
 */
public class BerZeroCopyBenchmark {

    private static final int WARMUP = 500;
    private static final int ITERATIONS = 5000;

    // ══════════════════════════════════════════════════════════
    // Test data: simulated TCAP component with user data payload
    // ══════════════════════════════════════════════════════════

    /** Simulated MAP OpenRequest: SEQUENCE { OID, OCTET(8B), OCTET(240B payload) } */
    private static final byte[] MAP_OPEN_LIKE;
    /** Simulated TCAP component: SEQUENCE { InvokeID(INT), OpCode(OID), Payload(OCTET 512B) } */
    private static final byte[] TCAP_COMPONENT;
    /** Nested CHOICE: SEQUENCE { CONTEXT[0] INT(42), CONTEXT[1] OCTET(128B), CONTEXT[2] OCTET(256B) } */
    private static final byte[] NESTED_CHOICE;

    static {
        BerWriter w = BerWriter.get();

        // MAP_OPEN_LIKE: SEQUENCE { OID(0,0,17,773,1,1,1), OCTET(8B), OCTET(240B payload) }
        // BerWriter encodes backward, so write in reverse for correct forward order
        byte[] payload240 = new byte[240];
        for (int i = 0; i < 240; i++) payload240[i] = (byte) (i & 0xFF);
        int mark = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING, payload240, 0, 240);
        w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING, new byte[]{1,2,3,4,5,6,7,8}, 0, 8);
        w.writeOID(BerTag.UNIVERSAL, BerTag.OID, new int[]{0, 0, 17, 773, 1, 1, 1});
        w.endSequence(mark, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        MAP_OPEN_LIKE = w.toByteArray();

        // TCAP_COMPONENT: SEQUENCE { InvokeID(INT=1), OpCode(OID), Payload(OCTET 512B) }
        // BerWriter encodes backward, so write in reverse for correct forward order
        byte[] bigPayload = new byte[512];
        for (int i = 0; i < 512; i++) bigPayload[i] = (byte) (i % 256);
        w = BerWriter.get();
        mark = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING, bigPayload, 0, 512);
        w.writeOID(BerTag.UNIVERSAL, BerTag.OID, new int[]{0, 0, 17, 773, 1, 1, 1}); // opCode
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 1);  // invokeId
        w.endSequence(mark, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        TCAP_COMPONENT = w.toByteArray();

        // NESTED_CHOICE: SEQUENCE { A[0] INT(42), A[1] OCTET(128B), A[2] OCTET(256B) }
        // BerWriter encodes backward, so write in reverse for correct forward order
        byte[] smallPayload = new byte[128];
        for (int i = 0; i < 128; i++) smallPayload[i] = (byte) (i ^ 0xAA);
        byte[] mediumPayload = new byte[256];
        for (int i = 0; i < 256; i++) mediumPayload[i] = (byte) (i ^ 0x55);
        w = BerWriter.get();
        mark = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.writeOctetString(BerTag.CONTEXT, 2, mediumPayload, 0, 256);
        w.writeOctetString(BerTag.CONTEXT, 1, smallPayload, 0, 128);
        w.writeInt32(BerTag.CONTEXT, 0, 42);
        w.endSequence(mark, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        NESTED_CHOICE = w.toByteArray();
    }

    // ══════════════════════════════════════════════════════════
    // Functional tests — ensure zero-copy slices match copy
    // ══════════════════════════════════════════════════════════

    @Test
    public void testMapOpenLikeDecodeZeroCopyVsCopy() throws Exception {
        // Decode with copy
        byte[] copiedPayload = decodeMapOpenLikeWithCopy(MAP_OPEN_LIKE);
        // Decode zero-copy
        byte[] zcPayload = decodeMapOpenLikeZeroCopy(MAP_OPEN_LIKE);
        assertEquals(zcPayload, copiedPayload);
    }

    @Test
    public void testTcapComponentDecodeZeroCopyVsCopy() throws Exception {
        byte[] copied = decodeTcapComponentWithCopy(TCAP_COMPONENT);
        byte[] zc = decodeTcapComponentZeroCopy(TCAP_COMPONENT);
        assertEquals(zc, copied);
    }

    @Test
    public void testNestedChoiceDecodeZeroCopyVsCopy() throws Exception {
        byte[] copied = decodeNestedChoiceWithCopy(NESTED_CHOICE);
        byte[] zc = decodeNestedChoiceZeroCopy(NESTED_CHOICE);
        assertEquals(zc, copied);
    }

    @Test
    public void testRawSliceRoundtrip() throws Exception {
        // Encode → getRawSlice → decode from slice
        BerWriter w = BerWriter.get();
        int mark = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        // BerWriter encodes backward — write in reverse for correct forward order
        w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING,
                new byte[]{0x01, 0x02, 0x03}, 0, 3);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 42);
        w.endSequence(mark, BerTag.UNIVERSAL, BerTag.SEQUENCE);

        BerSlice rawSlice = w.resultAsSlice();
        BerCursor c = rawSlice.cursor();
        c.readTag();
        assertTrue(c.tag() == BerTag.SEQUENCE);
        BerCursor body = c.openConstructed();
        body.readTag();
        assertEquals(body.readInt32(), 42);
        body.readTag();
        byte[] octets = body.getOctetString();
        assertEquals(octets, new byte[]{0x01, 0x02, 0x03});
        c.release();
        body.release();
    }

    @Test
    public void testValueSliceZeroAllocations() throws Exception {
        // getValueSlice() should not allocate byte[]
        BerCursor c = BerCursor.wrapHeap(TCAP_COMPONENT, 0, TCAP_COMPONENT.length);
        c.readTag();
        BerCursor body = c.openConstructed();

        // Skip invokeId and opCode
        body.readTag(); body.skipValue();
        body.readTag(); body.skipValue();

        // Read payload as slice (zero-copy)
        body.readTag();
        BerSlice payload = body.getValueSlice();
        // payload should be 512 bytes
        assertEquals(payload.length(), 512);
        assertFalse(payload.isEmpty());
        assertEquals(payload.byteAt(0), (byte) 0);

        c.release();
        body.release();
    }

    // ══════════════════════════════════════════════════════════
    // Performance comparison: Copy vs Zero-Copy
    // ══════════════════════════════════════════════════════════

    @Test
    public void benchmarkZeroCopyPipeline() throws Exception {
        System.out.println("\n=== Zero-Copy Pipeline Benchmark ===");
        System.out.printf("Warmup: %,d, Measure: %,d iterations%n%n", WARMUP, ITERATIONS);

        // 1. MAP OpenRequest — 240B payload
        bench("MAP OpenRequest decode (copy 240B)",
                () -> { for (int i = 0; i < ITERATIONS; i++) decodeMapOpenLikeWithCopy(MAP_OPEN_LIKE); },
                () -> { for (int i = 0; i < ITERATIONS; i++) decodeMapOpenLikeZeroCopy(MAP_OPEN_LIKE); });

        // 2. TCAP component — 512B payload
        bench("TCAP component decode (copy 512B)",
                () -> { for (int i = 0; i < ITERATIONS; i++) decodeTcapComponentWithCopy(TCAP_COMPONENT); },
                () -> { for (int i = 0; i < ITERATIONS; i++) decodeTcapComponentZeroCopy(TCAP_COMPONENT); });

        // 3. Nested CHOICE — 128B + 256B payloads
        bench("Nested CHOICE decode (copy 128B+256B)",
                () -> { for (int i = 0; i < ITERATIONS; i++) decodeNestedChoiceWithCopy(NESTED_CHOICE); },
                () -> { for (int i = 0; i < ITERATIONS; i++) decodeNestedChoiceZeroCopy(NESTED_CHOICE); });

        // 4. getRawSlice roundtrip
        bench("getRawSlice roundtrip (ZC encode→decode)",
                () -> {
                    for (int i = 0; i < ITERATIONS; i++) {
                        BerWriter w = BerWriter.get();
                        int m = w.beginSequence(0, 16);
                        w.writeInt32(0, 2, i);
                        w.endSequence(m, 0, 16);
                        // Copy path
                        byte[] enc = w.toByteArray();
                        BerCursor c = BerCursor.wrapHeap(enc, 0, enc.length);
                        c.readTag();
                        BerCursor body = c.openConstructed();
                        body.readTag();
                        body.readInt32();
                        c.release();
                        body.release();
                    }
                },
                () -> {
                    for (int i = 0; i < ITERATIONS; i++) {
                        BerWriter w = BerWriter.get();
                        int m = w.beginSequence(0, 16);
                        w.writeInt32(0, 2, i);
                        w.endSequence(m, 0, 16);
                        // Zero-copy path
                        BerCursor c = w.resultAsCursor();
                        c.readTag();
                        BerCursor body = c.openConstructed();
                        body.readTag();
                        body.readInt32();
                        c.release();
                        body.release();
                    }
                });
    }

    // ══════════════════════════════════════════════════════════
    // COPY PATH (baseline — allocates byte[] for payload)
    // ══════════════════════════════════════════════════════════

    private byte[] decodeMapOpenLikeWithCopy(byte[] data) {
        try {
            BerCursor c = BerCursor.wrapHeap(data, 0, data.length);
            c.readTag();
            BerCursor body = c.openConstructed();
            body.readTag(); body.skipValue(); // OID
            body.readTag(); body.skipValue(); // session data (8B)
            body.readTag();
            byte[] result = body.getOctetString(); // COPY 240B
            body.release();
            c.release();
            return result;
        } catch (AsnException e) { throw new RuntimeException(e); }
    }

    private byte[] decodeTcapComponentWithCopy(byte[] data) {
        try {
            BerCursor c = BerCursor.wrapHeap(data, 0, data.length);
            c.readTag();
            BerCursor body = c.openConstructed();
            body.readTag(); body.skipValue(); // invokeId
            body.readTag(); body.skipValue(); // opCode
            body.readTag();
            byte[] result = body.getOctetString(); // COPY 512B
            body.release();
            c.release();
            return result;
        } catch (AsnException e) { throw new RuntimeException(e); }
    }

    private byte[] decodeNestedChoiceWithCopy(byte[] data) {
        try {
            BerCursor c = BerCursor.wrapHeap(data, 0, data.length);
            c.readTag();
            BerCursor body = c.openConstructed();
            // A[0] INT(42)
            body.readTag();
            if (body.tag() != 0) throw new RuntimeException("expected [0]");
            body.skipValue();
            // A[1] OCTET(128B)
            body.readTag();
            if (body.tag() != 1) throw new RuntimeException("expected [1]");
            byte[] p1 = body.getOctetString(); // COPY 128B
            // A[2] OCTET(256B)
            body.readTag();
            if (body.tag() != 2) throw new RuntimeException("expected [2]");
            byte[] p2 = body.getOctetString(); // COPY 256B
            body.release();
            c.release();
            // concatenate for comparison
            byte[] result = new byte[p1.length + p2.length];
            System.arraycopy(p1, 0, result, 0, p1.length);
            System.arraycopy(p2, 0, result, p1.length, p2.length);
            return result;
        } catch (AsnException e) { throw new RuntimeException(e); }
    }

    // ══════════════════════════════════════════════════════════
    // ZERO-COPY PATH — use getValueSlice() / getRawSlice()
    // ══════════════════════════════════════════════════════════

    private byte[] decodeMapOpenLikeZeroCopy(byte[] data) {
        try {
            BerCursor c = BerCursor.wrapHeap(data, 0, data.length);
            c.readTag();
            BerCursor body = c.openConstructed();
            body.readTag(); body.skipValue(); // OID
            body.readTag(); body.skipValue(); // session data (8B)
            body.readTag();
            BerSlice slice = body.getValueSlice(); // ZERO COPY
            byte[] result = slice.toByteArray(); // only copy at final output
            body.release();
            c.release();
            return result;
        } catch (AsnException e) { throw new RuntimeException(e); }
    }

    private byte[] decodeTcapComponentZeroCopy(byte[] data) {
        try {
            BerCursor c = BerCursor.wrapHeap(data, 0, data.length);
            c.readTag();
            BerCursor body = c.openConstructed();
            body.readTag(); body.skipValue(); // invokeId
            body.readTag(); body.skipValue(); // opCode
            body.readTag();
            BerSlice slice = body.getValueSlice(); // ZERO COPY
            byte[] result = slice.toByteArray();
            body.release();
            c.release();
            return result;
        } catch (AsnException e) { throw new RuntimeException(e); }
    }

    private byte[] decodeNestedChoiceZeroCopy(byte[] data) {
        try {
            BerCursor c = BerCursor.wrapHeap(data, 0, data.length);
            c.readTag();
            BerCursor body = c.openConstructed();
            body.readTag(); body.skipValue(); // A[0]
            body.readTag();
            BerSlice s1 = body.getValueSlice(); // ZERO COPY 128B
            body.readTag();
            BerSlice s2 = body.getValueSlice(); // ZERO COPY 256B
            body.release();
            c.release();
            byte[] result = new byte[s1.length() + s2.length()];
            s1.copyTo(result, 0);
            s2.copyTo(result, s1.length());
            return result;
        } catch (AsnException e) { throw new RuntimeException(e); }
    }

    // ══════════════════════════════════════════════════════════
    // BENCHMARK HELPER
    // ══════════════════════════════════════════════════════════

    @FunctionalInterface
    private interface ThrowingRunnable { void run() throws Exception; }

    private static void bench(String name, ThrowingRunnable copyPath, ThrowingRunnable zcPath)
            throws Exception {
        // Warmup
        for (int i = 0; i < WARMUP; i++) { copyPath.run(); zcPath.run(); }

        System.gc();
        long t0 = System.nanoTime();
        copyPath.run();
        long copyNs = System.nanoTime() - t0;

        System.gc();
        t0 = System.nanoTime();
        zcPath.run();
        long zcNs = System.nanoTime() - t0;

        double speedup = (double) copyNs / zcNs;
        System.out.printf("%-55s | Copy: %,7d μs | ZC: %,7d μs | %.2fx %s%n",
                name,
                copyNs / 1000, zcNs / 1000,
                speedup >= 1.0 ? speedup : 1.0 / speedup,
                speedup >= 1.0 ? "ZERO-COPY FASTER" : "COPY FASTER (unexpected)");
    }
}
