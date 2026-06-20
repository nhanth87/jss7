package org.restcomm.protocols.ss7.map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.restcomm.protocols.ss7.map.api.primitives.AlertingCategory;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.primitives.AddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.AlertingPatternImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.ProcessUnstructuredSSRequestImpl;
import org.restcomm.protocols.ss7.map.service.supplementary.ProcessUnstructuredSSResponseImpl;
import org.restcomm.protocols.ss7.utility.SS7XmlMapperFactory;
import org.testng.annotations.Test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Golden round-trip tests for javolution-compatible MAP primitive XML.
 * Fixtures derived from {@code jss7-xmlformats/} javolution XMLFormat references.
 */
public class JavolutionXmlCompatibilityTest {

    private String loadFixture(String name) {
        InputStream in = getClass().getResourceAsStream("/javolution-fixtures/" + name);
        assertNotNull(in, "fixture missing: " + name);
        try (Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next().trim() : "";
        }
    }

    @Test
    public void deserializeAddressStringUppercaseNaiNpi() throws Exception {
        XmlMapper mapper = SS7XmlMapperFactory.createProtocolMapper();
        String xml = loadFixture("address-string-uppercase.xml");

        AddressStringImpl result = mapper.readValue(xml, AddressStringImpl.class);

        assertEquals(result.getAddress(), "79273605819");
        assertEquals(result.getAddressNature(), AddressNature.international_number);
        assertEquals(result.getNumberingPlan(), NumberingPlan.ISDN);
    }

    @Test
    public void deserializeAddressStringLowercaseNaiNpi() throws Exception {
        XmlMapper mapper = SS7XmlMapperFactory.createProtocolMapper();
        String xml = loadFixture("address-string-lowercase.xml");

        AddressStringImpl result = mapper.readValue(xml, AddressStringImpl.class);

        assertEquals(result.getAddress(), "79273605819");
        assertEquals(result.getAddressNature(), AddressNature.international_number);
        assertEquals(result.getNumberingPlan(), NumberingPlan.ISDN);
    }

    @Test
    public void serializeAddressStringUppercaseNaiNpi() throws Exception {
        XmlMapper mapper = MAPJacksonXMLHelper.getXmlMapper();
        AddressStringImpl original = new AddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN,
                "79273605819");

        String xml = mapper.writeValueAsString(original);

