package org.restcomm.protocols.ss7.m3ua.impl.message;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.mobicents.protocols.api.Association;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.AssociationImpl;
import org.restcomm.protocols.ss7.m3ua.impl.AspFactoryImpl;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.m3ua.impl.message.M3UAMessageImpl;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ProtocolDataImpl;
import org.restcomm.protocols.ss7.m3ua.message.M3UAMessage;
import org.restcomm.protocols.ss7.m3ua.message.MessageType;
import org.restcomm.protocols.ss7.m3ua.message.transfer.PayloadData;
import org.restcomm.protocols.ss7.m3ua.parameter.ProtocolData;
import org.testng.annotations.Test;

public class M3uaInboundZeroCopyTest {

    private static final byte[] SAMPLE = new byte[] { 0x01, 0x00, 0x01, 0x01, 0x00, 0x00, 0x00, 0x3c, 0x02, 0x00, 0x00,
            0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x00, 0x08, 0x00, 0x00, 0x00, 0x19, 0x02, 0x10, 0x00, 0x21, 0x00,
            0x00, 0x17, (byte) 0x9d, 0x00, 0x00, 0x18, 0x1c, 0x03, 0x03, 0x00, 0x02, 0x09, 0x00, 0x03, 0x05, 0x07, 0x02,
            0x42, 0x01, 0x02, 0x42, 0x01, 0x05, 0x03, (byte) 0xd5, 0x1c, 0x18, 0x00, 0x00, 0x00, 0x00 };

    private static final byte[] PL_DATA = new byte[] { 9, 0, 3, 5, 7, 2, 66, 1, 2, 66, 1, 5, 3, -43, 28, 24, 0 };

    @Test
    public void sctpBundledMessagesUseZeroCopy() throws Exception {
        byte[] bundled = new byte[SAMPLE.length * 2];
        System.arraycopy(SAMPLE, 0, bundled, 0, SAMPLE.length);
        System.arraycopy(SAMPLE, 0, bundled, SAMPLE.length, SAMPLE.length);

        AspFactoryImplProxy aspFactory = new AspFactoryImplProxy(true);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bundled);
        org.mobicents.protocols.api.PayloadData pd = new org.mobicents.protocols.api.PayloadData(byteBuf.capacity(), byteBuf,
                true, false, 0, 0);
        AssociationImpl association = new AssociationImpl("hostAddress", 1111, "peerAddress", 1112, "assocName",
                IpChannelType.SCTP, null);
        aspFactory.onPayload(association, pd);

