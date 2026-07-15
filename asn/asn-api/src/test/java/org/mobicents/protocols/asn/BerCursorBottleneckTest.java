package org.mobicents.protocols.asn;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Deep-dive micro-benchmarks isolating BerCursor performance bottlenecks.
 *
 * <p>Tests each bottleneck in isolation:
 * <ol>
 *   <li>Virtual-call overhead: backend.readByte() vs direct byte[] access</li>
 *   <li>Synchronized pool overhead: wrap/release vs no-pool</li>
 *   <li>Byte-by-byte readInt32 vs bulk read</li>
 *   <li>openConstructed() overhead vs direct offset navigation</li>
 *   <li>getOctetString() copy vs zero-copy slice</li>
 *   <li>Multiple SEQUENCE levels (deep nesting)</li>
 * </ol>
 */
public class BerCursorBottleneckTest {

    private static final int WARMUP = 2000;
    private static final int ITERATIONS = 50000;

    // ── Test data ────────────────────────────────────────────────────

    private static final byte[] TLV_INT_42 = {0x02, 0x01, 0x2A};
    private static final byte[] TLV_INT_99 = {0x02, 0x01, 0x63};

    private static final byte[] NESTED_1L;
    private static final byte[] NESTED_3L;
    private static final byte[] OCTET_50B;
    private static final byte[] OCTET_1K;

