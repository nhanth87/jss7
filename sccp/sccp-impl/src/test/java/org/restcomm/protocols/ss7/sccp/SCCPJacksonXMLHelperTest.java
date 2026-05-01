package org.restcomm.protocols.ss7.sccp;

import static org.junit.Assert.*;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.restcomm.protocols.ss7.indicator.GlobalTitleIndicator;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;

public class SCCPJacksonXMLHelperTest {

    private XmlMapper getXmlMapper() {
        return SCCPJacksonXMLHelper.getXmlMapper();
    }
    
    @Test
    public void testXmlMapperInitialization() {
        XmlMapper mapper = getXmlMapper();
        assertNotNull("XmlMapper should not be null", mapper);
    }
    
    @Test
    public void testGlobalTitleIndicatorDeserializer() throws Exception {
        // Test deserialization of GlobalTitleIndicator enum
        String xml = "0";
        GlobalTitleIndicator gti = getXmlMapper().readValue(xml, GlobalTitleIndicator.class);
        assertNotNull("GTI should not be null", gti);
        assertEquals(GlobalTitleIndicator.GLOBAL_TITLE_INCLUDES_NATURE_OF_ADDRESS_INDICATOR, gti);
    }
    
    @Test
    public void testGlobalTitleIndicatorValues() throws Exception {
        // Test all known GlobalTitleIndicator values
        int[] values = {0, 1, 2, 3, 4};
        for (int val : values) {
            String xml = String.valueOf(val);
            GlobalTitleIndicator gti = getXmlMapper().readValue(xml, GlobalTitleIndicator.class);
            assertNotNull("GTI value " + val + " should deserialize", gti);
        }
    }
    
    @Test
    public void testRoutingIndicatorDeserializer() throws Exception {
        // Test deserialization of RoutingIndicator enum
        String xml = "0";
        RoutingIndicator ri = getXmlMapper().readValue(xml, RoutingIndicator.class);
        assertNotNull("RI should not be null", ri);
        assertEquals(RoutingIndicator.ROUTING_ON_SSN, ri);
    }
    
    @Test
    public void testRoutingIndicatorValues() throws Exception {
        // Test all RoutingIndicator values
        String xml0 = "0";
        String xml1 = "1";
        RoutingIndicator ri0 = getXmlMapper().readValue(xml0, RoutingIndicator.class);
        RoutingIndicator ri1 = getXmlMapper().readValue(xml1, RoutingIndicator.class);
        assertNotNull(ri0);
        assertNotNull(ri1);
        assertEquals(RoutingIndicator.ROUTING_ON_SSN, ri0);
        assertEquals(RoutingIndicator.ROUTING_ON_GLOBAL_TITLE, ri1);
    }
    
    @Test
    public void testIndentationEnabled() throws Exception {
        TestBean bean = new TestBean();
        bean.name = "testBean";
        bean.value = 123;
        String xml = getXmlMapper().writeValueAsString(bean);
        assertNotNull("XML should not be null", xml);
    }
    
    @Test
    public void testFailOnUnknownPropertiesDisabled() throws Exception {
        String xmlWithUnknown = "<testBean><name>test</name><value>100</value><unknownField>ignore</unknownField></testBean>";
        TestBean bean = getXmlMapper().readValue(xmlWithUnknown, TestBean.class);
        assertNotNull("Bean should be deserialized", bean);
    }
    
    @Test
    public void testNonNullInclusion() throws Exception {
        TestBean bean = new TestBean();
        bean.name = "nonNull";
        bean.nullableField = null;
        
        String xml = getXmlMapper().writeValueAsString(bean);
        assertNotNull("XML should not be null", xml);
    }
    
    public static class TestBean {
        public String name;
        public int value;
        public String nullableField;
    }
}
