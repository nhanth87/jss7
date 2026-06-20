package org.mobicents.protocols.asn;

import io.netty.buffer.ByteBuf;

/**
 * BER/ASN.1 output stream backed by a Netty {@link ByteBuf}. Extends {@link AsnOutputStream}
 * so existing TCAP {@code encode(AsnOutputStream)} call sites work unchanged.
 */
public class NettyAsnOutputStream extends AsnOutputStream {

    private final ByteBufAsnBufferBackend byteBufBackend;

    public NettyAsnOutputStream(ByteBuf buffer) {
        this(buffer, buffer.readerIndex());
    }

    public NettyAsnOutputStream(ByteBuf buffer, int payloadStart) {
        super(new ByteBufAsnBufferBackend(buffer, payloadStart));
        this.byteBufBackend = (ByteBufAsnBufferBackend) getBackend();
    }

    AsnBufferBackend getBackend() {
        return super.getBackendInternal();
    }

    /**
     * Returns the underlying Netty buffer (includes any pre-reserved header room).
     */
    public ByteBuf getByteBuf() {
        return this.byteBufBackend.getByteBuf();
    }

    /**
     * Index where ASN payload encoding starts (after header reserve).
     */
    public int getPayloadStart() {
        return this.byteBufBackend.getPayloadStart();
    }

    /**
     * Returns a read-only view of the encoded ASN payload without copying.
     * Caller must not release the parent buffer while the slice is in use.
     */
    public ByteBuf encodedSlice() {
        ByteBuf buf = getByteBuf();
        return buf.slice(getPayloadStart(), getEncodedLength());
    }

    /**
     * Copies the encoded ASN payload into a new heap {@code byte[]}.
     */
    public byte[] encodedBytes() {
        return copyEncodedBytes();
    }
}
