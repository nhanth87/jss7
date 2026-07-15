package org.mobicents.protocols.asn;

import io.netty.buffer.ByteBuf;

/**
 * Zero-copy BER/DER backward encoder (Nokalva-style).
 *
 * <p>Strategy:
 * <ul>
 *   <li>Write from the end of the buffer toward the beginning → no need to know
 *       value length in advance.</li>
 *   <li>{@linkplain ThreadLocal} pool → no object allocation on hot paths.</li>
 *   <li>Result is a {@link BerSlice} pointing into the backing buffer (no copy).</li>
 * </ul>
 *
 * <h3>v4 zero-copy roundtrip (fixbercursor.md):</h3>
 * <ul>
 *   <li>{@link #resultAsCursor()} — wrap encoded result into BerCursor, ZERO COPY</li>
 *   <li>{@link #resultAsSlice()} — wrap into BerSlice, ZERO COPY</li>
 *   <li>{@link #encodedLength()} — returns current encoded length without copy</li>
 * </ul>
 *
 * <pre>{@code
 * BerWriter w = BerWriter.get();
 * int mark = w.beginSequence(BerTag.CONTEXT, 1);
 *   w.writeInt32(BerTag.UNIVERSAL, BerTag.INTEGER, 42);
 *   w.writeOctetString(BerTag.UNIVERSAL, BerTag.OCTET_STRING, data, 0, data.length);
 * w.endSequence(mark, BerTag.CONTEXT, 1);
 * BerSlice result = w.resultAsSlice();  // zero-copy
 * BerCursor c = w.resultAsCursor();     // zero-copy roundtrip
 * }</pre>
 */
public final class BerWriter {

    private static final int DEFAULT_CAPACITY = 8192;

    private static final ThreadLocal<BerWriter> TL = ThreadLocal.withInitial(
        () -> new BerWriter(DEFAULT_CAPACITY)
    );

    /** Borrow the thread-local writer (already reset). */
    public static BerWriter get() {
        BerWriter w = TL.get();
        w.reset();
        return w;
    }

    /**
     * Borrow a writer sized for {@code estimatedSize} bytes from the pool.
     * Routes to {@link BerWriterPool#acquire(int)} for tiered allocation.
     */
    public static BerWriter get(int estimatedSize) {
        return BerWriterPool.acquire(estimatedSize);
    }

    /**
     * Borrow a writer with at least {@code minCapacity} bytes.
     * For sizes beyond XLARGE pool tier, allocates one-off writer.
     */
    public static BerWriter getAtLeast(int minCapacity) {
        return BerWriterPool.acquireAtLeast(minCapacity);
    }

    // ── Backing buffer ───────────────────────────────────────────────
    byte[] buf;
    int writePos;   // always decreases on write

    BerWriter(int capacity) {
        this.buf = new byte[capacity];
        this.writePos = capacity;
    }

    void reset() {
        writePos = buf.length;
    }

    private void ensureCapacity(int needed) {
        if (writePos >= needed) return;
        int newCap = buf.length * 2;
        while (newCap - (buf.length - writePos) < needed) newCap *= 2;
        byte[] newBuf = new byte[newCap];
        int offset = newCap - buf.length;
        System.arraycopy(buf, 0, newBuf, offset, buf.length);
        writePos += offset;
        buf = newBuf;
    }

    // ==================================================================
    // LOW-LEVEL WRITE (backward)
    // ==================================================================

    private void writeByte(byte b) {
        ensureCapacity(1);
        buf[--writePos] = b;
    }

    private void writeBytes(byte[] data, int offset, int len) {
        ensureCapacity(len);
        writePos -= len;
        System.arraycopy(data, offset, buf, writePos, len);
    }

    private void writeLength(int len) {
        if (len <= 0x7F) {
            writeByte((byte) len);
        } else if (len <= 0xFF) {
            writeByte((byte) len);
            writeByte((byte) 0x81);
        } else if (len <= 0xFFFF) {
            writeByte((byte) (len & 0xFF));
            writeByte((byte) (len >> 8));
            writeByte((byte) 0x82);
        } else {
            writeByte((byte) (len & 0xFF));
            writeByte((byte) ((len >> 8) & 0xFF));
            writeByte((byte) (len >> 16));
            writeByte((byte) 0x83);
        }
    }

    private void writeTag(int tagClass, boolean primitive, int tag) {
        if (tag <= 30) {
            writeByte((byte) (
                (tagClass << 6) | (primitive ? 0 : 0x20) | tag
            ));
        } else {
            // long-form tag: write LSB to MSB (backward)
            boolean first = true;
            int t = tag;
            while (t > 0) {
                writeByte((byte) ((first ? 0x00 : 0x80) | (t & 0x7F)));
                t >>>= 7;
                first = false;
            }
            writeByte((byte) (
                (tagClass << 6) | (primitive ? 0 : 0x20) | 0x1F
            ));
        }
    }

    // ==================================================================
    // HIGH-LEVEL ENCODE API
    // ==================================================================

    /**
     * Encode INTEGER (ASN.1 universal tag 2).
     * Automatically selects the minimum number of bytes (signed BER integer).
     */
    public void writeInt32(int tagClass, int tag, int value) {
        int snapshot = writePos;
        int v = value;
        writeByte((byte) (v & 0xFF));
        v >>= 8;
        while (v != 0 && v != -1) {
            writeByte((byte) (v & 0xFF));
            v >>= 8;
        }
        int topByte = buf[writePos] & 0xFF;
        if (value >= 0 && (topByte & 0x80) != 0)
            writeByte((byte) 0x00);
        else if (value < 0 && (topByte & 0x80) == 0)
            writeByte((byte) 0xFF);
        int len = snapshot - writePos;
        writeLength(len);
        writeTag(tagClass, true, tag);
    }

