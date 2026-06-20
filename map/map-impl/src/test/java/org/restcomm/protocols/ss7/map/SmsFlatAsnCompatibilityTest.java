package org.restcomm.protocols.ss7.map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.function.Supplier;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.map.dialog.MAPOpenInfoImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive;
import org.restcomm.protocols.ss7.map.service.sms.AlertServiceCentreRequestImpl;
import org.restcomm.protocols.ss7.map.service.sms.ForwardShortMessageRequestImpl;
import org.restcomm.protocols.ss7.map.service.sms.InformServiceCentreRequestImpl;
import org.restcomm.protocols.ss7.map.service.sms.MoForwardShortMessageRequestImpl;
import org.restcomm.protocols.ss7.map.service.sms.MtForwardShortMessageRequestImpl;
import org.restcomm.protocols.ss7.map.service.sms.ReportSMDeliveryStatusRequestImpl;
import org.restcomm.protocols.ss7.map.service.sms.SendRoutingInfoForSMRequestImpl;
import org.restcomm.protocols.ss7.map.service.sms.SendRoutingInfoForSMResponseImpl;
import org.restcomm.protocols.ss7.map.api.MAPOperationCode;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = { "functional.decode", "functional.encode", "service.sms", "dialog", "flat-index", "asn-compat" })
public class SmsFlatAsnCompatibilityTest {

    private String previousFlatFlag;

    @BeforeMethod
    public void rememberFlatFlag() {
        previousFlatFlag = System.getProperty("jss7.asn.flatIndexEnabled");
    }

    @AfterMethod
    public void restoreFlatFlag() {
        if (previousFlatFlag == null) {
            System.clearProperty("jss7.asn.flatIndexEnabled");
        } else {
            System.setProperty("jss7.asn.flatIndexEnabled", previousFlatFlag);
        }
    }

    @Test
    public void forwardShortMessage_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 48, 38, -124, 7, -111, 34, 51, 67, -103, 32, 50, -126, 8, -111, 50, 17, 50, 33, 67, 51,
                -12, 4, 17, 11, 22, 33, 44, 55, 66, 77, 0, 1, 2, 3, 4, 5, 6, 7, 9, 8 };
        assertCompatible(data, ForwardShortMessageRequestImpl::new);
    }

    @Test
    public void mapOpenInfo_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { (byte) 0xa0, (byte) 0x14, (byte) 0x80, 0x09, (byte) 0x96, 0x02, 0x24, (byte) 0x80, 0x03,
                0x00, (byte) 0x80, 0x00, (byte) 0xf2, (byte) 0x81, 0x07, (byte) 0x91, 0x13, 0x26, (byte) 0x98, (byte) 0x86,
                0x03, (byte) 0xf0 };
        assertCompatible(data, MAPOpenInfoImpl::new);
    }

    @Test
    public void moForwardShortMessage_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 48, 38, -124, 7, -111, 34, 51, 67, -103, 32, 50, -126, 8, -111, 50, 17, 50, 33, 67, 51,
                -12, 4, 17, 11, 22, 33, 44, 55, 66, 77, 0, 1, 2, 3, 4, 5, 6, 7, 9, 8 };
        assertCompatible(data, MoForwardShortMessageRequestImpl::new);
    }

    @Test
    public void mtForwardShortMessage_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 48, 38, -124, 7, -111, 34, 51, 67, -103, 32, 50, -126, 8, -111, 50, 17, 50, 33, 67, 51,
                -12, 4, 17, 11, 22, 33, 44, 55, 66, 77, 0, 1, 2, 3, 4, 5, 6, 7, 9, 8 };
        assertCompatible(data, MtForwardShortMessageRequestImpl::new);
    }

    @Test
    public void sendRoutingInfoForSm_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 48, 20, -128, 7, -111, 49, 84, 119, 84, 85, -15, -127, 1, 0, -126, 6, -111, -119, 18,
                17, 51, 51 };
        assertCompatible(data, SendRoutingInfoForSMRequestImpl::new);
    }

    @Test
    public void reportSmDeliveryStatus_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 48, 19, 4, 6, -111, 39, 34, 51, 19, 17, 4, 6, -111, 1, -112, 115, 84, -13, 10, 1, 1 };
        assertCompatible(data, () -> new ReportSMDeliveryStatusRequestImpl(3));
    }

    @Test
    public void informServiceCentre_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 48, 61, 4, 6, -111, 17, 33, 34, 51, -13, 3, 2, 2, 80, 48, 39, -96, 32, 48, 10, 6, 3,
                42, 3, 4, 11, 12, 13, 14, 15, 48, 5, 6, 3, 42, 3, 6, 48, 11, 6, 3, 42, 3, 5, 21, 22, 23, 24, 25, 26, -95,
                3, 31, 32, 33, 2, 2, 2, 43, -128, 2, 1, -68 };
        assertCompatible(data, InformServiceCentreRequestImpl::new);
    }

    @Test
    public void informServiceCentreMwStatusOnly_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 48, 4, 3, 2, 2, 64 };
        assertCompatible(data, InformServiceCentreRequestImpl::new);
    }

    @Test
    public void alertServiceCentre_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 48, 18, 4, 7, -111, -110, 17, 19, 50, 19, -15, 4, 7, -111, -108, -120, 115, 0, -110,
                -14 };
        assertCompatible(data, () -> new AlertServiceCentreRequestImpl(MAPOperationCode.alertServiceCentre));
    }

    @Test
    public void sendRoutingInfoForSmResponse_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 48, 27, 4, 8, 2, -112, 9, 2, 16, 17, 34, -9, -96, 15, -127, 7, -111, 33, 48, 18, 0,
                -110, -11, 4, 4, 0, 3, 98, 49 };
        assertCompatible(data, SendRoutingInfoForSMResponseImpl::new);
    }

    @Test
    public void sendRoutingInfoForSmResponseWithMwdSet_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 48, 22, 4, 7, 82, 0, 17, 17, 17, 17, 17, -96, 8, -127, 6, -111, -105, -103, 25, 17,
                17, -126, 1, 0 };
        assertCompatible(data, SendRoutingInfoForSMResponseImpl::new);
    }

    private static <T extends MAPAsnPrimitive> void assertCompatible(byte[] sequenceTlv, Supplier<T> factory)
            throws Exception {
        T legacy = decodeWithFlatFlag(sequenceTlv, factory, false);
        T flat = decodeWithFlatFlag(sequenceTlv, factory, true);

        AsnOutputStream legacyOut = new AsnOutputStream();
        legacy.encodeAll(legacyOut);
        AsnOutputStream flatOut = new AsnOutputStream();
        flat.encodeAll(flatOut);

        assertTrue(Arrays.equals(legacyOut.toByteArray(), flatOut.toByteArray()),
                "re-encoded bytes differ between legacy and flat decode paths");

        AsnOutputStream wireOut = new AsnOutputStream();
        flat.encodeAll(wireOut);
        assertEquals(wireOut.toByteArray(), sequenceTlv, "flat decode + encode must match original wire bytes");
    }

    private static <T extends MAPAsnPrimitive> T decodeWithFlatFlag(byte[] sequenceTlv, Supplier<T> factory,
            boolean flatEnabled) throws Exception {
        if (flatEnabled) {
            System.setProperty("jss7.asn.flatIndexEnabled", "true");
        } else {
            System.setProperty("jss7.asn.flatIndexEnabled", "false");
        }

        AsnInputStream ais = new AsnInputStream(sequenceTlv);
        ais.readTag();
        T message = factory.get();
        message.decodeAll(ais);
        return message;
    }
}
