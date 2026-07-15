package org.mobicents.protocols.asn;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Zero-copy view into a buffer (heap {@code byte[]} or Netty {@link ByteBuf}).
 * No data is copied when a {@code BerSlice} is created — it is simply an
 * {@code (offset, length)} pair on the original backing store.
 *
 * <h3>v4 extensions (fixbercursor.md):</h3>
 * <ul>
 *   <li>{@link #cursor()} — wrap slice into berCursor for lazy decode (O(1))</li>
 *   <li>{@link #asByteBuf()} — Netty {@code ByteBuf} slice (zero-copy)</li>
 *   <li>{@link #equalsBytes(byte[], int, int)} — comparison without array copy</li>
 * </ul>
 */
public final class BerSlice {
    // ── Heap-fast-path ───────────────────────────────────────────────
    private final byte[]           heapBuf;  // non-null nếu heap
    private final AsnBufferBackend backend;  // non-null nếu ByteBuf
    private final int              offset;
    private final int              length;

    /** Heap path (direct {@code byte[]}, zero-copy). */
    BerSlice(byte[] heapBuf, int offset, int length) {
        this.heapBuf = heapBuf;
        this.backend = null;
        this.offset  = offset;
        this.length  = length;
    }

    /** ByteBuf path via {@link AsnBufferBackend}. */
    BerSlice(AsnBufferBackend backend, int offset, int length) {
        this.heapBuf = null;
        this.backend = backend;
        this.offset  = offset;
        this.length  = length;
    }

    public int length() { return length; }
    public int offset() { return offset; }

    /** Returns true if this slice has zero length. */
    public boolean isEmpty() { return length == 0; }

    /** Read a single byte at the given index (0-based, relative to offset). */
    public byte byteAt(int index) {
        if (index < 0 || index >= length) throw new IndexOutOfBoundsException(index);
        if (heapBuf != null)
            return heapBuf[offset + index];
        return (byte) backend.readByte(offset + index);
    }

    /** Zero-copy factory: wrap an existing heap byte array. */
    public static BerSlice fromHeap(byte[] buf, int offset, int length) {
        return new BerSlice(buf, offset, length);
    }

    /** Copy the referenced bytes to a destination array. */
    public void copyTo(byte[] dest, int destOffset) {
        if (heapBuf != null) {
            System.arraycopy(heapBuf, offset, dest, destOffset, length);
        } else {
            backend.copyTo(offset, dest, destOffset, length);
        }
    }

    // ── Zero-copy lazy decode ─────────────────────────────────────────

    /**
     * Shallow copy — O(1), không copy dữ liệu.
     * Dùng để wrap BerSlice vào BerCursor để tiếp tục decode lazy.
     */
    public BerCursor cursor() {
        if (heapBuf != null)
            return BerCursor.wrapHeap(heapBuf, offset, length);
        return BerCursor.wrapByteBuf(backend, offset, length);
    }

    // ── Copy (only when truly needed) ─────────────────────────────────

    /**
     * Copies the referenced bytes to a new heap {@code byte[]}.
     * Uses {@code System.arraycopy} on heap path, {@code backend.copyTo()} on ByteBuf path.
     */
    public byte[] toByteArray() {
        byte[] out = new byte[length];
        if (heapBuf != null) {
            System.arraycopy(heapBuf, offset, out, 0, length);
        } else {
            backend.copyTo(offset, out, 0, length);
        }
        return out;
    }

    // ── Netty / NIO views ─────────────────────────────────────────────

    /** Returns a Netty {@link ByteBuf} slice over the referenced region. */
    public ByteBuf asByteBuf() {
        if (heapBuf != null) {
            return Unpooled.wrappedBuffer(heapBuf, offset, length);
        }
        return backend.slice(offset, length);
    }

    // Netty ByteBuf is the canonical buffer type — use asByteBuf(). A java.nio
    // ByteBuffer accessor was intentionally removed (see AGENTS.md buffer policy).

    // ── Equality (no array copy) ──────────────────────────────────────

    /** Compares the referenced bytes against a heap {@code byte[]}. */
    public boolean equalsBytes(byte[] other) {
        return equalsBytes(other, 0, other.length);
    }

    /**
     * equalsBytes — không tạo byte[] copy để so sánh.
     */
    public boolean equalsBytes(byte[] other, int otherOffset, int len) {
        if (len != length) return false;
        if (heapBuf != null) {
            for (int i = 0; i < length; i++) {
                if (heapBuf[offset + i] != other[otherOffset + i]) return false;
            }
            return true;
        }
        return backend.equalsBytes(offset, other, otherOffset, len);
    }
}
