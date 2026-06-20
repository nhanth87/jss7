package org.mobicents.protocols.asn;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

/**
 * Utilities for Netty direct-buffer allocation on outbound ASN encode paths.
 */
public final class AsnByteBufUtils {

    private AsnByteBufUtils() {
    }

    /**
     * Allocates a direct {@link ByteBuf} with {@code headerReserve} bytes at the front for
     * lower-layer headers (SCCP/M3UA/SCTP). The writer and reader indices start at
     * {@code headerReserve} so ASN payload encoding begins after the reserved region.
     *
     * @param payloadEstimate expected ASN payload size (excluding header reserve)
     * @param headerReserve   bytes reserved before the ASN payload
     * @return a direct buffer ready for {@link NettyAsnOutputStream}
     */
    public static ByteBuf allocateWithHeaderRoom(int payloadEstimate, int headerReserve) {
        int reserve = Math.max(0, headerReserve);
        int estimate = Math.max(0, payloadEstimate);
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.directBuffer(reserve + estimate);
        buffer.writerIndex(reserve);
        buffer.readerIndex(reserve);
        return buffer;
    }
}
