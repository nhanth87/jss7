package org.mobicents.protocols.asn;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class FlatAsnParserByteBufTest {

    @Test
    public void testParseAllFromByteBufMatchesHeapParse() throws AsnException {
        byte[] data = new byte[] { 0x30, 0x0C, 0x04, 0x01, 0x0F, 0x04, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
        AsnMessageIndex heapIndex = new AsnMessageIndex();
        FlatAsnParser.parseAll(data, 0, data.length, heapIndex);

        ByteBuf buf = Unpooled.wrappedBuffer(data);
        AsnMessageIndex bufIndex = new AsnMessageIndex();
        FlatAsnParser.parseAll(buf, 0, data.length, bufIndex);

        assertEquals(bufIndex.tagCount, heapIndex.tagCount);
        for (int i = 0; i < heapIndex.tagCount; i++) {
            assertEquals(bufIndex.tags[i], heapIndex.tags[i]);
            assertEquals(bufIndex.valueOffsets[i], heapIndex.valueOffsets[i]);
            assertEquals(bufIndex.valueLengths[i], heapIndex.valueLengths[i]);
        }
        assertTrue(bufIndex.isByteBufBacked());
        assertSame(bufIndex.getRawBuf(), buf);
    }

    @Test
    public void testValueSliceSharesBackingArray() throws AsnException {
        byte[] data = new byte[] { 0x30, 0x06, 0x04, 0x04, 0x01, 0x02, 0x03, 0x04 };
        ByteBuf buf = Unpooled.wrappedBuffer(data);
        AsnMessageIndex index = new AsnMessageIndex();
        FlatAsnParser.parseAll(buf, 0, data.length, index);

        int octetIdx = AsnReaderHelper.findNthChildTag(index, 0, 0x04, 0);
        assertTrue(octetIdx >= 0);

        ByteBuf slice = index.valueSlice(octetIdx);
        try {
            assertEquals(slice.readableBytes(), 4);
            assertEquals(slice.getByte(0), 0x01);
            assertSame(slice.array(), data);
        } finally {
            slice.release();
        }
    }

    @Test
    public void testParseAllWithSubSliceOffset() throws AsnException {
        byte[] data = new byte[] { 0x00, 0x00, 0x30, 0x06, 0x04, 0x04, 0x0A, 0x0B, 0x0C, 0x0D };
        ByteBuf buf = Unpooled.wrappedBuffer(data);
        AsnMessageIndex index = new AsnMessageIndex();
        FlatAsnParser.parseAll(buf, 2, 8, index);

        int octetIdx = AsnReaderHelper.findNthChildTag(index, 0, 0x04, 0);
        assertTrue(octetIdx >= 0);
        assertEquals(index.byteAt(index.valueOffsets[octetIdx]), 0x0A);
    }
}
