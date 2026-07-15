package org.mobicents.protocols.asn;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Realistic micro-benchmarks that simulate actual TCAP/MAP message
 * parsing patterns — dynamic data (not compile-time constants),
 * multiple message variants, typical nesting depths.
 *
 * <p>This avoids JIT constant-folding artifacts by using pre-built
 * but randomly-indexed data arrays.</p>
 */
public class BerCursorRealisticBenchmark {

    private static final int WARMUP = 5000;
    private static final int ITERATIONS = 100000;
    private static final int DATA_VARIANTS = 16;

    // ── Multiple data variants (avoid constant folding) ──────────────

    private static final byte[][] TLV_VARIANTS = new byte[DATA_VARIANTS][];
    private static final byte[][] SEQ_VARIANTS = new byte[DATA_VARIANTS][];
    private static final byte[][] OCTET_VARIANTS = new byte[DATA_VARIANTS][];
    private static final byte[][] DEEP_VARIANTS = new byte[DATA_VARIANTS][];

    static {
        for (int v = 0; v < DATA_VARIANTS; v++) {
            int val = 1 + v;
            TLV_VARIANTS[v] = new byte[]{0x02, 0x01, (byte) val};

            // SEQUENCE with 3 INTs
            BerWriter w = BerWriter.get();
            int m = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
            w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, val);
            w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, val + 100);
            w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, val + 200);
            w.endSequence(m, BerTag.UNIVERSAL, BerTag.SEQUENCE);
            SEQ_VARIANTS[v] = w.toByteArray();

            // 20-byte OCTET STRING
            OCTET_VARIANTS[v] = new byte[2 + 20];
            OCTET_VARIANTS[v][0] = 0x04;
            OCTET_VARIANTS[v][1] = 20;
            for (int i = 0; i < 20; i++) OCTET_VARIANTS[v][2 + i] = (byte) (i + v);

            // 3-level nest
            w = BerWriter.get();
            m = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
            int m2 = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
            int m3 = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
            w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, val);
            w.endSequence(m3, BerTag.UNIVERSAL, BerTag.SEQUENCE);
            w.endSequence(m2, BerTag.UNIVERSAL, BerTag.SEQUENCE);
            w.endSequence(m, BerTag.UNIVERSAL, BerTag.SEQUENCE);
            DEEP_VARIANTS[v] = w.toByteArray();
        }
    }

    // ==================================================================
    // TESTS with dynamic data (no JIT constant folding)
    // ==================================================================

    @Test
    public void decodeSimpleTlvDynamic() throws Exception {
        System.out.println("\n─── Decode simple TLV (dynamic data, 16 variants) ───");

        bench("BerCursor simple TLV", () -> {
            int sum = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                byte[] data = TLV_VARIANTS[i % DATA_VARIANTS];
                BerCursor c = BerCursor.wrap(data, 0, data.length);
                try { c.readTag(); sum += c.readInt32(); }
                finally { c.release(); }
            }
            return sum > 0; // prevent dead-code
        });

        bench("AsnInputStream simple TLV", () -> {
            long sum = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                byte[] data = TLV_VARIANTS[i % DATA_VARIANTS];
                AsnInputStream ais = new AsnInputStream(data);
                ais.readTag();
                sum += ais.readInteger();
            }
            return sum > 0;
        });
    }

    @Test
    public void decodeNestedSeqDynamic() throws Exception {
        System.out.println("\n─── Decode nested SEQUENCE (3 INTs, dynamic data) ───");

        bench("BerCursor nested SEQ", () -> {
            int sum = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                byte[] data = SEQ_VARIANTS[i % DATA_VARIANTS];
                BerCursor c = BerCursor.wrap(data, 0, data.length);
                try {
                    c.readTag();
                    BerCursor body = c.openConstructed();
                    try {
                        while (body.hasMore()) {
                            body.readTag();
                            sum += body.readInt32();
                        }
                    } finally { body.release(); }
                } finally { c.release(); }
            }
            return sum > 0;
        });

        bench("AsnInputStream nested SEQ", () -> {
            long sum = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                byte[] data = SEQ_VARIANTS[i % DATA_VARIANTS];
                AsnInputStream outer = new AsnInputStream(data);
                outer.readTag();
                AsnInputStream body = outer.readSequenceStream();
                while (body.available() > 0) {
                    body.readTag();
                    sum += body.readInteger();
                }
            }
            return sum > 0;
        });
    }

    @Test
    public void decodeOctetStringDynamic() throws Exception {
        System.out.println("\n─── Decode 20B OCTET STRING (dynamic data) ───");

        bench("BerCursor getOctetStringSlice (zero-copy)", () -> {
            int total = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                byte[] data = OCTET_VARIANTS[i % DATA_VARIANTS];
                BerCursor c = BerCursor.wrap(data, 0, data.length);
                try {
                    c.readTag();
                    BerSlice s = c.getOctetStringSlice();
                    total += s.length();
                } finally { c.release(); }
            }
            return total > 0;
        });

        bench("BerCursor getOctetString (copy)", () -> {
            int total = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                byte[] data = OCTET_VARIANTS[i % DATA_VARIANTS];
                BerCursor c = BerCursor.wrap(data, 0, data.length);
                try {
                    c.readTag();
                    byte[] s = c.getOctetString();
                    total += s.length;
                } finally { c.release(); }
            }
            return total > 0;
        });

        bench("AsnInputStream readOctetString", () -> {
            int total = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                byte[] data = OCTET_VARIANTS[i % DATA_VARIANTS];
                AsnInputStream ais = new AsnInputStream(data);
                ais.readTag();
                byte[] s = ais.readOctetString();
                total += s.length;
            }
            return total > 0;
        });
    }

    @Test
    public void decodeDeepNestDynamic() throws Exception {
        System.out.println("\n─── Decode 3-level deep nest (dynamic data) ───");

        bench("BerCursor 3-level", () -> {
            int sum = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                byte[] data = DEEP_VARIANTS[i % DATA_VARIANTS];
                BerCursor c = BerCursor.wrap(data, 0, data.length);
                try {
                    c.readTag();
                    BerCursor l1 = c.openConstructed();
                    try {
                        l1.readTag();
                        BerCursor l2 = l1.openConstructed();
                        try {
                            l2.readTag();
                            BerCursor l3 = l2.openConstructed();
                            try {
                                l3.readTag();
                                sum += l3.readInt32();
                            } finally { l3.release(); }
                        } finally { l2.release(); }
                    } finally { l1.release(); }
                } finally { c.release(); }
            }
            return sum > 0;
        });

        bench("AsnInputStream 3-level", () -> {
            long sum = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                byte[] data = DEEP_VARIANTS[i % DATA_VARIANTS];
                AsnInputStream a0 = new AsnInputStream(data);
                a0.readTag();
                AsnInputStream a1 = a0.readSequenceStream();
                a1.readTag();
                AsnInputStream a2 = a1.readSequenceStream();
                a2.readTag();
                AsnInputStream a3 = a2.readSequenceStream();
                a3.readTag();
                sum += a3.readInteger();
            }
            return sum > 0;
        });
    }

    @Test
    public void encodeDynamic() throws Exception {
        System.out.println("\n─── Encode (dynamic values) ───");

        bench("BerWriter encode INT", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                BerWriter w = BerWriter.get();
                w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, i % 256);
                byte[] enc = w.toByteArray();
            }
            return true;
        });

        bench("AsnOutputStream encode INT", () -> {
            for (int i = 0; i < ITERATIONS; i++) {
                AsnOutputStream aos = new AsnOutputStream();
                aos.writeInteger(i % 256);
                byte[] enc = aos.toByteArray();
            }
            return true;
        });
    }

    @Test
    public void roundtripDynamic() throws Exception {
        System.out.println("\n─── Roundtrip encode→decode (dynamic values) ───");

        bench("BerWriter→BerCursor roundtrip", () -> {
            int sum = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                BerWriter w = BerWriter.get();
                w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, i % 1000);
                byte[] enc = w.toByteArray();
                BerCursor c = BerCursor.wrap(enc, 0, enc.length);
                try { c.readTag(); sum += c.readInt32(); }
                finally { c.release(); }
            }
            return sum > 0;
        });

        bench("AsnStreams roundtrip", () -> {
            long sum = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                AsnOutputStream aos = new AsnOutputStream();
                aos.writeInteger(i % 1000);
                byte[] enc = aos.toByteArray();
                AsnInputStream ais = new AsnInputStream(enc);
                ais.readTag();
                sum += ais.readInteger();
            }
            return sum > 0;
        });
    }

    // ==================================================================
    // HELPERS
    // ==================================================================

    @FunctionalInterface
    private interface BoolSupplier { boolean get() throws Exception; }

    private static void bench(String name, BoolSupplier task) throws Exception {
        // Warmup
        for (int i = 0; i < WARMUP; i++) task.get();

        // Measure
        System.gc();
        long t0 = System.nanoTime();
        boolean consumed = task.get();
        long ns = System.nanoTime() - t0;

        double perOp = (double) ns / ITERATIONS;
        System.out.printf("  %-50s | %,7d μs total | %,7.1f ns/op %s%n",
                name, ns / 1000, perOp,
                consumed ? "" : "(dead)");
    }
}
