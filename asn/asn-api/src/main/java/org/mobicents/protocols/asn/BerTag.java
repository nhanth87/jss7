package org.mobicents.protocols.asn;

/**
 * BER/DER tag class and universal tag constants.
 * Used by {@link BerCursor}, {@link BerWriter}, and all higher-level codecs.
 */
public final class BerTag {
    private BerTag() {}

    // ── Tag classes ──────────────────────────────────────────────────
    public static final int UNIVERSAL   = 0x00;
    public static final int APPLICATION = 0x01;
    public static final int CONTEXT     = 0x02;
    public static final int PRIVATE     = 0x03;

    // ── Universal primitive tags ─────────────────────────────────────
    public static final int BOOLEAN          = 0x01;
    public static final int INTEGER          = 0x02;
    public static final int BIT_STRING       = 0x03;
    public static final int OCTET_STRING     = 0x04;
    public static final int NULL             = 0x05;
    public static final int OID              = 0x06;
    public static final int ENUMERATED       = 0x0A;
    public static final int UTF8_STRING      = 0x0C;
    public static final int PRINTABLE_STRING = 0x13;
    public static final int IA5_STRING       = 0x16;

    // ── Universal constructed tags ───────────────────────────────────
    public static final int SEQUENCE         = 0x10;
    public static final int SET              = 0x11;

    // ── Helpers: wire byte (tagClass | constructed | tag) ───────────
    public static byte universalPrimitive(int tag) {
        return (byte) (UNIVERSAL << 6 | tag);
    }

    public static byte universalConstructed(int tag) {
        return (byte) (UNIVERSAL << 6 | 0x20 | tag);
    }

    public static byte contextPrimitive(int tag) {
        return (byte) (CONTEXT << 6 | tag);
    }

    public static byte contextConstructed(int tag) {
        return (byte) (CONTEXT << 6 | 0x20 | tag);
    }
}
