package org.mobicents.protocols.asn;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.testng.annotations.Test;

import io.netty.buffer.ByteBuf;

@Test(groups = { "asn", "netty-encode" })
public class NettyAsnOutputStreamTest {

    @Test
    public void encodeTagLengthValueWithHeaderReserve() throws Exception {
        int headerReserve = 128;
        NettyAsnOutputStream stream = AsnStreamPool.borrowNettyOutput(64, headerReserve);
        try {
            assertEquals(stream.getPayloadStart(), headerReserve);
            assertEquals(stream.getByteBuf().readerIndex(), headerReserve);
            assertEquals(stream.getByteBuf().writerIndex(), headerReserve);

            stream.writeTag(Tag.CLASS_UNIVERSAL, true, Tag.STRING_OCTET);
            int pos = stream.StartContentDefiniteLength();
            stream.write(0x0f);
            stream.FinalizeContent(pos);

            byte[] expected = new byte[] { 0x04, 0x01, 0x0f };
            assertEquals(stream.getEncodedLength(), expected.length);
            assertTrue(Arrays.equals(stream.copyEncodedBytes(), expected));
        } finally {
            AsnStreamPool.releaseNettyOutput();
        }
    }

    @Test
    public void encodedSliceIsZeroCopyView() throws Exception {
        int headerReserve = 64;
        NettyAsnOutputStream stream = AsnStreamPool.borrowNettyOutput(32, headerReserve);
        try {
            stream.writeOctetString(new byte[] { 0x01, 0x02, 0x03 });

            ByteBuf parent = stream.getByteBuf();
            ByteBuf slice = stream.encodedSlice();
            int payloadStart = stream.getPayloadStart();

            assertEquals(slice.readableBytes(), stream.getEncodedLength());
            assertEquals(slice.getByte(0), parent.getByte(payloadStart));
            assertEquals(slice.getByte(1), parent.getByte(payloadStart + 1));
            assertEquals(slice.getByte(2), parent.getByte(payloadStart + 2));

            byte[] fromParent = new byte[stream.getEncodedLength()];
            parent.getBytes(payloadStart, fromParent);
            byte[] fromSlice = new byte[slice.readableBytes()];
            slice.getBytes(slice.readerIndex(), fromSlice);
            assertTrue(Arrays.equals(fromParent, fromSlice));
        } finally {
            AsnStreamPool.releaseNettyOutput();
        }
    }

    @Test
    public void releaseViaAsnStreamPoolRetainsBufferForReuse() throws Exception {
        int headerReserve = 128;
        NettyAsnOutputStream stream1 = AsnStreamPool.borrowNettyOutput(32, headerReserve);
        ByteBuf buffer = stream1.getByteBuf();
        int refCnt = buffer.refCnt();

        stream1.writeOctetString(new byte[] { 0x0a });
        ByteBuf slice = stream1.encodedSlice();
        assertEquals(slice.readableBytes(), 3);

        AsnStreamPool.releaseNettyOutput();
        assertEquals(buffer.refCnt(), refCnt);

        NettyAsnOutputStream stream2 = AsnStreamPool.borrowNettyOutput(32, headerReserve);
        assertSame(stream2, stream1);
        assertSame(stream2.getByteBuf(), buffer);
        assertEquals(stream2.getPayloadStart(), headerReserve);
    }
}
