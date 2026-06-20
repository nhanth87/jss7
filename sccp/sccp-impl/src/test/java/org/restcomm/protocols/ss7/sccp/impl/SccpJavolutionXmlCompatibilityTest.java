package org.restcomm.protocols.ss7.sccp.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.sccp.SCCPJacksonXMLHelper;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0100Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.testng.annotations.Test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Golden round-trip tests for javolution-compatible SCCP parameter XML.
 * Fixtures derived from {@code jss7-xmlformats/} and production Jackson output.
 */
public class SccpJavolutionXmlCompatibilityTest {

    private String loadFixture(String name) {
        InputStream in = getClass().getResourceAsStream("/javolution-fixtures/" + name);
        assertNotNull(in, "fixture missing: " + name);
        try (Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next().trim() : "";
        }
    }

    @Test
    public void deserializeGlobalTitle0100Fixture() throws Exception {
        XmlMapper mapper = SCCPJacksonXMLHelper.getXmlMapper();
        GlobalTitle0100Impl gt = mapper.readValue(loadFixture("global-title-0100.xml"), GlobalTitle0100Impl.class);

        assertEquals(gt.getDigits(), "9023629581");
        assertEquals(gt.getTranslationType(), 0);
        assertEquals(gt.getNatureOfAddress(), NatureOfAddress.NATIONAL);
        assertEquals(gt.getNumberingPlan(), NumberingPlan.ISDN_TELEPHONY);
    }

    @Test
    public void roundTripGlobalTitle0100Fixture() throws Exception {
        XmlMapper mapper = SCCPJacksonXMLHelper.getXmlMapper();
        GlobalTitle0100Impl original = mapper.readValue(loadFixture("global-title-0100.xml"), GlobalTitle0100Impl.class);

        GlobalTitle0100Impl copy = mapper.readValue(mapper.writeValueAsString(original), GlobalTitle0100Impl.class);

        assertEquals(copy.getDigits(), original.getDigits());
        assertEquals(copy.getTranslationType(), original.getTranslationType());
        assertEquals(copy.getNatureOfAddress(), original.getNatureOfAddress());
        assertEquals(copy.getNumberingPlan(), original.getNumberingPlan());
    }

    @Test
    public void deserializeSccpAddressGt0100Fixture() throws Exception {
        XmlMapper mapper = SCCPJacksonXMLHelper.getXmlMapper();
        SccpAddress address = mapper.readValue(loadFixture("sccp-address-gt0100.xml"), SccpAddressImpl.class);

        assertEquals(address.getSignalingPointCode(), 146);
        assertEquals(address.getSubsystemNumber(), 8);
        assertNotNull(address.getGlobalTitle());
        assertEquals(address.getGlobalTitle().getDigits(), "9023629581");
    }

    @Test
    public void roundTripSccpAddressGt0100Fixture() throws Exception {
        XmlMapper mapper = SCCPJacksonXMLHelper.getXmlMapper();
        SccpAddressImpl original = mapper.readValue(loadFixture("sccp-address-gt0100.xml"), SccpAddressImpl.class);

        SccpAddressImpl copy = mapper.readValue(mapper.writeValueAsString(original), SccpAddressImpl.class);

        assertEquals(copy.getSignalingPointCode(), original.getSignalingPointCode());
        assertEquals(copy.getSubsystemNumber(), original.getSubsystemNumber());
        assertEquals(copy.getGlobalTitle().getDigits(), original.getGlobalTitle().getDigits());
    }
}
