package org.restcomm.protocols.ss7.sccp.impl.message;

import java.io.IOException;

import io.netty.buffer.ByteBuf;

/**
 * Zero-copy SCCP PDU reader for inbound decode. Address and optional-parameter
 * fields are copied (small); user data is exposed via {@link #readDataSlice(int)}.
 */
public class SccpByteBufDecodeReader {

    private final ByteBuf buf;

    public SccpByteBufDecodeReader(ByteBuf buf) {
        this.buf = buf;
    }

    public int available() {
        return buf.readableBytes();
    }

    public void mark() {
        buf.markReaderIndex();
    }

    public void reset() {
        buf.resetReaderIndex();
    }

    public int read() throws IOException {
        if (!buf.isReadable()) {
            throw new IOException("Not enough data in buffer");
        }
        return buf.readUnsignedByte();
    }

    public long skip(long n) throws IOException {
        if (n < 0) {
            throw new IOException("Negative skip length");
        }
        int toSkip = (int) n;
        if (buf.readableBytes() < toSkip) {
            throw new IOException("Not enough data in buffer");
        }
        buf.skipBytes(toSkip);
        return toSkip;
    }

    public void readBytes(byte[] dst) throws IOException {
        if (buf.readableBytes() < dst.length) {
            throw new IOException("Not enough data in buffer");
        }
        buf.readBytes(dst);
    }

    /**
     * Returns a retained slice of the SCCP user-data field (no payload copy).
     */
    public ByteBuf readDataSlice(int len) throws IOException {
        if (buf.readableBytes() < len) {
            throw new IOException("Not enough data in buffer");
        }
        return buf.readSlice(len).retain();
    }
}
