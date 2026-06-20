package org.restcomm.protocols.ss7.sccp.impl.message;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.testng.annotations.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class SccpByteBufDecodeReaderTest {

    @Test
    public void testReadDataSliceReturnsRetainedSlice() throws IOException {
        byte[] payload = { 0x01, 0x02, 0x03, 0x04, 0x05 };
        ByteBuf buf = Unpooled.wrappedBuffer(payload);
        SccpByteBufDecodeReader reader = new SccpByteBufDecodeReader(buf);

        reader.read();
        reader.read();

        ByteBuf slice = reader.readDataSlice(3);
        try {
            assertEquals(slice.readableBytes(), 3);
            assertEquals(slice.getByte(0), 0x03);
            assertEquals(slice.getByte(1), 0x04);
            assertEquals(slice.getByte(2), 0x05);
            assertEquals(buf.readerIndex(), 5);
        } finally {
            slice.release();
        }
    }

    @Test
    public void testReadDataSliceSharesBackingArray() throws IOException {
        byte[] payload = { 0x0A, 0x0B, 0x0C };
        ByteBuf buf = Unpooled.wrappedBuffer(payload);
        SccpByteBufDecodeReader reader = new SccpByteBufDecodeReader(buf);

        ByteBuf slice = reader.readDataSlice(3);
        try {
            assertSame(slice.array(), payload);
            slice.setByte(0, 0x7F);
            assertEquals(payload[0], 0x7F);
        } finally {
            slice.release();
        }
    }

    @Test(expectedExceptions = IOException.class)
    public void testReadDataSliceNotEnoughData() throws IOException {
        ByteBuf buf = Unpooled.wrappedBuffer(new byte[] { 0x01, 0x02 });
        SccpByteBufDecodeReader reader = new SccpByteBufDecodeReader(buf);
        reader.readDataSlice(3);
    }

    @Test
    public void testMarkResetAndAvailable() throws IOException {
        byte[] payload = { 0x10, 0x20, 0x30 };
        ByteBuf buf = Unpooled.wrappedBuffer(payload);
        SccpByteBufDecodeReader reader = new SccpByteBufDecodeReader(buf);

        assertEquals(reader.available(), 3);
        reader.mark();
        assertEquals(reader.read(), 0x10);
        reader.reset();
        assertEquals(reader.read(), 0x10);

        ByteBuf slice = reader.readDataSlice(2);
        try {
            byte[] copy = new byte[2];
            slice.readBytes(copy);
            assertTrue(Arrays.equals(copy, new byte[] { 0x20, 0x30 }));
        } finally {
            slice.release();
        }
    }
}
