package org.mobicents.protocols.asn;

import io.netty.buffer.ByteBuf;

/**
 * Low-level byte sink for BER encoding and source for zero-copy BER decoding.
 * Heap and Netty {@link ByteBuf} backends share this interface so that
 * {@link AsnOutputStream} (encode) and {@link BerCursor} (decode) can work
 * on either backing store without object allocation on hot paths.
 */
public interface AsnBufferBackend {

    // ── Write-oriented (existing) ──────────────────────────────────────
    int getWriterIndex();
    void setWriterIndex(int index);
    void writeByte(int value);
    void writeBytes(byte[] source, int offset, int length);
    void setByte(int index, byte value);
    byte getByte(int index);
    void ensureWritable(int additional);

    /**
     * Copies {@code length} bytes from {@code srcIndex} to {@code dstIndex},
     * allowing overlap when expanding a length field.
     */
    void copyBytes(int srcIndex, int dstIndex, int length);

    /** Resets writer index to the start of the payload region. */
    void reset();

    /** Returns the payload start offset (0 for heap buffers). */
    int getPayloadStart();

    // ── Read-oriented (new — for BerCursor zero-copy decode) ──────────
    /**
     * Reads an unsigned byte at the given absolute offset.
     * @return unsigned byte value (0–255)
     */
    int readByte(int offset);

    /**
     * Copies {@code length} bytes starting at {@code offset} into a new
     * heap {@code byte[]}.
     */
    byte[] toByteArray(int offset, int length);

    /**
     * Returns a Netty {@link ByteBuf} slice over {@code offset..offset+length}
     * without copying.
     */
    ByteBuf slice(int offset, int length);

    /**
     * Compares bytes at {@code offset} against {@code other[otherOff..]} for
     * {@code length} bytes.
     * @return true if every byte matches
     */
    boolean equalsBytes(int offset, byte[] other, int otherOffset, int length);

    // ── Bulk copy (fixbercursor.md Step 1) ────────────────────────────
    /**
     * Bulk copy bytes from this backend to a heap {@code byte[]}, avoiding
     * byte-by-byte loops. Called by {@link BerCursor#getOctetString()} and
     * {@link BerSlice#toByteArray()} on the ByteBuf path.
     *
     * @param srcOffset  offset in this backend
     * @param dest       destination heap array
     * @param destOffset destination offset
     * @param length     number of bytes to copy
     */
    void copyTo(int srcOffset, byte[] dest, int destOffset, int length);
}
