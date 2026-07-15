package org.restcomm.protocols.ss7.map.api.primitives;

import org.mobicents.protocols.asn.*;

/**
 * LegID ::= CHOICE {
 *     sendingSideID   [0] IMPLICIT LegType,   -- SendingSideID
 *     receivingSideID [1] IMPLICIT LegType    -- ReceivingSideID
 * }
 * LegType ::= OCTET STRING (SIZE(1))
 *   -- leg1(0x01), leg2(0x02)
 */
public final class LegIDCodec {

    public enum Side { SENDING, RECEIVING }

    public static final class Result {
        public Side side;
        public int  legType;  // 1=leg1, 2=leg2
        public void reset() { side = null; legType = -1; }
    }

    private static final int TAG_SENDING   = 0x00;
    private static final int TAG_RECEIVING = 0x01;

    // ── DECODE ───────────────────────────────────────────────────────

    public static void decode(BerCursor cursor, Result out)
            throws AsnException {
        if (cursor.tagClass() != BerTag.CONTEXT)
            throw new AsnException("LegID: expected CONTEXT");
        switch (cursor.tag()) {
            case TAG_SENDING:
                out.side    = Side.SENDING;
                out.legType = cursor.readInt32();
                break;
            case TAG_RECEIVING:
                out.side    = Side.RECEIVING;
                out.legType = cursor.readInt32();
                break;
            default:
                throw new AsnException("LegID: unknown tag=" + cursor.tag());
        }
        cursor.skipValue();
    }

    // ── ENCODE ───────────────────────────────────────────────────────

    public static void encodeSending(BerWriter w, int legType) {
        w.writeOctetString(BerTag.CONTEXT, TAG_SENDING,
            new byte[]{(byte) legType}, 0, 1);
    }

    public static void encodeReceiving(BerWriter w, int legType) {
        w.writeOctetString(BerTag.CONTEXT, TAG_RECEIVING,
            new byte[]{(byte) legType}, 0, 1);
    }

    public static void encode(BerWriter w, Result result) {
        if (result.side == Side.SENDING)
            encodeSending(w, result.legType);
        else
            encodeReceiving(w, result.legType);
    }

    private LegIDCodec() {}
}
