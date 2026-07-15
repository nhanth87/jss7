package org.mobicents.protocols.asn;

/**
 * Descriptor cho một CHOICE alternative.
 * Dùng để build dispatch table không cần if-else chain.
 *
 * <p>CHOICE trong BER:
 * <ul>
 *   <li>Không có wrapper TLV</li>
 *   <li>Tag của chosen alternative = tag của CHOICE trên wire</li>
 *   <li>Encode = encode alternative trực tiếp (không thêm tag ngoài)</li>
 *   <li>Decode = readTag() rồi dispatch theo (tagClass, tag)</li>
 * </ul>
 */
public final class BerChoice {
    public final int     tagClass;
    public final int     tag;
    public final boolean primitive;
    public final String  name;

    public BerChoice(int tagClass, boolean primitive, int tag, String name) {
        this.tagClass  = tagClass;
        this.primitive = primitive;
        this.tag       = tag;
        this.name      = name;
    }

    /** Kiểm tra cursor hiện tại có khớp alternative này không */
    public boolean matches(BerCursor cursor) {
        return cursor.tagClass() == tagClass
            && cursor.tag()      == tag;
    }

    // ── Factory helpers ──────────────────────────────────────────────
    public static BerChoice contextPrimitive(int tag, String name) {
        return new BerChoice(BerTag.CONTEXT, true, tag, name);
    }
    public static BerChoice contextConstructed(int tag, String name) {
        return new BerChoice(BerTag.CONTEXT, false, tag, name);
    }
    public static BerChoice universalPrimitive(int tag, String name) {
        return new BerChoice(BerTag.UNIVERSAL, true, tag, name);
    }
    public static BerChoice applicationPrimitive(int tag, String name) {
        return new BerChoice(BerTag.APPLICATION, true, tag, name);
    }
    public static BerChoice applicationConstructed(int tag, String name) {
        return new BerChoice(BerTag.APPLICATION, false, tag, name);
    }

    @Override
    public String toString() {
        return name + "([" + tagClass + "] " + (primitive ? "P" : "C")
            + " tag=" + tag + ")";
    }
}
