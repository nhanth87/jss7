package org.mobicents.protocols.asn;

import io.netty.buffer.ByteBuf;

/**
 * Netty {@link ByteBuf} backend for {@link AsnBufferBackend}.
 * Used by {@link NettyAsnOutputStream} for outbound and by {@link BerCursor} for
 * zero-copy inbound decode.
 */
public final class ByteBufAsnBufferBackend implements AsnBufferBackend {

    private final ByteBuf buffer;
    private final int payloadStart;

    public ByteBufAsnBufferBackend(ByteBuf buffer, int payloadStart) {
        this.buffer = buffer;
        this.payloadStart = payloadStart;
        this.buffer.writerIndex(payloadStart);
        this.buffer.readerIndex(payloadStart);
    }

    ByteBuf getByteBuf() {
        return this.buffer;
    }

    @Override
    public int getWriterIndex() {
        return this.buffer.writerIndex();
    }

    @Override
    public void setWriterIndex(int index) {
        this.buffer.writerIndex(index);
    }

    @Override
    public void writeByte(int value) {
        this.buffer.writeByte(value);
    }

    @Override
    public void writeBytes(byte[] source, int offset, int length) {
        this.buffer.writeBytes(source, offset, length);
    }

    @Override
    public void setByte(int index, byte value) {
        this.buffer.setByte(index, value);
    }

    @Override
    public byte getByte(int index) {
        return this.buffer.getByte(index);
    }

    @Override
    public void ensureWritable(int additional) {
        this.buffer.ensureWritable(additional);
    }

    @Override
    public void copyBytes(int srcIndex, int dstIndex, int length) {
        for (int i = length - 1; i >= 0; i--) {
            this.buffer.setByte(dstIndex + i, this.buffer.getByte(srcIndex + i));
        }
    }

    @Override
    public void reset() {
        this.buffer.writerIndex(this.payloadStart);
        this.buffer.readerIndex(this.payloadStart);
    }

    @Override
    public int getPayloadStart() {
        return this.payloadStart;
    }

    // ── Read-oriented (BerCursor support) ────────────────────────────

    @Override
    public int readByte(int offset) {
        return this.buffer.getUnsignedByte(offset);
    }

    @Override
    public byte[] toByteArray(int offset, int length) {
        byte[] copy = new byte[length];
        this.buffer.getBytes(offset, copy, 0, length);
        return copy;
    }

    @Override
    public ByteBuf slice(int offset, int length) {
        return this.buffer.slice(offset, length);
    }

    @Override
    public boolean equalsBytes(int offset, byte[] other, int otherOffset, int length) {
        for (int i = 0; i < length; i++) {
            if (this.buffer.getByte(offset + i) != other[otherOffset + i]) {
                return false;
            }
        }
        return true;
    }

    // ── Bulk copy (fixbercursor.md Step 1) ────────────────────────────

    @Override
    public void copyTo(int srcOffset, byte[] dest, int destOffset, int length) {
        this.buffer.getBytes(srcOffset, dest, destOffset, length);
    }
}
