package org.restcomm.protocols.ss7.tcap.asn.comp;

import org.mobicents.protocols.asn.*;

/**
 * OperationCode ::= CHOICE {
 *     localValue   [0] IMPLICIT INTEGER,
 *     globalValue  [1] IMPLICIT OBJECT IDENTIFIER
 * }
 *
 * <p>Encode/Decode zero-copy via BerCursor/BerWriter.
 */
public final class OperationCodeCodec {

    public enum Type { LOCAL, GLOBAL }

    /** Kết quả decode — reusable */
    public static final class Result {
        public Type     type;
        public int      localValue;   // nếu LOCAL
        public BerSlice globalValue;  // nếu GLOBAL (lazy OID bytes)

        public void reset() {
            type        = null;
            localValue  = 0;
            globalValue = null;
        }
    }

    // CHOICE alternatives
    private static final int TAG_LOCAL  = 0x00; // [0] IMPLICIT INTEGER
    private static final int TAG_GLOBAL = 0x01; // [1] IMPLICIT OID

    // ── DECODE ───────────────────────────────────────────────────────

    /**
     * Decode OperationCode CHOICE.
     * Caller đã gọi cursor.readTag() trước khi gọi hàm này.
     */
    public static void decode(BerCursor cursor, Result out)
            throws AsnException {
        if (cursor.tagClass() != BerTag.CONTEXT) {
            throw new AsnException("OperationCode: expected CONTEXT tag, " +
                "got class=" + cursor.tagClass() + " tag=" + cursor.tag());
        }
        switch (cursor.tag()) {
            case TAG_LOCAL:
                out.type       = Type.LOCAL;
                out.localValue = cursor.readInt32();
                break;
            case TAG_GLOBAL:
                out.type        = Type.GLOBAL;
                out.globalValue = cursor.getOctetStringSlice(); // lazy OID
                break;
            default:
                throw new AsnException("OperationCode: unknown tag=" + cursor.tag());
        }
        cursor.skipValue();
    }

    // ── ENCODE ───────────────────────────────────────────────────────

    /** Encode local OperationCode — [0] IMPLICIT INTEGER */
    public static void encodeLocal(BerWriter w, int localValue) {
        // CHOICE: không có wrapper, ghi trực tiếp
        w.writeInt32(BerTag.CONTEXT, TAG_LOCAL, localValue);
    }

    /** Encode global OperationCode — [1] IMPLICIT OID */
    public static void encodeGlobal(BerWriter w, byte[] oidEncoded) {
        w.writeOctetString(BerTag.CONTEXT, TAG_GLOBAL,
            oidEncoded, 0, oidEncoded.length);
    }

    /** Encode từ Result */
    public static void encode(BerWriter w, Result result) {
        if (result.type == Type.LOCAL) {
            encodeLocal(w, result.localValue);
        } else {
            encodeGlobal(w, result.globalValue.toByteArray());
        }
    }

    private OperationCodeCodec() {}
}
