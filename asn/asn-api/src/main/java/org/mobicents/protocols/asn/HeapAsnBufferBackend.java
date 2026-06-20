package org.mobicents.protocols.asn;

final class HeapAsnBufferBackend implements AsnBufferBackend {

    private byte[] buffer;
    private int writerIndex;
    private int capacity;

    HeapAsnBufferBackend(int initialCapacity) {
        this.capacity = initialCapacity;
        this.buffer = new byte[this.capacity];
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
}
