package org.mobicents.protocols.asn;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import io.netty.buffer.Unpooled;

public class AsnInputStreamByteBufTest {

    @Test
    public void testBorrowByteBufSliceReadsBytes() throws Exception {
        byte[] payload = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        AsnInputStream ais = AsnStreamPool.borrowByteBufSlice(Unpooled.wrappedBuffer(payload), 1, 3);
        assertEquals(ais.read(), 0x02);
        assertEquals(ais.read(), 0x03);
        assertEquals(ais.read(), 0x04);
        assertEquals(ais.available(), 0);
    }

    @Test
    public void testReadSequenceStreamFromByteBufSlice() throws Exception {
        byte[] payload = new byte[] {
                0x30, 0x06,
                0x02, 0x01, 0x2A,
                0x02, 0x01, 0x07
        };
        AsnInputStream ais = AsnStreamPool.borrowByteBufSlice(Unpooled.wrappedBuffer(payload), 0, payload.length);
        assertTrue(ais.isByteBufBacked());
        assertEquals(ais.readTag(), 0x10);
        AsnInputStream seq = ais.readSequenceStream();
        assertTrue(seq.isByteBufBacked());
        assertEquals(seq.readTag(), 0x02);
        assertEquals(seq.readInteger(), 42);
        assertEquals(seq.readTag(), 0x02);
        assertEquals(seq.readInteger(), 7);
        assertEquals(seq.available(), 0);
    }
}
