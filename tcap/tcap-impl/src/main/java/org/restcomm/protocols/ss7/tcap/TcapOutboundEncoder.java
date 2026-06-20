package org.restcomm.protocols.ss7.tcap;

import org.mobicents.protocols.asn.AsnStreamPool;
import org.mobicents.protocols.asn.NettyAsnOutputStream;
import org.restcomm.protocols.ss7.tcap.asn.Encodable;
import org.restcomm.protocols.ss7.tcap.asn.EncodeException;

import io.netty.buffer.ByteBuf;

/**
 * Outbound TCAP ASN.1 encoder using {@link NettyAsnOutputStream} direct buffers.
 */
public final class TcapOutboundEncoder {

    public static final int DEFAULT_HEADER_RESERVE = 128;
    public static final int DEFAULT_PAYLOAD_ESTIMATE = 512;

    private TcapOutboundEncoder() {
    }

    public static EncodedTcapPayload encode(Encodable message) {
        return encode(message, DEFAULT_PAYLOAD_ESTIMATE, DEFAULT_HEADER_RESERVE);
    }

    public static EncodedTcapPayload encode(Encodable message, int payloadEstimate, int headerReserve) {
        NettyAsnOutputStream stream = AsnStreamPool.borrowNettyOutput(payloadEstimate, headerReserve);
        try {
            message.encode(stream);
        } catch (EncodeException e) {
            AsnStreamPool.releaseNettyOutput();
            throw new IllegalStateException("Failed to encode TCAP message", e);
        }
        return EncodedTcapPayload.netty(stream);
    }

    /**
     * Encoded TCAP payload. Netty-backed instances retain header room for lower-layer headers.
     */
    public static final class EncodedTcapPayload {
        private final byte[] heapBytes;
        private final NettyAsnOutputStream nettyStream;

        private EncodedTcapPayload(byte[] heapBytes, NettyAsnOutputStream nettyStream) {
            this.heapBytes = heapBytes;
            this.nettyStream = nettyStream;
        }

        static EncodedTcapPayload heap(byte[] data) {
            return new EncodedTcapPayload(data, null);
        }

        static EncodedTcapPayload netty(NettyAsnOutputStream stream) {
            return new EncodedTcapPayload(null, stream);
        }

        public boolean isNettyEncoded() {
            return this.nettyStream != null;
        }

        public NettyAsnOutputStream getNettyStream() {
            return this.nettyStream;
        }

        /**
         * Zero-copy view of encoded ASN bytes when Netty path is active.
         */
        public ByteBuf getEncodedByteBuf() {
            if (this.nettyStream == null) {
                return null;
            }
            return this.nettyStream.encodedSlice();
        }

        /**
         * Returns encoded TCAP bytes for SCCP send. Copies from direct buffer when needed.
         */
        public byte[] toByteArray() {
            if (this.heapBytes != null) {
                return this.heapBytes;
            }
            return this.nettyStream.copyEncodedBytes();
        }

        public int getHeaderReserve() {
            if (this.nettyStream == null) {
                return 0;
            }
            return this.nettyStream.getPayloadStart();
        }

        public void release() {
            if (this.nettyStream != null) {
                AsnStreamPool.releaseNettyOutput();
            }
        }
    }
}
