/*
 * RestComm jSS7 - Flat ASN.1 Message Index
 *
 * Zero-GC metadata pool: primitive int[] arrays record TLV coordinates
 * without allocating per-tag Java objects.
 */
package org.mobicents.protocols.asn;

/**
 * Flat index of BER/DER TLV tags in a raw byte buffer.
 * Instances are intended for reuse via {@link AsnIndexPool}.
 */
public class AsnMessageIndex {

    public static final int MAX_TAGS = 512;
    public static final int MAX_DEPTH = 64;
    public static final int STRIDE = 6;

    /** Interleaved entry field offsets (Phase 3 cache-line layout). */
    public static final int E_TAG = 0;
    public static final int E_TAG_NUM = 1;
    public static final int E_OFF = 2;
    public static final int E_LEN = 3;
    public static final int E_PARENT = 4;
    public static final int E_DEPTH = 5;

    public final int[] entries = new int[MAX_TAGS * STRIDE];

    /** Legacy parallel arrays kept for backward compatibility; synced with {@link #entries}. */
    public final int[] tags = new int[MAX_TAGS];
    public final int[] valueOffsets = new int[MAX_TAGS];
    public final int[] valueLengths = new int[MAX_TAGS];
    public final int[] parents = new int[MAX_TAGS];
    public final int[] depths = new int[MAX_TAGS];

    public final int[] tagNumbers = new int[MAX_TAGS];
    public final int[] firstOccurrence = new int[256];
    public final int[] firstChild = new int[MAX_TAGS];
    public final int[] nextSibling = new int[MAX_TAGS];
    public final int[] lastChild = new int[MAX_TAGS];
    public final int[] parentStack = new int[MAX_DEPTH];

    /** Head of root-level (parent {@code -1}) sibling linked list. */
    public int rootFirstChild = -1;
    /** Tail pointer used only while building root sibling list during parse. */
    public int rootLastChild = -1;

    public int tagCount = 0;
    public byte[] rawBuffer;

    public AsnMessageIndex() {
        clearLookupArrays();
    }

    public void reset(byte[] buffer) {
        this.rawBuffer = buffer;
        this.tagCount = 0;
        this.rootFirstChild = -1;
        this.rootLastChild = -1;
        clearLookupArrays();
    }

    private void clearLookupArrays() {
        for (int i = 0; i < 256; i++) {
            firstOccurrence[i] = -1;
        }
        for (int i = 0; i < MAX_TAGS; i++) {
            firstChild[i] = -1;
            nextSibling[i] = -1;
            lastChild[i] = -1;
        }
    }
}
