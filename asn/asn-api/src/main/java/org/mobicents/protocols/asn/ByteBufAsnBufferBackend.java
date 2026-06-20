package org.mobicents.protocols.asn;

import io.netty.buffer.ByteBuf;

final class ByteBufAsnBufferBackend implements AsnBufferBackend {

    private final ByteBuf buffer;
    private final int payloadStart;

    ByteBufAsnBufferBackend(ByteBuf buffer, int payloadStart) {
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
}
