package org.restcomm.protocols.ss7.tcap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnStreamPool;
import org.restcomm.protocols.ss7.tcap.asn.TcapFactory;
import org.restcomm.protocols.ss7.tcap.asn.comp.TCBeginMessage;
import org.testng.annotations.Test;

import io.netty.buffer.Unpooled;

public class TcapInboundByteBufTest {

    @Test
    public void testTcBeginDecodeFromInboundByteBufSlice() throws Exception {
        byte[] tcapBegin = new byte[] {
                0x62, 38,
                0x48, 4, 8, (byte) 0xA5, 0, 1,
                0x6B, 30,
                0x28, 28,
                0x06, 7, 0, 17, (byte) 134, 5, 1, 1, 1, (byte)
                160, 17,
                0x60, 15, (byte)
                0x80, 2, 7, (byte) 0x80, (byte)
                161, 9,
                6, 7, 4, 0, 1, 1, 1, 3, 0
        };

        AsnInputStream ais = AsnStreamPool.borrowByteBufSlice(Unpooled.wrappedBuffer(tcapBegin), 0, tcapBegin.length);
        assertTrue(ais.isByteBufBacked());
        assertEquals(ais.readTag(), TCBeginMessage._TAG);

        TCBeginMessage tcm = TcapFactory.createTCBeginMessage(ais);
        assertTrue(Arrays.equals(tcm.getOriginatingTransactionId(), new byte[] { 8, (byte) 0xA5, 0, 1 }));
        assertNotNull(tcm.getDialogPortion());
    }
}
