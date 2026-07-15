package org.restcomm.protocols.ss7.tcap.asn.comp;

import org.mobicents.protocols.asn.*;

/**
 * ErrorCode ::= CHOICE {
 *     localValue   [0] IMPLICIT INTEGER,
 *     globalValue  [1] IMPLICIT OBJECT IDENTIFIER
 * }
 */
public final class ErrorCodeCodec {

    public enum Type { LOCAL, GLOBAL }

    public static final class Result {
        public Type     type;
        public int      localValue;
        public BerSlice globalValue;

        public void reset() { type = null; localValue = 0; globalValue = null; }
    }

    private static final int TAG_LOCAL  = 0x00;
    private static final int TAG_GLOBAL = 0x01;

    // ── DECODE ───────────────────────────────────────────────────────

    public static void decode(BerCursor cursor, Result out)
            throws AsnException {
        if (cursor.tagClass() != BerTag.CONTEXT)
            throw new AsnException("ErrorCode: expected CONTEXT");
        switch (cursor.tag()) {
            case TAG_LOCAL:
                out.type       = Type.LOCAL;
                out.localValue = cursor.readInt32();
                break;
            case TAG_GLOBAL:
                out.type        = Type.GLOBAL;
                out.globalValue = cursor.getOctetStringSlice();
                break;
            default:
                throw new AsnException("ErrorCode: unknown tag=" + cursor.tag());
        }
        cursor.skipValue();
    }

    // ── ENCODE ───────────────────────────────────────────────────────

    public static void encodeLocal(BerWriter w, int value) {
        w.writeInt32(BerTag.CONTEXT, TAG_LOCAL, value);
    }

    public static void encodeGlobal(BerWriter w, byte[] oidEncoded) {
        w.writeOctetString(BerTag.CONTEXT, TAG_GLOBAL,
            oidEncoded, 0, oidEncoded.length);
    }

    private ErrorCodeCodec() {}
}