        assertTrue(xml.contains("NAI=\"international_number\"") || xml.contains("NAI='international_number'"),
                "expected uppercase NAI attribute, got: " + xml);
        assertTrue(xml.contains("NPI=\"ISDN\"") || xml.contains("NPI='ISDN'"),
                "expected uppercase NPI attribute, got: " + xml);
        assertTrue(xml.contains("number=\"79273605819\"") || xml.contains("number='79273605819'"),
                "expected number attribute, got: " + xml);
    }

    @Test
    public void roundTripAddressStringPreservesValues() throws Exception {
        XmlMapper mapper = MAPJacksonXMLHelper.getXmlMapper();
        AddressStringImpl original = new AddressStringImpl(AddressNature.national_significant_number, NumberingPlan.land_mobile,
                "1234567");

        AddressStringImpl copy = mapper.readValue(mapper.writeValueAsString(original), AddressStringImpl.class);

        assertEquals(copy.getAddress(), original.getAddress());
        assertEquals(copy.getAddressNature(), original.getAddressNature());
        assertEquals(copy.getNumberingPlan(), original.getNumberingPlan());
    }

    @Test
    public void deserializeIsdnAddressStringUppercaseNaiNpi() throws Exception {
        XmlMapper mapper = SS7XmlMapperFactory.createProtocolMapper();
        String xml = loadFixture("isdn-address-string-uppercase.xml");

        ISDNAddressStringImpl result = mapper.readValue(xml, ISDNAddressStringImpl.class);

        assertEquals(result.getAddress(), "79273605819");
        assertEquals(result.getAddressNature(), AddressNature.international_number);
        assertEquals(result.getNumberingPlan(), NumberingPlan.ISDN);
    }

    @Test
    public void deserializeIsdnAddressStringMixedAliases() throws Exception {
        XmlMapper mapper = SS7XmlMapperFactory.createProtocolMapper();
        String xml = loadFixture("isdn-address-string-mixed.xml");

        ISDNAddressStringImpl result = mapper.readValue(xml, ISDNAddressStringImpl.class);

        assertEquals(result.getAddress(), "79273605819");
        assertEquals(result.getAddressNature(), AddressNature.international_number);
        assertEquals(result.getNumberingPlan(), NumberingPlan.ISDN);
    }

    @Test
    public void serializeIsdnAddressStringUppercaseNaiNpi() throws Exception {
        XmlMapper mapper = MAPJacksonXMLHelper.getXmlMapper();
        ISDNAddressStringImpl original = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN,
                "79273605819");

        String xml = mapper.writeValueAsString(original);

        assertTrue(xml.contains("NAI=\"international_number\"") || xml.contains("NAI='international_number'"),
                "expected uppercase NAI attribute, got: " + xml);
        assertTrue(xml.contains("NPI=\"ISDN\"") || xml.contains("NPI='ISDN'"),
                "expected uppercase NPI attribute, got: " + xml);
    }

    @Test
    public void deserializeAlertingPatternCategory3() throws Exception {
        XmlMapper mapper = MAPJacksonXMLHelper.getXmlMapper();
        AlertingPatternImpl result = mapper.readValue(loadFixture("alerting-pattern-category3.xml"), AlertingPatternImpl.class);

        assertEquals(result.getAlertingCategory(), AlertingCategory.Category3);
    }

    @Test
    public void roundTripAlertingPatternPreservesCategory() throws Exception {
        XmlMapper mapper = MAPJacksonXMLHelper.getXmlMapper();
        AlertingPatternImpl original = new AlertingPatternImpl(AlertingCategory.Category3);

        AlertingPatternImpl copy = mapper.readValue(mapper.writeValueAsString(original), AlertingPatternImpl.class);

        assertEquals(copy.getAlertingCategory(), original.getAlertingCategory());
    }

    @Test
    public void deserializeProcessUnstructuredSsResponseJavolutionFixture() throws Exception {
        XmlMapper mapper = MAPJacksonXMLHelper.getXmlMapper();
        ProcessUnstructuredSSResponseImpl result = mapper.readValue(
                loadFixture("process-unstructured-ss-response-javolution.xml"), ProcessUnstructuredSSResponseImpl.class);

        assertEquals(result.getDataCodingScheme().getCode(), 15);
        assertEquals(result.getUSSDString().getString(null), "*234#");
    }

    @Test
    public void roundTripProcessUnstructuredSsResponseFixture() throws Exception {
        XmlMapper mapper = MAPJacksonXMLHelper.getXmlMapper();
        String xml = loadFixture("process-unstructured-ss-response-javolution.xml");

        ProcessUnstructuredSSResponseImpl copy = mapper.readValue(mapper.writeValueAsString(
                mapper.readValue(xml, ProcessUnstructuredSSResponseImpl.class)), ProcessUnstructuredSSResponseImpl.class);

        assertEquals(copy.getDataCodingScheme().getCode(), 15);
        assertEquals(copy.getUSSDString().getString(null), "*234#");
    }

    @Test
    public void deserializeProcessUnstructuredSsRequestJavolutionFixture() throws Exception {
        XmlMapper mapper = MAPJacksonXMLHelper.getXmlMapper();
        ProcessUnstructuredSSRequestImpl result = mapper.readValue(
                loadFixture("process-unstructured-ss-request-javolution.xml"), ProcessUnstructuredSSRequestImpl.class);

        assertEquals(result.getDataCodingScheme().getCode(), 15);
        assertEquals(result.getUSSDString().getString(null), "*234#");
        assertEquals(result.getMSISDNAddressString().getAddress(), "79273605819");
        assertEquals(result.getAlertingPattern().getAlertingCategory(), AlertingCategory.Category3);
    }
}
