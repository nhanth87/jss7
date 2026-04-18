package org.restcomm.protocols.ss7.map.load;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.restcomm.protocols.ss7.map.MAPJacksonXMLHelper;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.SupportedCamelPhasesImpl;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Low-traffic sanity test to verify Jackson XML serialization works correctly
 * in the map-load module context after the Javolution → Jackson migration.
 */
public class MapLoadJacksonSanityTest {

    @Test
    public void testISDNAddressStringRoundTrip() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        ISDNAddressStringImpl original = new ISDNAddressStringImpl(
                AddressNature.international_number, NumberingPlan.ISDN, "2207750007");

        String xml = xmlMapper.writeValueAsString(original);
        ISDNAddressStringImpl copy = xmlMapper.readValue(xml, ISDNAddressStringImpl.class);

        assertEquals(copy.getAddress(), original.getAddress());
        assertEquals(copy.getAddressNature(), original.getAddressNature());
        assertEquals(copy.getNumberingPlan(), original.getNumberingPlan());
    }

    @Test
    public void testSupportedCamelPhasesRoundTrip() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();
        SupportedCamelPhasesImpl original = new SupportedCamelPhasesImpl(true, false, true, false);

        String xml = xmlMapper.writeValueAsString(original);
        SupportedCamelPhasesImpl copy = xmlMapper.readValue(xml, SupportedCamelPhasesImpl.class);

        assertEquals(copy.getPhase1Supported(), original.getPhase1Supported());
        assertEquals(copy.getPhase2Supported(), original.getPhase2Supported());
        assertEquals(copy.getPhase3Supported(), original.getPhase3Supported());
        assertEquals(copy.getPhase4Supported(), original.getPhase4Supported());
    }

    @Test(invocationCount = 5, threadPoolSize = 2)
    public void testLowTrafficStressISDNAddressString() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();

        for (int i = 0; i < 20; i++) {
            ISDNAddressStringImpl original = new ISDNAddressStringImpl(
                    AddressNature.international_number, NumberingPlan.ISDN, "220775" + String.format("%04d", i));
            String xml = xmlMapper.writeValueAsString(original);
            ISDNAddressStringImpl copy = xmlMapper.readValue(xml, ISDNAddressStringImpl.class);
            assertEquals(copy.getAddress(), original.getAddress());
        }
    }

    @Test(invocationCount = 5, threadPoolSize = 2)
    public void testLowTrafficStressCamelPhases() throws Exception {
        XmlMapper xmlMapper = MAPJacksonXMLHelper.getXmlMapper();

        for (int i = 0; i < 20; i++) {
            SupportedCamelPhasesImpl original = new SupportedCamelPhasesImpl(
                    (i & 1) == 1, (i & 2) == 2, (i & 4) == 4, (i & 8) == 8);
            String xml = xmlMapper.writeValueAsString(original);
            SupportedCamelPhasesImpl copy = xmlMapper.readValue(xml, SupportedCamelPhasesImpl.class);
            assertEquals(copy.getPhase1Supported(), original.getPhase1Supported());
            assertEquals(copy.getPhase2Supported(), original.getPhase2Supported());
            assertEquals(copy.getPhase3Supported(), original.getPhase3Supported());
            assertEquals(copy.getPhase4Supported(), original.getPhase4Supported());
        }
    }
}
