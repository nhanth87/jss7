package org.restcomm.protocols.ss7.sccp.impl.message;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.mtp.Mtp3;
import org.restcomm.protocols.ss7.sccp.impl.SccpHarness;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImpl;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImplProxy;
import org.restcomm.protocols.ss7.sccp.impl.User;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.message.SccpDataMessage;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.ss7ext.Ss7ExtInterface;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.netty.buffer.Unpooled;

/**
 * Zero-copy SCCP inbound path (always enabled).
 */
public class SccpByteBufReassemblyTest extends SccpHarness {

    private final SccpDataMessageTest dataHelper = new SccpDataMessageTest();
    private SccpAddress a1;
    private SccpAddress a2;
    private User u1;

    @BeforeClass
    public void setUpClass() throws Exception {
        this.sccpStack1Name = "SccpByteBufReassemblyTestStack1";
        this.sccpStack2Name = "SccpByteBufReassemblyTestStack2";
    }

    @Override
    protected SccpStackImpl createStack(String name, Ss7ExtInterface ss7ExtInterface) {
        return new SccpStackImplProxy(name, ss7ExtInterface);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        a1 = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null, getStack1PC(), 8);
        a2 = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null, getStack2PC(), 8);
        u1 = new User(sccpStack1.getSccpProvider(), a1, a2, getSSN());
        u1.register();
        Thread.sleep(100);
    }

    @AfterMethod
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testByteBufUdtDelivery() throws Exception {
        SccpStackImpl stack = (SccpStackImpl) sccpStack1;
        stack.receiveM3uaProtocolData(getStack2PC(), getStack1PC(), Mtp3._SI_SERVICE_SCCP, 2, 0, 0,
                Unpooled.wrappedBuffer(dataHelper.getDataUdt1()));

        Thread.sleep(300);
        assertTrue(u1.getMessages().size() >= 1);
        SccpDataMessage dMsg = (SccpDataMessage) u1.getMessages().get(0);
        assertNotNull(dMsg.getDataBuf());
        assertTrue(Arrays.equals(dMsg.getData(), dataHelper.getDataUdtSrc()));
    }

    @Test
    public void testByteBufXudtReassembly() throws Exception {
        SccpStackImpl stack = (SccpStackImpl) sccpStack1;
        assertEquals(((SccpStackImplProxy) stack).getReassemplyCacheSize(), 0);
        stack.receiveM3uaProtocolData(getStack2PC(), getStack1PC(), Mtp3._SI_SERVICE_SCCP, 2, 0, 0,
                Unpooled.wrappedBuffer(MessageSegmentationTest.getDataSegm1()));
        Thread.sleep(100);
        assertEquals(((SccpStackImplProxy) stack).getReassemplyCacheSize(), 1);
        stack.receiveM3uaProtocolData(getStack2PC(), getStack1PC(), Mtp3._SI_SERVICE_SCCP, 2, 0, 0,
                Unpooled.wrappedBuffer(MessageSegmentationTest.getDataSegm2()));
        Thread.sleep(100);
        stack.receiveM3uaProtocolData(getStack2PC(), getStack1PC(), Mtp3._SI_SERVICE_SCCP, 2, 0, 0,
                Unpooled.wrappedBuffer(MessageSegmentationTest.getDataSegm3()));
        Thread.sleep(300);
        assertEquals(((SccpStackImplProxy) stack).getReassemplyCacheSize(), 0);
        assertEquals(u1.getMessages().size(), 1);
        SccpDataMessage dMsg = (SccpDataMessage) u1.getMessages().get(0);
        assertNotNull(dMsg.getDataBuf());
        assertTrue(Arrays.equals(dMsg.getData(), MessageSegmentationTest.getDataA()));
    }
}
