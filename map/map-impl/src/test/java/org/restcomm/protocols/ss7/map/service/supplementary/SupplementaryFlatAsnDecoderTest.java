package org.restcomm.protocols.ss7.map.service.supplementary;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.mobicents.protocols.asn.AsnInputStream;
import org.restcomm.protocols.ss7.map.primitives.USSDStringImpl;
import org.testng.annotations.Test;

/**
 * Zero-GC flat ASN.1 decode tests for all USSD supplementary MAP messages.
 */
public class SupplementaryFlatAsnDecoderTest {

    private static void withFlatIndex(Runnable test) {
        String previous = System.getProperty("jss7.asn.flatIndexEnabled");
        try {
            System.setProperty("jss7.asn.flatIndexEnabled", "true");
            test.run();
        } finally {
            if (previous == null) {
                System.clearProperty("jss7.asn.flatIndexEnabled");
            } else {
                System.setProperty("jss7.asn.flatIndexEnabled", previous);
            }
        }
    }

    @Test(groups = { "functional.decode", "service.ussd", "flat-index" })
    public void testProcessUnstructuredSSRequestFlat() throws Exception {
        withFlatIndex(() -> {
            try {
                byte[] data = new byte[] { 0x30, 0x0a, 0x04, 0x01, 0x0f, 0x04, 0x05, 0x2a, (byte) 0xd9, (byte) 0x8c, 0x36, 0x02 };
                AsnInputStream asn = new AsnInputStream(data);
                asn.readTag();
                ProcessUnstructuredSSRequestImpl req = new ProcessUnstructuredSSRequestImpl();
                req.decodeAll(asn);
                assertEquals(req.getDataCodingScheme().getCode(), 0x0f);
                assertEquals(req.getUSSDString().getString(null), "*234#");
                assertTrue(((USSDStringImpl) req.getUSSDString()).hasDataView());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test(groups = { "functional.decode", "service.ussd", "flat-index" })
    public void testProcessUnstructuredSSResponseFlat() throws Exception {
        withFlatIndex(() -> {
            try {
                byte[] data = new byte[] { 0x30, 0x15, 0x04, 0x01, 0x0f, 0x04, 0x10, (byte) 0xd9, 0x77, 0x5d, 0x0e, 0x12,
                        (byte) 0x87, (byte) 0xd9, 0x61, (byte) 0xf7, (byte) 0xb8, 0x0c, (byte) 0xea, (byte) 0x81, 0x66, 0x35,
                        0x18 };
                AsnInputStream asn = new AsnInputStream(data);
                asn.readTag();
                ProcessUnstructuredSSResponseImpl resp = new ProcessUnstructuredSSResponseImpl();
                resp.decodeAll(asn);
                assertEquals(resp.getDataCodingScheme().getCode(), 0x0f);
                assertEquals(resp.getUSSDString().getString(null), "Your balance = 350");
                assertTrue(((USSDStringImpl) resp.getUSSDString()).hasDataView());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test(groups = { "functional.decode", "service.ussd", "flat-index" })
    public void testUnstructuredSSRequestFlat() throws Exception {
        withFlatIndex(() -> {
            try {
                byte[] data = new byte[] { 0x30, 0x3e, 0x04, 0x01, 0x0f, 0x04, 0x39, (byte) 0xd5, (byte) 0xe9, (byte) 0x94,
                        0x08, (byte) 0x9a, (byte) 0xd2, (byte) 0xe5, 0x69, (byte) 0xf7, 0x19, (byte) 0xa4, 0x03, 0x21,
                        (byte) 0xcb, 0x6c, (byte) 0xf6, 0x1b, 0x74, 0x7d, (byte) 0xcb, (byte) 0xd9, 0x64, 0x10, 0x6f, 0x28,
                        (byte) 0xf5, (byte) 0x81, 0x62, 0x2e, (byte) 0x90, 0x30, (byte) 0xcc, 0x0e, (byte) 0xbb, (byte) 0xc7,
                        0x65, 0x10, 0x6f, 0x28, (byte) 0xf5, (byte) 0x81, 0x64, 0x2e, 0x10, (byte) 0xb5, (byte) 0x8c,
                        (byte) 0xa7, (byte) 0xcf, 0x41, (byte) 0xd2, 0x72, 0x3b, (byte) 0x9c, 0x76, (byte) 0xa7, (byte) 0xdd,
                        0x67 };
                AsnInputStream asn = new AsnInputStream(data);
                asn.readTag();
                UnstructuredSSRequestImpl req = new UnstructuredSSRequestImpl();
                req.decodeAll(asn);
                assertEquals(req.getDataCodingScheme().getCode(), 0x0f);
                assertNotNull(req.getUSSDString());
                assertTrue(((USSDStringImpl) req.getUSSDString()).hasDataView());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test(groups = { "functional.decode", "service.ussd", "flat-index" })
    public void testUnstructuredSSResponseFlat() throws Exception {
        withFlatIndex(() -> {
            try {
                byte[] data = new byte[] { 0x30, 0x0a, 0x04, 0x01, 0x0f, 0x04, 0x05, 0x2a, (byte) 0xd9, (byte) 0x8c, 0x36, 0x02 };
                AsnInputStream asn = new AsnInputStream(data);
                asn.readTag();
                UnstructuredSSResponseImpl resp = new UnstructuredSSResponseImpl();
                resp.decodeAll(asn);
                assertEquals(resp.getDataCodingScheme().getCode(), 0x0f);
                assertEquals(resp.getUSSDString().getString(null), "*234#");
                assertTrue(((USSDStringImpl) resp.getUSSDString()).hasDataView());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