    /**
     * Encode LONG (64-bit INTEGER).
     */
    public void writeInt64(int tagClass, int tag, long value) {
        int snapshot = writePos;
        long v = value;
        writeByte((byte) (v & 0xFF));
        v >>= 8;
        while (v != 0L && v != -1L) {
            writeByte((byte) (v & 0xFF));
            v >>= 8;
        }
        int topByte = buf[writePos] & 0xFF;
        if (value >= 0 && (topByte & 0x80) != 0)
            writeByte((byte) 0x00);
        else if (value < 0 && (topByte & 0x80) == 0)
            writeByte((byte) 0xFF);
        int len = snapshot - writePos;
        writeLength(len);
        writeTag(tagClass, true, tag);
    }

    /** Encode BOOLEAN. */
    public void writeBoolean(int tagClass, int tag, boolean value) {
        writeByte(value ? (byte) 0xFF : (byte) 0x00);
        writeLength(1);
        writeTag(tagClass, true, tag);
    }

    /** Encode OCTET STRING from raw {@code byte[]}. */
    public void writeOctetString(int tagClass, int tag,
                                  byte[] data, int offset, int len) {
        writeBytes(data, offset, len);
        writeLength(len);
        writeTag(tagClass, true, tag);
    }

    /** Encode OCTET STRING from a {@link BerSlice} (copies — use raw overload for hot paths). */
    public void writeOctetString(int tagClass, int tag, BerSlice slice) {
        writeOctetString(tagClass, tag,
            slice.toByteArray(), 0, slice.length());
    }

    /** Encode NULL. */
    public void writeNull(int tagClass, int tag) {
        writeLength(0);
        writeTag(tagClass, true, tag);
    }

    /**
     * Encode OBJECT IDENTIFIER.
     * @param oidComponents int[] of arcs (e.g. {0, 0, 17, 773, 1, 1, 1})
     */
    public void writeOID(int tagClass, int tag, int[] oidComponents) {
        int snapshot = writePos;
        // arcs from 3rd onward, encode backward
        for (int i = oidComponents.length - 1; i >= 2; i--) {
            int arc = oidComponents[i];
            writeByte((byte) (arc & 0x7F));
            arc >>>= 7;
            while (arc > 0) {
                writeByte((byte) (0x80 | (arc & 0x7F)));
                arc >>>= 7;
            }
        }
        // first arc: (first * 40) + second
        int first = oidComponents[0] * 40 + oidComponents[1];
        writeByte((byte) (first & 0x7F));
        first >>>= 7;
        while (first > 0) {
            writeByte((byte) (0x80 | (first & 0x7F)));
            first >>>= 7;
        }
        int len = snapshot - writePos;
        writeLength(len);
        writeTag(tagClass, true, tag);
    }

    // ==================================================================
    // CONSTRUCTED TLV (SEQUENCE / SET / context-constructed)
    // ==================================================================

    /**
     * Opens a constructed TLV. Returns a mark to pass to {@link #endSequence(int, int, int)}.
     */
    public int beginSequence(int tagClass, int tag) {
        return writePos;
    }

    /**
     * Closes a constructed TLV, filling the header (tag + length) at the
     * position captured by {@link #beginSequence}.
     */
    public void endSequence(int mark, int tagClass, int tag) {
        int contentLen = mark - writePos;
        writeLength(contentLen);
        writeTag(tagClass, false, tag);
    }

    // ==================================================================
    // RESULT (v4 zero-copy roundtrip)
    // ==================================================================

    /**
     * Returns the complete encoded result as a zero-copy slice backed by a
     * {@link HeapAsnBufferBackend} (preserves existing API compatibility).
     */
    public BerSlice getResult() {
        return new BerSlice(
            new HeapAsnBufferBackend(buf),
            writePos,
            buf.length - writePos
        );
    }

    /**
     * Wrap kết quả encode thành BerCursor để decode ngay — ZERO COPY.
     * Dùng trong roundtrip test / loopback scenario.
     */
    public BerCursor resultAsCursor() {
        return BerCursor.wrapHeap(buf, writePos, buf.length - writePos);
    }

    /**
     * Wrap kết quả thành BerSlice — ZERO COPY, trỏ thẳng vào internal buf.
     */
    public BerSlice resultAsSlice() {
        return new BerSlice(buf, writePos, buf.length - writePos);
    }

    /**
     * Trả về độ dài hiện tại của encoded bytes (không cần copy).
     */
    public int encodedLength() {
        return buf.length - writePos;
    }

    /** Flushes the encoded bytes into a Netty {@link ByteBuf}. */
    public void flushTo(ByteBuf out) {
        out.writeBytes(buf, writePos, buf.length - writePos);
    }

    /** Flushes into a {@link NettyAsnOutputStream}. */
    public void flushTo(NettyAsnOutputStream out) {
        out.write(buf, writePos, buf.length - writePos);
    }

    /** Copies the encoded bytes to a new heap {@code byte[]}. */
    public byte[] toByteArray() {
        int len = buf.length - writePos;
        byte[] result = new byte[len];
        System.arraycopy(buf, writePos, result, 0, len);
        return result;
    }
}
