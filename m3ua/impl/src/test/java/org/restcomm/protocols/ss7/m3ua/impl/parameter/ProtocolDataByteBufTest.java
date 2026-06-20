package org.restcomm.protocols.ss7.m3ua.impl.parameter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.restcomm.protocols.ss7.m3ua.impl.message.MessageFactoryImpl;
import org.restcomm.protocols.ss7.m3ua.impl.message.M3UAMessageImpl;
import org.restcomm.protocols.ss7.m3ua.message.transfer.PayloadData;
import org.restcomm.protocols.ss7.m3ua.parameter.ProtocolData;
import org.testng.annotations.Test;

public class ProtocolDataByteBufTest {

    private static final byte[] SAMPLE = new byte[] { 0x01, 0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x3c, 0x02, 0x00, 0x00,
            0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x00, 0x08, 0x00, 0x00, 0x00, 0x19, 0x02, 0x10, 0x00, 0x21, 0x00,
            0x00, 0x17, (byte) 0x9d, 0x00, 0x00, 0x18, 0x1c, 0x03, 0x03, 0x00, 0x02, 0x09, 0x00, 0x03, 0x05, 0x07, 0x02,
            0x42, 0x01, 0x02, 0x42, 0x01, 0x05, 0x03, (byte) 0xd5, 0x1c, 0x18, 0x00, 0x00, 0x00, 0x00 };

    private static final byte[] PL_DATA = new byte[] { 9, 0, 3, 5, 7, 2, 66, 1, 2, 66, 1, 5, 3, -43, 28, 24, 0 };

    @Test
    public void byteBufDecodeUsesSliceWithoutCopyingPayload() {
        MessageFactoryImpl factory = new MessageFactoryImpl();
        ByteBuf buf = Unpooled.wrappedBuffer(SAMPLE);
        PayloadData payload = (PayloadData) factory.createMessage(buf);
        ProtocolDataImpl protocolData = (ProtocolDataImpl) payload.getData();

        assertNotNull(protocolData.getDataBuf());
        assertTrue(protocolData.getDataBuf().refCnt() > 0);
        assertEquals(6045, protocolData.getOpc());
        assertEquals(6172, protocolData.getDpc());
        assertTrue(Arrays.equals(PL_DATA, protocolData.getData()));

        ((M3UAMessageImpl) payload).releaseParameters();
        assertNull(protocolData.getDataBuf());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void malformedProtocolDataHeaderShorterThan12Bytes() {
        ByteBuf shortValue = Unpooled.wrappedBuffer(new byte[] { 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x02 });
        ProtocolDataImpl.fromProtocolDataByteBuf(shortValue);
    }

    @Test
    public void releaseParametersDropsRefCount() {
        MessageFactoryImpl factory = new MessageFactoryImpl();
        ByteBuf buf = Unpooled.wrappedBuffer(SAMPLE);
        PayloadData payload = (PayloadData) factory.createMessage(buf);
        ProtocolDataImpl protocolData = (ProtocolDataImpl) payload.getData();

        assertNotNull(protocolData.getDataBuf());
        assertTrue(protocolData.getDataBuf().refCnt() > 0);

        ((M3UAMessageImpl) payload).releaseParameters();
        assertNull(protocolData.getDataBuf());
    }

    @Test
    public void encodeRoundTripViaGetData() {
        MessageFactoryImpl factory = new MessageFactoryImpl();
        ByteBuf buf = Unpooled.wrappedBuffer(SAMPLE);
        PayloadData payload = (PayloadData) factory.createMessage(buf);
        ProtocolDataImpl protocolData = (ProtocolDataImpl) payload.getData();
        byte[] originalPayload = protocolData.getData();

        ByteBuf encoded = Unpooled.buffer();
        ((M3UAMessageImpl) payload).encode(encoded);
        ((M3UAMessageImpl) payload).releaseParameters();

        PayloadData roundTrip = (PayloadData) factory.createMessage(encoded);
        try {
            assertTrue(Arrays.equals(originalPayload, roundTrip.getData().getData()));
            assertEquals(6045, roundTrip.getData().getOpc());
            assertEquals(6172, roundTrip.getData().getDpc());
        } finally {
            ((M3UAMessageImpl) roundTrip).releaseParameters();
        }
    }
}
