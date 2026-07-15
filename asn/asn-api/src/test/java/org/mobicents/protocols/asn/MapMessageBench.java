package org.mobicents.protocols.asn;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * 1M ops benchmark: nested SEQUENCE + OPTIONAL fields
 * Realistic MAP USSD message: TCAP-Begin with optional dialoguePortion
 *
 * v2 fix: after openConstructed() on invoke SEQUENCE, call parent.skipValue()
 * to advance past the TLV. Without this, parent hasMore() returns true and
 * re-reads invoke content as outer TLVs, hitting invalid BER bytes.
 */
public class MapMessageBench {
    static final int ITERS = 1_000_000;
    static final int WARMUP = 5_000;

    private byte[] encodeMapMessage(boolean withDp) {
        byte[] ussd = new byte[160];
        for (int i = 0; i < 160; i++) ussd[i] = (byte) i;

        BerWriter w = BerWriter.get(512);
        int outer = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);

        // invoke SEQUENCE (always)
        int inv = w.beginSequence(BerTag.UNIVERSAL, BerTag.SEQUENCE);
        w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING, ussd, 0, 160);
        w.writeOID(BerTag.UNIVERSAL, BerTag.OID, new int[]{0,0,17,773,1,1,1});
        w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 42);
        w.endSequence(inv, BerTag.UNIVERSAL, BerTag.SEQUENCE);

        // dialoguePortion [0] EXPLICIT (OPTIONAL)
        if (withDp) {
            int dp = w.beginSequence(BerTag.CONTEXT, 0);
            w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 1);
            w.writeOID(BerTag.UNIVERSAL, BerTag.OID, new int[]{0,0,17,773});
            w.endSequence(dp, BerTag.CONTEXT, 0);
        }
        w.endSequence(outer, BerTag.UNIVERSAL, BerTag.SEQUENCE);
        return w.toByteArray();
    }

    private int decodeMapMessage(byte[] data) {
        try {
            BerCursor c = BerCursor.wrapHeap(data, 0, data.length);
            c.readTag();
            BerCursor outer = c.openConstructed();
            int invokeId = -1;
            while (outer.hasMore()) {
                outer.readTag();
                if (outer.tagClass() == BerTag.CONTEXT && outer.tag() == 0) {
                    outer.skipValue(); // dialoguePortion — skip past whole TLV
                } else if (!outer.isPrimitive()) {
                    BerCursor inv = outer.openConstructed();
                    inv.readTag(); invokeId = inv.readInt32();
                    inv.readTag(); inv.skipValue();
                    inv.readTag(); inv.getValueSlice();
                    inv.release();
                    outer.skipValue(); // ← FIX: advance outer past invoke SEQUENCE TLV
                }
            }
            c.release(); outer.release();
            return invokeId;
        } catch (AsnException e) {
            throw new RuntimeException(e);
        }
    }

    // ── Test 1: Decode only (pre-built bytes) ──
    @Test
    public void benchDecodeOnly() throws Exception {
        byte[] withDp = encodeMapMessage(true);
        byte[] noDp = encodeMapMessage(false);

        System.out.printf("Encoded size: withDp=%d bytes, noDp=%d bytes%n", withDp.length, noDp.length);

        // warmup
        for (int i = 0; i < WARMUP; i++) {
            int id = decodeMapMessage((i & 1) == 0 ? withDp : noDp);
            assertEquals(id, 42, "decode failed at warmup " + i);
        }
        System.gc(); Thread.sleep(50);

        long t0 = System.nanoTime();
        for (int i = 0; i < ITERS; i++) {
            int id = decodeMapMessage((i & 1) == 0 ? withDp : noDp);
            assertEquals(id, 42, "decode failed at iter " + i);
        }
        long ns = System.nanoTime() - t0;
        double nsPerOp = ns / (double) ITERS;
        double opsPerSec = ITERS * 1e9 / ns;

        System.out.printf("%n=== DECODE ONLY: MAP USSD (1M ops) ===%n");
        System.out.printf("Latency:    %,.0f ns/op (%.1f us/op)%n", nsPerOp, nsPerOp / 1000);
        System.out.printf("Throughput: %,.0f ops/s%n", opsPerSec);
        System.out.printf("4x overhead: %,.0f TPS%n", opsPerSec / 4);
        System.out.printf("2x overhead: %,.0f TPS (1M TPS needs %.0f threads)%n",
                opsPerSec / 2, 2e6 / opsPerSec);
    }

    // ── Test 2: Full roundtrip (encode + decode) ──
    @Test
    public void benchRoundtrip() throws Exception {
        // warmup encode
        for (int i = 0; i < WARMUP; i++) encodeMapMessage((i & 1) == 0);
        // warmup decode
        byte[] ref = encodeMapMessage(true);
        for (int i = 0; i < WARMUP; i++) decodeMapMessage(ref);

        System.gc(); Thread.sleep(50);

        long t0 = System.nanoTime();
        int totalBytes = 0;
        for (int i = 0; i < ITERS; i++) {
            byte[] enc = encodeMapMessage((i & 1) == 0);
            totalBytes += enc.length;
            int id = decodeMapMessage(enc);
            assertEquals(id, 42, "roundtrip failed at iter " + i);
        }
        long ns = System.nanoTime() - t0;
        double nsPerOp = ns / (double) ITERS;
        double opsPerSec = ITERS * 1e9 / ns;

        System.out.printf("%n=== ROUNDTRIP: MAP USSD (1M ops) ===%n");
        System.out.printf("Latency:    %,.0f ns/op (%.1f us/op)%n", nsPerOp, nsPerOp / 1000);
        System.out.printf("Throughput: %,.0f ops/s (encode+decode)%n", opsPerSec);
        System.out.printf("Avg size:   %d bytes%n", totalBytes / ITERS);
        System.out.printf("4x overhead: %,.0f TPS%n", opsPerSec / 4);
        System.out.printf("2x overhead: %,.0f TPS (1M TPS needs %.0f threads)%n",
                opsPerSec / 2, 2e6 / opsPerSec);
    }
}
