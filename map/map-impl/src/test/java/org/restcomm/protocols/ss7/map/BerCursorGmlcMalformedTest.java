package org.restcomm.protocols.ss7.map;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.map.api.MAPParsingComponentException;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.SubscriberIdentityImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.AnyTimeInterrogationRequestImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.RequestedInfoImpl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Malformed BER must surface as a typed AsnException / MAPParsingComponentException
 * or a clean fallback — never an unchecked exception.
 */
public class BerCursorGmlcMalformedTest {

    private static final String PROPERTY = "jss7.asn.berCursorEnabled";
    private String previous;

    @BeforeMethod
    public void enableBer() {
        previous = System.getProperty(PROPERTY);
        System.setProperty(PROPERTY, "true");
    }

    @AfterMethod
    public void restore() {
        if (previous == null)
            System.clearProperty(PROPERTY);
        else
            System.setProperty(PROPERTY, previous);
    }

    @Test
    public void testTruncatedBuffer() throws Exception {
        byte[] good = encodeAti();
        decodeTypedOrFallback(new byte[] { good[0], good[1] });
        decodeTypedOrFallback(new byte[] { 0x30, 0x10, (byte) 0xa0 });
    }

    @Test
    public void testOverflowLength() throws Exception {
        decodeTypedOrFallback(new byte[] { 0x30, 0x7F, 0x01 });
        decodeTypedOrFallback(new byte[] { 0x30, (byte) 0x84, (byte) 0x80, 0x00, 0x00, 0x00 });
    }

    @Test
    public void testIllegalIndefinitePrimitive() throws Exception {
        decodeTypedOrFallback(new byte[] { 0x30, 0x04, (byte) 0x80, (byte) 0x80, 0x00, 0x00 });
    }

    @Test
    public void testUnknownExtensionTagFallsBackCleanly() throws Exception {
        byte[] good = encodeAti();
        // SEQUENCE with one unknown context tag [31] NULL after a truncated prefix is
        // not useful — append an unknown context-constructed [30] empty to a valid ATI.
        byte[] withUnknown = new byte[good.length + 2];
        System.arraycopy(good, 0, withUnknown, 0, good.length);
        // Rewrite SEQUENCE length to include the extra TLV.
        withUnknown[1] = (byte) ((good[1] & 0xFF) + 2);
        withUnknown[good.length] = (byte) 0xBE; // CONTEXT 30 constructed
        withUnknown[good.length + 1] = 0x00;
        decodeTypedOrFallback(withUnknown);
    }

    private static byte[] encodeAti() throws Exception {
        ISDNAddressStringImpl msisdn = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN,
                "553499775190");
        ISDNAddressStringImpl scf = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN,
                "553496629943");
        RequestedInfoImpl requestedInfo = new RequestedInfoImpl(true, true, null, false, null, false, false, false, false);
        AnyTimeInterrogationRequestImpl ati = new AnyTimeInterrogationRequestImpl(new SubscriberIdentityImpl(msisdn),
                requestedInfo, scf, null);
        AsnOutputStream aos = new AsnOutputStream();
        ati.encodeAll(aos);
        return aos.toByteArray();
    }

    private static void decodeTypedOrFallback(byte[] wire) {
        try {
            AsnInputStream ais = new AsnInputStream(wire);
            if (ais.available() > 0)
                ais.readTag();
            new AnyTimeInterrogationRequestImpl().decodeAll(ais);
        } catch (MAPParsingComponentException | java.io.IOException expected) {
            assertTrue(expected instanceof Exception);
            return;
        } catch (RuntimeException unexpected) {
            fail("unchecked exception from malformed BER: " + unexpected);
        }
    }
}
