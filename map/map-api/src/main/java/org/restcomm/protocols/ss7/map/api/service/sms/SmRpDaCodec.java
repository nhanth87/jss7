package org.restcomm.protocols.ss7.map.api.service.sms;

import org.mobicents.protocols.asn.*;

/**
 * SM-RP-DA ::= CHOICE {
 *     imsi                    [0] IMPLICIT IMSI,
 *     lmsi                    [1] IMPLICIT LMSI,
 *     serviceCentreAddressDA  [4] IMPLICIT AddressString,
 *     noSM-RP-DA              [5] IMPLICIT NULL
 * }
 */
public final class SmRpDaCodec {

    public enum Type { IMSI, LMSI, SC_ADDRESS, NO_DA }

    public static final class Result {
        public Type     type;
        public BerSlice valueSlice;  // raw bytes (IMSI/LMSI/AddressString)
        public void reset() { type = null; valueSlice = null; }
    }

    private static final int TAG_IMSI    = 0x00;
    private static final int TAG_LMSI    = 0x01;
    private static final int TAG_SC_ADDR = 0x04;
    private static final int TAG_NO_DA   = 0x05;

    // ── DECODE ───────────────────────────────────────────────────────

    public static void decode(BerCursor cursor, Result out)
            throws AsnException {
        if (cursor.tagClass() != BerTag.CONTEXT)
            throw new AsnException("SM-RP-DA: expected CONTEXT");
        switch (cursor.tag()) {
            case TAG_IMSI:
                out.type       = Type.IMSI;
                out.valueSlice = cursor.getOctetStringSlice();
                break;
            case TAG_LMSI:
                out.type       = Type.LMSI;
                out.valueSlice = cursor.getOctetStringSlice();
                break;
            case TAG_SC_ADDR:
                out.type       = Type.SC_ADDRESS;
                out.valueSlice = cursor.getOctetStringSlice();
                break;
            case TAG_NO_DA:
                out.type       = Type.NO_DA;
                out.valueSlice = null;
                break;
            default:
                throw new AsnException("SM-RP-DA: unknown tag=" + cursor.tag());
        }
        cursor.skipValue();
    }

    // ── ENCODE ───────────────────────────────────────────────────────

    public static void encode(BerWriter w, Type type, byte[] value) {
        switch (type) {
            case IMSI:
                w.writeOctetString(BerTag.CONTEXT, TAG_IMSI,
                    value, 0, value.length);
                break;
            case LMSI:
                w.writeOctetString(BerTag.CONTEXT, TAG_LMSI,
                    value, 0, value.length);
                break;
            case SC_ADDRESS:
                w.writeOctetString(BerTag.CONTEXT, TAG_SC_ADDR,
                    value, 0, value.length);
                break;
            case NO_DA:
                w.writeNull(BerTag.CONTEXT, TAG_NO_DA);
                break;
        }
    }

    private SmRpDaCodec() {}
}
