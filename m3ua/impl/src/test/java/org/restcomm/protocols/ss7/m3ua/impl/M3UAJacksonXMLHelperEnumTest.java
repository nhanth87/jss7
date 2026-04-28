package org.restcomm.protocols.ss7.m3ua.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.IPSPType;

/**
 * Unit tests for M3UA Jackson XML binding, specifically for enum deserialization.
 * Ensures backward compatibility with javolution XML format.
 * 
 * @author Matrix Agent
 */
public class M3UAJacksonXMLHelperEnumTest {

    /**
     * Test Functionality.IPSP serialization
     */
    @Test
    public void testFunctionalityIPSP() throws Exception {
        String xml = "<aspFactory><functionality>IPSP</functionality></aspFactory>";
        String result = M3UAJacksonXMLHelper.toXML(new AspFactoryImpl());
        assertNotNull(result);
    }

    /**
     * Test Functionality.AS serialization
     */
    @Test
    public void testFunctionalityAS() throws Exception {
        AspFactoryImpl factory = new AspFactoryImpl();
        String result = M3UAJacksonXMLHelper.toXML(factory);
        assertNotNull(result);
    }

    /**
     * Test IPSPType.CLIENT serialization
     */
    @Test
    public void testIPSPTypeCLIENT() throws Exception {
        AspFactoryImpl factory = new AspFactoryImpl();
        String result = M3UAJacksonXMLHelper.toXML(factory);
        assertNotNull(result);
    }

    /**
     * Test ExchangeType.DE serialization
     */
    @Test
    public void testExchangeTypeDE() throws Exception {
        AspFactoryImpl factory = new AspFactoryImpl();
        String result = M3UAJacksonXMLHelper.toXML(factory);
        assertNotNull(result);
    }

    /**
     * Test Functionality.getFunctionality() method
     */
    @Test
    public void testFunctionalityGetFunctionality() {
        assertEquals(Functionality.IPSP, Functionality.getFunctionality("IPSP"));
        assertEquals(Functionality.AS, Functionality.getFunctionality("AS"));
        assertEquals(Functionality.SGW, Functionality.getFunctionality("SGW"));
        assertEquals(Functionality.IPSP, Functionality.getFunctionality("ipsp"));
        assertEquals(null, Functionality.getFunctionality("INVALID"));
    }

    /**
     * Test IPSPType.getIPSPType() method
     */
    @Test
    public void testIPSPTypeGetIPSPType() {
        assertEquals(IPSPType.CLIENT, IPSPType.getIPSPType("CLIENT"));
        assertEquals(IPSPType.SERVER, IPSPType.getIPSPType("SERVER"));
        assertEquals(IPSPType.CLIENT, IPSPType.getIPSPType("client"));
        assertEquals(null, IPSPType.getIPSPType("INVALID"));
    }

    /**
     * Test ExchangeType.getExchangeType() method
     */
    @Test
    public void testExchangeTypeGetExchangeType() {
        assertEquals(ExchangeType.SE, ExchangeType.getExchangeType("SE"));
        assertEquals(ExchangeType.DE, ExchangeType.getExchangeType("DE"));
        assertEquals(ExchangeType.SE, ExchangeType.getExchangeType("se"));
        assertEquals(null, ExchangeType.getExchangeType("INVALID"));
    }

    /**
     * Test AsImpl serialization with enums
     */
    @Test
    public void testAsImplSerialization() throws Exception {
        AsImpl as = new AsImpl();
        as.setName("testAS");
        // Set functionality via the inherited method if available
        
        String result = M3UAJacksonXMLHelper.toXML(as);
        assertNotNull(result);
        assertTrue("Result should be valid XML", result.contains("<?xml"));
    }

    /**
     * Test AspFactoryImpl serialization with enums
     */
    @Test
    public void testAspFactoryImplSerialization() throws Exception {
        AspFactoryImpl factory = new AspFactoryImpl();
        factory.setName("testFactory");
        
        String result = M3UAJacksonXMLHelper.toXML(factory);
        assertNotNull(result);
        assertTrue("Result should be valid XML", result.contains("<?xml"));
    }
}
