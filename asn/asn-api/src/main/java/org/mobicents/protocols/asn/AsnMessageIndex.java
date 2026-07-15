/*
 * RestComm jSS7 - Flat ASN.1 Message Index
 *
 * Zero-GC metadata pool: primitive int[] arrays record TLV coordinates
 * without allocating per-tag Java objects.
 */
package org.mobicents.protocols.asn;

import io.netty.buffer.ByteBuf;

/**
 * Flat index of BER/DER TLV tags in a raw byte buffer or {@link ByteBuf} slice.
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

    /** Heap backing for legacy paths and tests. */
    public byte[] rawBuffer;

    /** Zero-copy backing; when non-null, {@link #valueOffsets} are absolute indices in this buffer. */
    private ByteBuf rawBuf;

    public AsnMessageIndex() {
        clearLookupArrays();
    }

    public void reset(byte[] buffer) {
        this.rawBuffer = buffer;
        this.rawBuf = null;
        this.tagCount = 0;
        this.rootFirstChild = -1;
        this.rootLastChild = -1;
        clearLookupArrays();
    }

    /**
     * Bind index to a {@link ByteBuf} region without copying payload bytes.
     * {@code offset} and indexed TLV coordinates are absolute positions in {@code buffer}.
     */
    public void reset(ByteBuf buffer, int offset, int length) {
        if (buffer == null || !buffer.isReadable()) {
            throw new IllegalArgumentException("ByteBuf must be non-null and readable");
        }
        if (offset < 0 || length < 0 || offset + length > buffer.capacity()) {
            throw new IllegalArgumentException("Invalid ByteBuf slice bounds");
        }
        this.rawBuf = buffer;
        this.rawBuffer = null;
        this.tagCount = 0;
        this.rootFirstChild = -1;
        this.rootLastChild = -1;
        clearLookupArrays();
    }

    public boolean isByteBufBacked() {
        return this.rawBuf != null;
    }

    public ByteBuf getRawBuf() {
        return this.rawBuf;
    }

    public int byteAt(int absoluteOffset) {
        if (this.rawBuf != null) {
            return this.rawBuf.getUnsignedByte(absoluteOffset);
        }
        if (this.rawBuffer == null) {
            throw new IllegalStateException("AsnMessageIndex has no backing buffer");
        }
        return this.rawBuffer[absoluteOffset] & 0xFF;
    }

    /**
     * Returns a retained slice over the indexed tag value bytes (caller must release).
     */
    public ByteBuf valueSlice(int tagIndex) {
        int off = this.valueOffsets[tagIndex];
        int len = this.valueLengths[tagIndex];
        if (this.rawBuf != null) {
            return this.rawBuf.retainedSlice(off, len);
        }
        return io.netty.buffer.Unpooled.wrappedBuffer(this.rawBuffer, off, len);
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
