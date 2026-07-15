package org.mobicents.protocols.asn;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Heap {@code byte[]} backend for {@link AsnBufferBackend}.
 * Used by both classic {@link AsnOutputStream} and new {@link BerCursor} / {@link BerWriter}.
 */
public final class HeapAsnBufferBackend implements AsnBufferBackend {

    private byte[] buffer;
    private int writerIndex;
    private int capacity;

    public HeapAsnBufferBackend(int initialCapacity) {
        this.capacity = initialCapacity;
        this.buffer = new byte[this.capacity];
        this.writerIndex = 0;
    }

    /** Convenience: wrap existing byte array (read-only use; writer may mutate). */
    HeapAsnBufferBackend(byte[] data) {
        this.buffer = data;
        this.capacity = data.length;
        this.writerIndex = 0;
    }

    byte[] getBuffer() {
        return this.buffer;
    }

    @Override
    public int getWriterIndex() {
        return this.writerIndex;
    }

    @Override
    public void setWriterIndex(int index) {
        this.writerIndex = index;
    }

    @Override
    public void writeByte(int value) {
        ensureWritable(1);
        this.buffer[this.writerIndex++] = (byte) value;
    }

    @Override
    public void writeBytes(byte[] source, int offset, int length) {
        ensureWritable(length);
        System.arraycopy(source, offset, this.buffer, this.writerIndex, length);
        this.writerIndex += length;
    }

    @Override
    public void setByte(int index, byte value) {
        this.buffer[index] = value;
    }

    @Override
    public byte getByte(int index) {
        return this.buffer[index];
    }

    @Override
    public void ensureWritable(int additional) {
        if (this.writerIndex + additional <= this.capacity) {
            return;
        }
        int newCapacity = this.capacity * 2;
        if (newCapacity < this.writerIndex + additional) {
            newCapacity = this.writerIndex + additional + this.capacity;
        }
        byte[] newBuffer = new byte[newCapacity];
        System.arraycopy(this.buffer, 0, newBuffer, 0, this.buffer.length);
        this.buffer = newBuffer;
        this.capacity = newCapacity;
    }

    @Override
    public void copyBytes(int srcIndex, int dstIndex, int length) {
        System.arraycopy(this.buffer, srcIndex, this.buffer, dstIndex, length);
    }

    @Override
    public void reset() {
        this.writerIndex = 0;
    }

    @Override
    public int getPayloadStart() {
        return 0;
    }

    // ── Read-oriented (BerCursor support) ────────────────────────────

    @Override
    public int readByte(int offset) {
        return this.buffer[offset] & 0xFF;
    }

    @Override
    public byte[] toByteArray(int offset, int length) {
        byte[] copy = new byte[length];
        System.arraycopy(this.buffer, offset, copy, 0, length);
        return copy;
    }

    @Override
    public ByteBuf slice(int offset, int length) {
        return Unpooled.wrappedBuffer(this.buffer, offset, length);
    }

    @Override
    public boolean equalsBytes(int offset, byte[] other, int otherOffset, int length) {
        for (int i = 0; i < length; i++) {
            if (this.buffer[offset + i] != other[otherOffset + i]) {
                return false;
            }
        }
        return true;
    }

    // ── Bulk copy (fixbercursor.md Step 1) ────────────────────────────

    @Override
    public void copyTo(int srcOffset, byte[] dest, int destOffset, int length) {
        System.arraycopy(this.buffer, srcOffset, dest, destOffset, length);
    }
}
