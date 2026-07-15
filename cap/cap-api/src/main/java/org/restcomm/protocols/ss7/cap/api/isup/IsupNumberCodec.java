package org.restcomm.protocols.ss7.cap.api.isup;

import org.mobicents.protocols.asn.*;

/**
 * GenericNumbers ::= SET SIZE(1..3) OF GenericNumber
 * GenericNumber  ::= OCTET STRING (SIZE(2..11))
 *   -- Encoded Q.763 Generic Number parameter
 *   -- Byte 0: Number Qualifier Indicator
 *   -- Byte 1: Nature of Address, Numbering Plan, Presentation, Screening
 *   -- Byte 2..n: Address signals (BCD, odd nibble last)
 *
 * CalledPartyNumber ::= OCTET STRING (SIZE(2..12))
 *   -- Encoded Q.763 Called Party Number
 */
public final class IsupNumberCodec {

    // Number Qualifier (byte 0 của GenericNumber)
    public static final int NQI_ADDITIONAL_CALLED  = 0x00;
    public static final int NQI_ADDITIONAL_CALLING = 0x01;
    public static final int NQI_REDIRECTING        = 0x02;
    public static final int NQI_CONNECTED          = 0x03;

    // Nature of Address (bits 6-4 của byte 1)
    public static final int NAI_SUBSCRIBER    = 0x01;
    public static final int NAI_NATIONAL      = 0x03;
    public static final int NAI_INTERNATIONAL = 0x04;

    // Numbering Plan (bits 3-0 của byte 1)
    public static final int NPI_ISDN = 0x01;

    /** Kết quả decode ISUP number */
    public static final class Result {
        public int    nai;         // Nature of Address Indicator
        public int    npi;         // Numbering Plan Indicator
        public String digits;      // BCD digits string
        // CalledPartyNumber thêm:
        public int    inni;        // Internal Network Number Indicator
        public int    apri;        // Address Presentation Restricted Indicator

        public void reset() {
            nai = -1; npi = -1;
            digits = null;
            inni = -1; apri = -1;
        }
    }

    private static final ThreadLocal<Result> TL_RESULT =
        ThreadLocal.withInitial(Result::new);

    // ── DECODE CalledPartyNumber ─────────────────────────────────────

    public static Result decodeCalledPartyNumber(BerSlice slice) {
        Result out = TL_RESULT.get();
        out.reset();
        byte[] raw = slice.toByteArray();
        if (raw.length < 2) return out;
        boolean oddFlag = (raw[0] & 0x80) != 0;
        out.nai = raw[0] & 0x7F;
        out.inni = (raw[1] >> 7) & 0x01;
        out.npi  = (raw[1] >> 4) & 0x07;
        out.digits = decodeBCD(raw, 2, raw.length - 2, oddFlag);
        return out;
    }

    // ── DECODE CallingPartyNumber ────────────────────────────────────

    public static Result decodeCallingPartyNumber(BerSlice slice) {
        Result out = TL_RESULT.get();
        out.reset();
        byte[] raw = slice.toByteArray();
        if (raw.length < 2) return out;
        boolean oddFlag = (raw[0] & 0x80) != 0;
        out.nai  = raw[0] & 0x7F;
        out.npi  = (raw[1] >> 4) & 0x07;
        out.apri = (raw[1] >> 2) & 0x03;
        out.digits = decodeBCD(raw, 2, raw.length - 2, oddFlag);
        return out;
    }

    // ── BCD decode ───────────────────────────────────────────────────

    public static String decodeBCD(byte[] raw, int offset,
                                    int len, boolean odd) {
        StringBuilder sb = new StringBuilder(len * 2);
        for (int i = offset; i < offset + len; i++) {
            int b = raw[i] & 0xFF;
            sb.append((char)('0' + (b & 0x0F)));
            int hi = (b >> 4) & 0x0F;
            if (i < offset + len - 1 || !odd)
                sb.append((char)('0' + hi));
        }
        return sb.toString();
    }

    // ── ENCODE CalledPartyNumber ─────────────────────────────────────

    public static byte[] encodeCalledPartyNumber(int nai, int npi,
                                                  String digits) {
        boolean odd = (digits.length() % 2) != 0;
        byte[] out  = new byte[2 + (digits.length() + 1) / 2];
        out[0] = (byte) ((odd ? 0x80 : 0x00) | (nai & 0x7F));
        out[1] = (byte) ((npi & 0x07) << 4);
        encodeBCD(digits, out, 2);
        return out;
    }

    // ── BCD encode ───────────────────────────────────────────────────

    private static void encodeBCD(String digits, byte[] out, int offset) {
        for (int i = 0; i < digits.length(); i += 2) {
            int lo = digits.charAt(i) - '0';
            int hi = (i + 1 < digits.length())
                ? digits.charAt(i + 1) - '0'
                : 0x0F; // filler
            out[offset + i / 2] = (byte) (lo | (hi << 4));
        }
    }

    private IsupNumberCodec() {}
}
