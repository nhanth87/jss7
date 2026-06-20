package org.restcomm.protocols.ss7.map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.function.Supplier;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.USSDString;
import org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive;
import org.restcomm.protocols.ss7.map.service.supplementary.ProcessUnstructuredSSRequestImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.ProcessUnstructuredSSResponseImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.SupplementaryMessageImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.UnstructuredSSNotifyRequestImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.UnstructuredSSRequestImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.UnstructuredSSResponseImpl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Verifies flat-index decode ({@code jss7.asn.flatIndexEnabled=true}) produces
 * identical semantic results and wire-compatible re-encode as Mobicents legacy
 * {@link AsnInputStream} object-tree parsing.
 */
@Test(groups = { "functional.decode", "functional.encode", "service.ussd", "flat-index", "asn-compat" })
public class MobicentsAsnFlatLegacyCompatibilityTest {

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
    public void processUnstructuredSSRequest_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 0x30, 0x0a, 0x04, 0x01, 0x0f, 0x04, 0x05, 0x2a, (byte) 0xd9, (byte) 0x8c, 0x36, 0x02 };
        assertUssdMessageCompatible(data, ProcessUnstructuredSSRequestImpl::new, false);
    }

    @Test
    public void processUnstructuredSSResponse_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 0x30, 0x15, 0x04, 0x01, 0x0f, 0x04, 0x10, (byte) 0xd9, 0x77, 0x5d, 0x0e, 0x12,
                (byte) 0x87, (byte) 0xd9, 0x61, (byte) 0xf7, (byte) 0xb8, 0x0c, (byte) 0xea, (byte) 0x81, 0x66, 0x35, 0x18 };
        assertUssdMessageCompatible(data, ProcessUnstructuredSSResponseImpl::new, false);
    }

    @Test
    public void unstructuredSSRequest_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 0x30, 0x3e, 0x04, 0x01, 0x0f, 0x04, 0x39, (byte) 0xd5, (byte) 0xe9, (byte) 0x94, 0x08,
                (byte) 0x9a, (byte) 0xd2, (byte) 0xe5, 0x69, (byte) 0xf7, 0x19, (byte) 0xa4, 0x03, 0x21, (byte) 0xcb, 0x6c,
                (byte) 0xf6, 0x1b, 0x74, 0x7d, (byte) 0xcb, (byte) 0xd9, 0x64, 0x10, 0x6f, 0x28, (byte) 0xf5, (byte) 0x81,
                0x62, 0x2e, (byte) 0x90, 0x30, (byte) 0xcc, 0x0e, (byte) 0xbb, (byte) 0xc7, 0x65, 0x10, 0x6f, 0x28,
                (byte) 0xf5, (byte) 0x81, 0x64, 0x2e, 0x10, (byte) 0xb5, (byte) 0x8c, (byte) 0xa7, (byte) 0xcf, 0x41,
                (byte) 0xd2, 0x72, 0x3b, (byte) 0x9c, 0x76, (byte) 0xa7, (byte) 0xdd, 0x67 };
        assertUssdMessageCompatible(data, UnstructuredSSRequestImpl::new, false);
    }

    @Test
    public void unstructuredSSResponse_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 0x30, 0x0a, 0x04, 0x01, 0x0f, 0x04, 0x05, 0x2a, (byte) 0xd9, (byte) 0x8c, 0x36, 0x02 };
        assertUssdMessageCompatible(data, UnstructuredSSResponseImpl::new, false);
    }

    @Test
    public void unstructuredSSNotifyRequest_legacyMatchesFlat() throws Exception {
        byte[] data = new byte[] { 0x30, 0x0a, 0x04, 0x01, 0x0f, 0x04, 0x05, 0x2a, (byte) 0xd9, (byte) 0x8c, 0x36, 0x02 };
        assertUssdMessageCompatible(data, UnstructuredSSNotifyRequestImpl::new, false);
    }

    @Test
    public void flatDecodeReencodeMatchesOriginalWire() throws Exception {
        byte[] data = new byte[] { 0x30, 0x15, 0x04, 0x01, 0x0f, 0x04, 0x10, (byte) 0xd9, 0x77, 0x5d, 0x0e, 0x12,
                (byte) 0x87, (byte) 0xd9, 0x61, (byte) 0xf7, (byte) 0xb8, 0x0c, (byte) 0xea, (byte) 0x81, 0x66, 0x35, 0x18 };

        ProcessUnstructuredSSResponseImpl flatDecoded = decodeWithFlat(data, ProcessUnstructuredSSResponseImpl::new);
        AsnOutputStream aos = new AsnOutputStream();
        flatDecoded.encodeAll(aos);
        assertTrue(Arrays.equals(data, aos.toByteArray()), "flat decode + encode must match original Mobicents wire bytes");
    }

    private static <T extends SupplementaryMessageImpl & MAPAsnPrimitive> void assertUssdMessageCompatible(byte[] sequenceTlv,
            Supplier<T> factory, boolean withOptionalFields) throws Exception {
        T legacy = decodeWithFlatFlag(sequenceTlv, factory, false);
        T flat = decodeWithFlatFlag(sequenceTlv, factory, true);

        assertEquals(flat.getDataCodingScheme().getCode(), legacy.getDataCodingScheme().getCode(), "DCS mismatch");
        assertUssdStringEquals(legacy.getUSSDString(), flat.getUSSDString());

        if (withOptionalFields) {
            assertOptionalAddressEquals(legacy, flat);
        }

        AsnOutputStream legacyOut = new AsnOutputStream();
        legacy.encodeAll(legacyOut);
        AsnOutputStream flatOut = new AsnOutputStream();
        flat.encodeAll(flatOut);

        assertTrue(Arrays.equals(legacyOut.toByteArray(), flatOut.toByteArray()),
                "re-encoded bytes differ between legacy and flat decode paths");
    }

    private static <T extends SupplementaryMessageImpl & MAPAsnPrimitive> T decodeWithFlat(byte[] sequenceTlv,
            Supplier<T> factory) throws Exception {
        return decodeWithFlatFlag(sequenceTlv, factory, true);
    }

    private static <T extends SupplementaryMessageImpl & MAPAsnPrimitive> T decodeWithFlatFlag(byte[] sequenceTlv,
            Supplier<T> factory, boolean flatEnabled) throws Exception {
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

    private static void assertUssdStringEquals(USSDString legacy, USSDString flat) throws Exception {
        assertNotNull(legacy);
        assertNotNull(flat);
        assertEquals(flat.getString(null), legacy.getString(null), "USSD text mismatch");
    }

    private static void assertOptionalAddressEquals(SupplementaryMessageImpl legacy, SupplementaryMessageImpl flat) {
        if (legacy instanceof ProcessUnstructuredSSRequestImpl) {
            ISDNAddressString lMsisdn = ((ProcessUnstructuredSSRequestImpl) legacy).getMSISDNAddressString();
            ISDNAddressString fMsisdn = ((ProcessUnstructuredSSRequestImpl) flat).getMSISDNAddressString();
            if (lMsisdn == null) {
                assertNull(fMsisdn);
            } else {
                assertEquals(fMsisdn.getAddress(), lMsisdn.getAddress());
            }
        }
    }
}
