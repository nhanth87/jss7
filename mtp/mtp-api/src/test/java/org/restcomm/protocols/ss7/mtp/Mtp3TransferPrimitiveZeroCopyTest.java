package org.restcomm.protocols.ss7.mtp;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.restcomm.protocols.ss7.mtp.RoutingLabelFormat;
import org.testng.annotations.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

@Test(groups = { "mtp", "zero-copy" })
public class Mtp3TransferPrimitiveZeroCopyTest {

    private static final byte[] PAYLOAD = new byte[] { 0x09, 0x00, 0x03, 0x05, 0x07 };

    @Test
    public void valueOfRetainsByteBufWithoutCopying() {
        ByteBuf buf = Unpooled.wrappedBuffer(PAYLOAD);
        Mtp3TransferPrimitive msg = Mtp3TransferPrimitive.valueOf(3, 2, 0, 100, 200, 5, buf.retain(),
                RoutingLabelFormat.ITU);

        assertNotNull(msg.getDataBuf());
        assertEquals(msg.getDataBuf().refCnt(), 2);
        assertTrue(Arrays.equals(PAYLOAD, msg.getData()));
        assertEquals(msg.getOpc(), 100);
        assertEquals(msg.getDpc(), 200);

        msg.getDataBuf().release();
        buf.release();
    }

    @Test
    public void heapConstructorStillWorksForOamAndXml() {
        Mtp3TransferPrimitive msg = new Mtp3TransferPrimitive(3, 2, 0, 100, 200, 5, PAYLOAD, RoutingLabelFormat.ITU);
        assertTrue(Arrays.equals(PAYLOAD, msg.getData()));
    }
}
