package org.restcomm.protocols.ss7.map.api.primitives;

import org.mobicents.protocols.asn.*;

/**
 * Ext-BasicServiceCode ::= CHOICE {
 *     ext-BearerService [2] IMPLICIT Ext-BearerServiceCode,
 *     ext-TeleService   [3] IMPLICIT Ext-TeleServiceCode
 * }
 * Ext-BearerServiceCode ::= OCTET STRING (SIZE(1..5))
 * Ext-TeleServiceCode   ::= OCTET STRING (SIZE(1..5))
 */
public final class ExtBasicServiceCodeCodec {

    public enum Type { BEARER_SERVICE, TELE_SERVICE }

    public static final class Result {
        public Type     type;
        public BerSlice codeSlice;  // raw OCTET STRING bytes
        public void reset() { type = null; codeSlice = null; }
    }

    private static final int TAG_BEARER = 0x02;
    private static final int TAG_TELE   = 0x03;

    // ── DECODE ───────────────────────────────────────────────────────

    public static void decode(BerCursor cursor, Result out)
            throws AsnException {
        if (cursor.tagClass() != BerTag.CONTEXT)
            throw new AsnException("Ext-BasicServiceCode: expected CONTEXT");
        switch (cursor.tag()) {
            case TAG_BEARER:
                out.type      = Type.BEARER_SERVICE;
                out.codeSlice = cursor.getOctetStringSlice();
                break;
            case TAG_TELE:
                out.type      = Type.TELE_SERVICE;
                out.codeSlice = cursor.getOctetStringSlice();
                break;
            default:
                throw new AsnException(
                    "Ext-BasicServiceCode: unknown tag=" + cursor.tag());
        }
        cursor.skipValue();
    }

    // ── ENCODE ───────────────────────────────────────────────────────

    public static void encode(BerWriter w, Type type, byte[] code) {
        int tag = (type == Type.BEARER_SERVICE) ? TAG_BEARER : TAG_TELE;
        w.writeOctetString(BerTag.CONTEXT, tag, code, 0, code.length);
    }

    private ExtBasicServiceCodeCodec() {}
}