    static {
        // Single-level SEQUENCE: {INT(100), INT(200), INT(300)}
        BerWriter w = BerWriter.get();
        int m = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 100);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 200);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 300);
        w.endSequence(m, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        NESTED_1L = w.toByteArray();

        // 3-level deep: SEQ { SEQ { SEQ { INT(99) } } }
        w = BerWriter.get();
        m = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        int m2 = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        int m3 = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 99);
        w.endSequence(m3, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.endSequence(m2, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.endSequence(m, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        NESTED_3L = w.toByteArray();

        // 50-byte OCTET STRING
        OCTET_50B = new byte[2 + 50];
        OCTET_50B[0] = 0x04; OCTET_50B[1] = 50;
        for (int i = 0; i < 50; i++) OCTET_50B[2 + i] = (byte) i;

        // 1KB OCTET STRING
        OCTET_1K = new byte[4 + 1024];
        OCTET_1K[0] = 0x04;
        OCTET_1K[1] = (byte) 0x82; OCTET_1K[2] = 0x04; OCTET_1K[3] = 0x00;
        for (int i = 0; i < 1024; i++) OCTET_1K[4 + i] = (byte) i;
    }

    // ==================================================================
    // BOTTLENECK 1: Virtual-call readByte() vs direct array access
    // ==================================================================

    @Test
    public void bottleneck1_VirtualCallReadByte() throws Exception {
        System.out.println("\n─── Bottleneck 1: Virtual-call readByte() vs direct array access ───");

        // A) BerCursor.readTag() → virtual call via backend.readByte()
        bench("BerCursor.readTag() (virtual call)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                BerCursor c = BerCursor.wrap(TLV_INT_42, 0, TLV_INT_42.length);
                c.readTag();
                c.release();
            }
        });

        // B) AsnInputStream.readTag() → direct buffer[pos++]
        bench("AsnInputStream.readTag() (direct arr)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                AsnInputStream ais = new AsnInputStream(TLV_INT_42);
                ais.readTag();
            }
        });

        // C) SIMULATED: BerCursor-like logic with direct byte[] (no backend)
        bench("Simulated direct byte[] readTag", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                directReadTag(TLV_INT_42);
            }
        });
    }

    /** Simulates BerCursor.readTag() with direct byte[] access */
    private static int directReadTag(byte[] buf) {
        int pos = 0;
        int b = buf[pos++] & 0xFF;
        int tagClass = (b >> 6) & 3;
        boolean primitive = (b & 0x20) == 0;
        int tag = b & 0x1F;
        if (tag == 0x1F) {
            tag = 0;
            do { b = buf[pos++] & 0xFF; tag = (tag << 7) | (b & 0x7F); }
            while ((b & 0x80) != 0);
        }
        b = buf[pos++] & 0xFF;
        if (b > 0x7F) {
            int n = b & 0x7F;
            int vlen = 0;
            for (int j = 0; j < n; j++) vlen = (vlen << 8) | (buf[pos++] & 0xFF);
        }
        return tag;
    }

    // ==================================================================
    // BOTTLENECK 2: Synchronized pool overhead
    // ==================================================================

    @Test
    public void bottleneck2_SynchronizedPool() throws Exception {
        System.out.println("\n─── Bottleneck 2: Synchronized pool wrap/release ───");

        // A) Current: wrap + release with synchronized pool
        bench("BerCursor wrap+release (sync pool)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                BerCursor c = BerCursor.wrap(TLV_INT_42, 0, TLV_INT_42.length);
                c.readTag();
                c.release();
            }
        });

        // B) No pool: new HeapAsnBufferBackend + inline parse (what legacy does with new AsnInputStream())
        // Legacy AsnInputStream for inner sequences does NOT pool — it creates new instances
        bench("new AsnInputStream (no pool, alloc)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                AsnInputStream ais = new AsnInputStream(TLV_INT_42);
                ais.readTag();
            }
        });
    }

    // ==================================================================
    // BOTTLENECK 3: readInt32 byte-by-byte vs bulk
    // ==================================================================

    @Test
    public void bottleneck3_ReadInt32() throws Exception {
        System.out.println("\n─── Bottleneck 3: readInt32 byte-by-byte virtual call vs bulk ───");

        // Build TLV with 4-byte INTEGER
        byte[] tlv = {0x02, 0x04, 0x12, 0x34, 0x56, 0x78};

        // A) BerCursor.readInt32() — byte-by-byte via backend.readByte()
        bench("BerCursor.readInt32() (byte-by-byte)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                BerCursor c = BerCursor.wrap(tlv, 0, tlv.length);
                try { c.readTag(); int v = c.readInt32(); }
                finally { c.release(); }
            }
        });

        // B) AsnInputStream.readInteger() — also byte-by-byte but direct array
        bench("AsnInputStream.readInteger() (dir arr)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                AsnInputStream ais = new AsnInputStream(tlv);
                ais.readTag();
                long v = ais.readInteger();
            }
        });

        // C) Direct bulk merge read
        bench("Direct bulk merge (<< 8 shift)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                int v = directBulkReadInt32(tlv, 2, 4);
            }
        });
    }

    private static int directBulkReadInt32(byte[] data, int offset, int len) {
        int result = 0;
        int end = offset + len;
        for (int i = offset; i < end; i++) result = (result << 8) | (data[i] & 0xFF);
        if (len > 0 && len <= 4 && (data[offset] & 0x80) != 0) result |= (-1 << (len * 8));
        return result;
    }

    // ==================================================================
    // BOTTLENECK 4: openConstructed() overhead
    // ==================================================================

    @Test
    public void bottleneck4_OpenConstructed() throws Exception {
        System.out.println("\n─── Bottleneck 4: openConstructed() vs offset navigation ───");

        // A) BerCursor with openConstructed() (pool allocate)
        bench("BerCursor.openConstructed() (1 level)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                BerCursor c = BerCursor.wrap(NESTED_1L, 0, NESTED_1L.length);
                try {
                    c.readTag();
                    BerCursor body = c.openConstructed();
                    try {
                        while (body.hasMore()) {
                            body.readTag();
                            body.readInt32();
                        }
                    } finally { body.release(); }
                } finally { c.release(); }
            }
        });

        // B) AsnInputStream with readSequenceStreamData (shared buffer, no alloc)
        bench("AsnInputStream.readSequenceStreamData()", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                AsnInputStream outer = new AsnInputStream(NESTED_1L);
                outer.readTag();
                AsnInputStream body = outer.readSequenceStream();
                while (body.available() > 0) {
                    body.readTag();
                    body.readInteger();
                }
            }
        });

        // C) Simulated: direct byte[] offset-based parsing (what BerCursor SHOULD do)
        bench("Direct offset navigation (no sub-cursor)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                directOffsetParse(NESTED_1L);
            }
        });
    }

    private static int directOffsetParse(byte[] buf) {
        int pos = 2; // skip outer SEQ tag+len
        int limit = buf.length;
        int sum = 0;
        while (pos < limit) {
            // readTag inline
            int b = buf[pos++] & 0xFF;
            int tag = b & 0x1F;
            // length
            b = buf[pos++] & 0xFF;
            int vlen = b;
            int voffset = pos;
            pos += vlen;
            // readInt32 inline
            int v = 0;
            for (int j = voffset; j < voffset + vlen; j++) v = (v << 8) | (buf[j] & 0xFF);
            sum += v;
        }
        return sum;
    }

    // ==================================================================
    // BOTTLENECK 5: getOctetString copy vs slice
    // ==================================================================

    @Test
    public void bottleneck5_OctetString() throws Exception {
        System.out.println("\n─── Bottleneck 5: getOctetString (copy) vs getOctetStringSlice (zero-copy) ───");

        // A) getOctetString() — copies to new byte[]
        bench("BerCursor.getOctetString() (50B copy)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                BerCursor c = BerCursor.wrap(OCTET_50B, 0, OCTET_50B.length);
                try { c.readTag(); byte[] v = c.getOctetString(); }
                finally { c.release(); }
            }
        });

        // B) getOctetStringSlice() — zero-copy view
        bench("BerCursor.getOctetStringSlice() (zero-copy)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                BerCursor c = BerCursor.wrap(OCTET_50B, 0, OCTET_50B.length);
                try { c.readTag(); BerSlice v = c.getOctetStringSlice(); }
                finally { c.release(); }
            }
        });

        // C) Legacy AsnInputStream.readOctetString()
        bench("AsnInputStream.readOctetString() (copy)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                AsnInputStream ais = new AsnInputStream(OCTET_50B);
                ais.readTag();
                byte[] v = ais.readOctetString();
            }
        });
    }

    // ==================================================================
    // BOTTLENECK 6: Deep nesting (3 levels)
    // ==================================================================

    @Test
    public void bottleneck6_DeepNesting() throws Exception {
        System.out.println("\n─── Bottleneck 6: Deep nesting (3 levels) ───");

        bench("BerCursor 3-level nest", () -> {
            for (int i = 0; i < ITERATIONS / 5; i++) {
                BerCursor c = BerCursor.wrap(NESTED_3L, 0, NESTED_3L.length);
                try {
                    c.readTag();
                    BerCursor b1 = c.openConstructed();
                    try {
                        b1.readTag();
                        BerCursor b2 = b1.openConstructed();
                        try {
                            b2.readTag();
                            BerCursor b3 = b2.openConstructed();
                            try {
                                b3.readTag();
                                int v = b3.readInt32();
                            } finally { b3.release(); }
                        } finally { b2.release(); }
                    } finally { b1.release(); }
                } finally { c.release(); }
            }
        });

        bench("AsnInputStream 3-level nest", () -> {
            for (int i = 0; i < ITERATIONS / 5; i++) {
                AsnInputStream outer = new AsnInputStream(NESTED_3L);
                outer.readTag();
                AsnInputStream b1 = outer.readSequenceStream();
                b1.readTag();
                AsnInputStream b2 = b1.readSequenceStream();
                b2.readTag();
                AsnInputStream b3 = b2.readSequenceStream();
                b3.readTag();
                long v = b3.readInteger();
            }
        });
    }

    // ==================================================================
    // BOTTLENECK 7: encode→decode roundtrip cost breakdown
    // ==================================================================

    @Test
    public void bottleneck7_RoundtripBreakdown() throws Exception {
        System.out.println("\n─── Bottleneck 7: Roundtrip cost breakdown ───");

        // A) BerWriter.encodeOnly
        bench("BerWriter.encode (INT 42)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                BerWriter w = BerWriter.get();
                w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 42);
                w.toByteArray(); // copy out
            }
        });

        // B) BerCursor.decodeOnly
        bench("BerCursor.decode (INT 42 TLV)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                BerCursor c = BerCursor.wrap(TLV_INT_42, 0, TLV_INT_42.length);
                try { c.readTag(); c.readInt32(); }
                finally { c.release(); }
            }
        });

        // C) Full roundtrip (encode + copy + decode)
        bench("Full roundtrip (encode+copy+decode)", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                BerWriter w = BerWriter.get();
                w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 42);
                byte[] enc = w.toByteArray();
                BerCursor c = BerCursor.wrap(enc, 0, enc.length);
                try { c.readTag(); c.readInt32(); }
                finally { c.release(); }
            }
        });
    }

    // ==================================================================
    // BENCHMARK HELPERS
    // ==================================================================

    @FunctionalInterface
    private interface ThrowingRunnable { void run() throws Exception; }

    private static void bench(String name, ThrowingRunnable task) throws Exception {
        // Warmup
        for (int i = 0; i < WARMUP; i++) task.run();

        // Measure
        System.gc();
        long t0 = System.nanoTime();
        task.run();
        long ns = System.nanoTime() - t0;

        double perOp = (double) ns / ITERATIONS;
        System.out.printf("  %-55s | %,7d μs total | %,7.1f ns/op%n",
                name, ns / 1000, perOp);
    }
}
