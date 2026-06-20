package org.mobicents.protocols.asn;

/**
 * Low-level byte sink for BER encoding. Heap and Netty {@link io.netty.buffer.ByteBuf}
 * backends share the same {@link AsnOutputStream} encode logic.
 */
interface AsnBufferBackend {

    int getWriterIndex();

    void setWriterIndex(int index);

    void writeByte(int value);

    void writeBytes(byte[] source, int offset, int length);

    void setByte(int index, byte value);

    byte getByte(int index);

    void ensureWritable(int additional);

    /**
     * Copies {@code length} bytes from {@code srcIndex} to {@code dstIndex}, allowing overlap
     * when expanding a length field (shift-right semantics used by {@link AsnOutputStream#FinalizeContent(int)}).
     */
    void copyBytes(int srcIndex, int dstIndex, int length);

    /** Resets writer index to the start of the payload region. */
    void reset();

    /** Returns the payload start offset (0 for heap buffers). */
    int getPayloadStart();
}
