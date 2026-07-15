package org.mobicents.protocols.asn;

import io.netty.buffer.ByteBuf;

/**
 * ThreadLocal pool for {@link AsnInputStream} reuse on hot decode paths and
 * {@link NettyAsnOutputStream} / direct {@link ByteBuf} reuse on outbound encode paths.
 */
public final class AsnStreamPool {

    private static final ThreadLocal<AsnInputStream> INPUT_POOL =
            ThreadLocal.withInitial(() -> new AsnInputStream(new byte[0]));

    private static final ThreadLocal<NettyOutboundEncoder> NETTY_OUTBOUND_POOL =
            ThreadLocal.withInitial(NettyOutboundEncoder::new);

    private AsnStreamPool() {
    }

    public static AsnInputStream borrow(byte[] data) {
        AsnInputStream ais = INPUT_POOL.get();
        ais.reset(data);
        return ais;
    }

    public static AsnInputStream borrowSlice(byte[] buffer, int offset, int length) {
        AsnInputStream ais = INPUT_POOL.get();
        ais.resetSlice(buffer, offset, length);
        return ais;
    }

    public static AsnInputStream borrowByteBufSlice(ByteBuf buffer, int offset, int length) {
        AsnInputStream ais = INPUT_POOL.get();
        ais.resetByteBufSlice(buffer, offset, length);
        return ais;
    }

    public static AsnInputStream borrowTagged(byte[] data, int tagClass, boolean isPrimitive, int tag) {
        AsnInputStream ais = INPUT_POOL.get();
        ais.resetTagged(data, tagClass, isPrimitive, tag);
        return ais;
    }

    /**
     * Borrows a thread-local direct {@link NettyAsnOutputStream} backed by a pooled {@link ByteBuf}.
     * The returned stream is reset and ready for encoding. Call {@link #releaseNettyOutput()} when done.
     */
    public static NettyAsnOutputStream borrowNettyOutput(int payloadEstimate, int headerReserve) {
        return NETTY_OUTBOUND_POOL.get().borrow(payloadEstimate, headerReserve);
    }

    /**
     * Releases the current thread-local outbound Netty buffer after a send path has finished with it.
     */
    public static void releaseNettyOutput() {
        NETTY_OUTBOUND_POOL.get().release();
    }

    private static final class NettyOutboundEncoder {
        private ByteBuf buffer;
        private NettyAsnOutputStream stream;
        private int headerReserve;

        NettyAsnOutputStream borrow(int payloadEstimate, int headerReserve) {
            this.headerReserve = Math.max(0, headerReserve);
            if (this.buffer == null || !this.buffer.isDirect()) {
                replaceBuffer(payloadEstimate);
            } else {
                int required = this.headerReserve + Math.max(0, payloadEstimate);
                if (this.buffer.capacity() < required) {
                    this.buffer.release();
                    replaceBuffer(payloadEstimate);
                } else {
                    this.buffer.clear();
                    this.buffer.writerIndex(this.headerReserve);
                    this.buffer.readerIndex(this.headerReserve);
                }
            }
            if (this.stream == null) {
                this.stream = new NettyAsnOutputStream(this.buffer, this.headerReserve);
            } else {
                this.stream.reset();
            }
            return this.stream;
        }

        void release() {
            if (this.buffer != null) {
                this.buffer.clear();
                this.buffer.writerIndex(this.headerReserve);
                this.buffer.readerIndex(this.headerReserve);
            }
        }

        private void replaceBuffer(int payloadEstimate) {
            this.buffer = AsnByteBufUtils.allocateWithHeaderRoom(payloadEstimate, this.headerReserve);
        }
    }
}