        assertEquals(aspFactory.lstReadMessage.size(), 2);
        for (M3UAMessage message : aspFactory.lstReadMessage) {
            PayloadData payloadData = (PayloadData) message;
            ProtocolDataImpl protocolData = (ProtocolDataImpl) payloadData.getData();
            assertNotNull(protocolData.getDataBuf());
            assertTrue(protocolData.getDataBuf().refCnt() > 0);
            assertTrue(Arrays.equals(PL_DATA, protocolData.getData()));
            ((M3UAMessageImpl) message).releaseParameters();
            assertNull(protocolData.getDataBuf());
        }
    }

    @Test
    public void truncatedSctpPayloadProducesNoMessage() throws Exception {
        byte[] truncated = Arrays.copyOf(SAMPLE, SAMPLE.length - 4);

        AspFactoryImplProxy aspFactory = new AspFactoryImplProxy(true);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(truncated);
        org.mobicents.protocols.api.PayloadData pd = new org.mobicents.protocols.api.PayloadData(byteBuf.capacity(), byteBuf,
                true, false, 0, 0);
        AssociationImpl association = new AssociationImpl("hostAddress", 1111, "peerAddress", 1112, "assocName",
                IpChannelType.SCTP, null);
        aspFactory.onPayload(association, pd);

        assertEquals(aspFactory.lstReadMessage.size(), 0);
    }

    @Test
    public void releaseParametersDropsProtocolDataRefCount() throws Exception {
        AspFactoryImplProxy aspFactory = new AspFactoryImplProxy(true);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(SAMPLE);
        org.mobicents.protocols.api.PayloadData pd = new org.mobicents.protocols.api.PayloadData(byteBuf.capacity(), byteBuf,
                true, false, 0, 0);
        AssociationImpl association = new AssociationImpl("hostAddress", 1111, "peerAddress", 1112, "assocName",
                IpChannelType.SCTP, null);
        aspFactory.onPayload(association, pd);

        assertEquals(aspFactory.lstReadMessage.size(), 1);
        M3UAMessage message = aspFactory.lstReadMessage.get(0);
        ProtocolDataImpl protocolData = (ProtocolDataImpl) ((PayloadData) message).getData();
        assertNotNull(protocolData.getDataBuf());
        assertTrue(protocolData.getDataBuf().refCnt() > 0);

        ((M3UAMessageImpl) message).releaseParameters();
        assertNull(protocolData.getDataBuf());
    }

    @Test
    public void tcpCompositeBufferPathUsesZeroCopy() throws Exception {
        byte[] header = new byte[] { 0x01, 0x00, 0x01, 0x01, 0x00, 0x00, 0x01, 0x08 };
        byte[] bodyStart = new byte[] { 0x00, 0x06, 0x00, 0x08, 0x00, 0x00, 0x00, 0x01, 0x02, 0x10, 0x00, (byte) 0xf8, 0x00,
                0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x01, 0x03, 0x02, 0x00, 0x01, 0x09, 0x01, 0x03, 0x10, 0x1d, 0x0d, 0x53,
                0x01, 0x00, (byte) 0x91, 0x00, 0x12, 0x04, 0x19, 0x09, 0x31, (byte) 0x91, 0x39, 0x08, 0x0d, 0x53, 0x02, 0x00,
                (byte) 0x92, 0x00, 0x12, 0x04, 0x19, 0x09, 0x31, (byte) 0x91, 0x39, 0x09, (byte) 0xc6, 0x62, (byte) 0x81,
                (byte) 0xc3, 0x48, 0x04, 0x00, 0x08, 0x00, 0x10, 0x6b, 0x1a, 0x28, 0x18, 0x06, 0x07, 0x00, 0x11, (byte) 0x86,
                0x05, 0x01, 0x01, 0x01, (byte) 0xa0, 0x0d, 0x60, 0x0b, (byte) 0xa1, 0x09, 0x06, 0x07, 0x04, 0x00, 0x00, 0x01,
                0x00, 0x32, 0x01, 0x6c, (byte) 0x81, (byte) 0x9e, (byte) 0xa1, (byte) 0x81, (byte) 0x9b, 0x02, 0x01, 0x01,
                0x02, 0x01, 0x00, 0x30, (byte) 0x81, (byte) 0x92, (byte) 0x80, 0x01, 0x0c, (byte) 0x82, 0x09, 0x03, 0x10, 0x13,
                0x60, (byte) 0x99, (byte) 0x86, 0x00, 0x00, 0x02, (byte) 0x83, 0x08, 0x04, 0x13, 0x19, (byte) 0x89, 0x17,
                (byte) 0x97, 0x31, 0x72, (byte) 0x85, 0x01, 0x0a, (byte) 0x88, 0x01, 0x01, (byte) 0x8a, 0x05, 0x04, 0x13, 0x19,
                (byte) 0x89, (byte) 0x86, (byte) 0xbb, 0x04, (byte) 0x80, 0x02, (byte) 0x80 };
        byte[] bodyEndWithOtherMessage = { (byte) 0x90, (byte) 0x9c, 0x01, 0x0c, (byte) 0x9f, 0x32, 0x08, 0x04, 0x64, 0x58,
                0x05, (byte) 0x94, 0x74, 0x34, (byte) 0xf3, (byte) 0xbf, 0x33, 0x02, (byte) 0x80, 0x00, (byte) 0xbf, 0x34,
                0x2b, 0x02, 0x01, 0x23, (byte) 0x80, 0x08, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x81, 0x07,
                (byte) 0x91, 0x19, (byte) 0x89, (byte) 0x86, (byte) 0x91, 0x01, (byte) 0x82, (byte) 0x82, 0x08, 0x04,
                (byte) 0x97, 0x19, (byte) 0x89, (byte) 0x86, (byte) 0x91, 0x01, (byte) 0x82, (byte) 0xa3, 0x09, (byte) 0x80,
                0x07, 0x04, (byte) 0xf4, (byte) 0x86, 0x00, 0x65, 0x18, (byte) 0xd1, (byte) 0xbf, 0x35, 0x03, (byte) 0x83,
                0x01, 0x11, (byte) 0x9f, 0x36, 0x08, (byte) 0xd2, 0x25, 0x00, 0x00, 0x0d, 0x62, 0x0b, (byte) 0x88, (byte) 0x9f,
                0x37, 0x07, (byte) 0x91, 0x19, (byte) 0x89, (byte) 0x86, (byte) 0x95, (byte) 0x99, (byte) 0x89, (byte) 0x9f,
                0x39, 0x08, 0x02, 0x11, 0x20, 0x10, (byte) 0x91, 0x45, 0x51, 0x23, 0x01, 0x00, 0x01, 0x01, 0x00, 0x00, 0x01,
                0x08, 0x00, 0x06, 0x00, 0x08, 0x00, 0x00, 0x00, 0x01, 0x02, 0x10, 0x00, (byte) 0xf8, 0x00, 0x00, 0x00, 0x02,
                0x00, 0x00, 0x00, 0x01, 0x03, 0x02, 0x00, 0x01, 0x09, 0x01, 0x03, 0x10, 0x1d, 0x0d, 0x53, 0x01, 0x00,
                (byte) 0x91, 0x00, 0x12, 0x04, 0x19, 0x09, 0x31, (byte) 0x91, 0x39, 0x08, 0x0d, 0x53, 0x02, 0x00, (byte) 0x92,
                0x00, 0x12, 0x04, 0x19, 0x09, 0x31, (byte) 0x91, 0x39, 0x09, (byte) 0xc6, 0x62, (byte) 0x81, (byte) 0xc3, 0x48,
                0x04, 0x00, 0x08, 0x00, 0x10, 0x6b, 0x1a, 0x28, 0x18, 0x06, 0x07, 0x00, 0x11, (byte) 0x86, 0x05, 0x01, 0x01,
                0x01, (byte) 0xa0, 0x0d, 0x60, 0x0b, (byte) 0xa1, 0x09, 0x06, 0x07, 0x04, 0x00, 0x00, 0x01, 0x00, 0x32, 0x01,
                0x6c, (byte) 0x81, (byte) 0x9e, (byte) 0xa1, (byte) 0x81, (byte) 0x9b, 0x02, 0x01, 0x01, 0x02, 0x01, 0x00,
                0x30, (byte) 0x81, (byte) 0x92, (byte) 0x80, 0x01, 0x0c, (byte) 0x82, 0x09, 0x03, 0x10, 0x13, 0x60,
                (byte) 0x99, (byte) 0x86, 0x00, 0x00, 0x02, (byte) 0x83, 0x08, 0x04, 0x13, 0x19, (byte) 0x89, 0x17,
                (byte) 0x97, 0x31, 0x72, (byte) 0x85, 0x01, 0x0a, (byte) 0x88, 0x01, 0x01, (byte) 0x8a, 0x05, 0x04, 0x13, 0x19,
                (byte) 0x89, (byte) 0x86, (byte) 0xbb, 0x04, (byte) 0x80, 0x02, (byte) 0x80, (byte) 0x90, (byte) 0x9c, 0x01,
                0x0c, (byte) 0x9f, 0x32, 0x08, 0x04, 0x64, 0x58, 0x05, (byte) 0x94, 0x74, 0x34, (byte) 0xf3, (byte) 0xbf, 0x33,
                0x02, (byte) 0x80, 0x00, (byte) 0xbf, 0x34, 0x2b, 0x02, 0x01, 0x23, (byte) 0x80, 0x08, 0x10, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, (byte) 0x81, 0x07, (byte) 0x91, 0x19, (byte) 0x89, (byte) 0x86, (byte) 0x91, 0x01,
                (byte) 0x82, (byte) 0x82, 0x08, 0x04, (byte) 0x97, 0x19, (byte) 0x89, (byte) 0x86, (byte) 0x91, 0x01,
                (byte) 0x82, (byte) 0xa3, 0x09, (byte) 0x80, 0x07, 0x04, (byte) 0xf4, (byte) 0x86, 0x00, 0x65, 0x18,
                (byte) 0xd1, (byte) 0xbf, 0x35, 0x03, (byte) 0x83, 0x01, 0x11, (byte) 0x9f, 0x36, 0x08, (byte) 0xd2, 0x25,
                0x00, 0x00, 0x0d, 0x62, 0x0b, (byte) 0x88, (byte) 0x9f, 0x37, 0x07, (byte) 0x91, 0x19, (byte) 0x89,
                (byte) 0x86, (byte) 0x95, (byte) 0x99, (byte) 0x89, (byte) 0x9f, 0x39, 0x08, 0x02, 0x11, 0x20, 0x10,
                (byte) 0x91, 0x45, 0x51, 0x23 };

        AspFactoryImplProxy aspFactory = new AspFactoryImplProxy(true);
        AssociationImpl association = new AssociationImpl("hostAddress", 1111, "peerAddress", 1112, "assocName",
                IpChannelType.TCP, null);

        org.mobicents.protocols.api.PayloadData pd = new org.mobicents.protocols.api.PayloadData(header.length,
                Unpooled.wrappedBuffer(header), true, false, 0, 0);
        aspFactory.onPayload(association, pd);
        assertEquals(aspFactory.lstReadMessage.size(), 0);

        pd = new org.mobicents.protocols.api.PayloadData(bodyStart.length, Unpooled.wrappedBuffer(bodyStart), true, false, 0,
                0);
        aspFactory.onPayload(association, pd);
        assertEquals(aspFactory.lstReadMessage.size(), 0);

        pd = new org.mobicents.protocols.api.PayloadData(bodyEndWithOtherMessage.length,
                Unpooled.wrappedBuffer(bodyEndWithOtherMessage), true, false, 0, 0);
        aspFactory.onPayload(association, pd);
        assertEquals(aspFactory.lstReadMessage.size(), 2);

        for (M3UAMessage message : aspFactory.lstReadMessage) {
            assertEquals(MessageType.PAYLOAD, message.getMessageType());
            PayloadData payloadData = (PayloadData) message;
            ProtocolData protocolData = payloadData.getData();
            assertNotNull(protocolData);
            ProtocolDataImpl protocolDataImpl = (ProtocolDataImpl) protocolData;
            assertNotNull(protocolDataImpl.getDataBuf());
            assertTrue(protocolDataImpl.getDataBuf().refCnt() > 0);
            assertEquals(2, protocolData.getOpc());
            assertEquals(1, protocolData.getDpc());
            ((M3UAMessageImpl) message).releaseParameters();
            assertNull(protocolDataImpl.getDataBuf());
        }
    }

    private class AspFactoryImplProxy extends AspFactoryImpl {
        protected ArrayList<M3UAMessage> lstReadMessage = new ArrayList<M3UAMessage>();

        public AspFactoryImplProxy(boolean nettySupport) {
            super("M3uaAspFact", 16, 1, false);
            M3UAManagementImpl m3uaManagement = new M3UAManagementImplProxy("Test", "m3ua", nettySupport);
            this.setM3UAManagement(m3uaManagement);
            this.createSLSTable(8);
        }

        protected void read(M3UAMessage message) {
            lstReadMessage.add(message);
        }
    }

    private class M3UAManagementImplProxy extends M3UAManagementImpl {
        public M3UAManagementImplProxy(String name, String productName, boolean sctpLibNettySupport) {
            super(name, productName, null);
            this.sctpLibNettySupport = sctpLibNettySupport;
        }
    }
}
