package org.restcomm.protocols.ss7.tcap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.tcap.asn.Encodable;
import org.restcomm.protocols.ss7.tcap.asn.EncodeException;
import org.restcomm.protocols.ss7.tcap.asn.ParseException;
import org.testng.annotations.Test;

import io.netty.buffer.ByteBuf;

@Test(groups = { "tcap", "netty-encode" })
public class TcapOutboundEncoderTest {

    private static final class SimpleEncodable implements Encodable {
        private static final long serialVersionUID = 1L;

        @Override
        public void encode(AsnOutputStream aos) throws EncodeException {
            try {
                aos.writeOctetString(new byte[] { 0x42 });
            } catch (Exception e) {
                throw new EncodeException(e);
            }
        }

        @Override
        public void decode(AsnInputStream ais) throws ParseException {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void encodeSimpleEncodableUsesNettyPath() throws EncodeException {
        TcapOutboundEncoder.EncodedTcapPayload payload = TcapOutboundEncoder.encode(new SimpleEncodable());
        try {
            assertTrue(payload.isNettyEncoded());
            assertEquals(payload.getHeaderReserve(), TcapOutboundEncoder.DEFAULT_HEADER_RESERVE);
            assertNotNull(payload.getNettyStream());
            assertNotNull(payload.getEncodedByteBuf());

            byte[] expected = new byte[] { 0x04, 0x01, 0x42 };
            assertTrue(Arrays.equals(payload.toByteArray(), expected));
            assertEquals(payload.getEncodedByteBuf().readableBytes(), expected.length);
        } finally {
            ByteBuf buffer = payload.getNettyStream().getByteBuf();
            int refBefore = buffer.refCnt();
            payload.release();
            assertEquals(buffer.refCnt(), refBefore);
        }
    }
}
