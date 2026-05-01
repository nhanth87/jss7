package org.restcomm.protocols.ss7.tcap;

import static org.junit.Assert.*;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class TCAPJacksonXMLHelperTest {

    private XmlMapper getXmlMapper() {
        return TCAPJacksonXMLHelper.getXmlMapper();
    }
    
    @Test
    public void testXmlMapperInitialization() {
        XmlMapper mapper = getXmlMapper();
        assertNotNull("XmlMapper should not be null", mapper);
    }
    
    @Test
    public void testIndentationEnabled() throws Exception {
        TestBean bean = new TestBean();
        bean.name = "testBean";
        bean.value = 123;
        String xml = TCAPJacksonXMLHelper.toXML(bean);
        assertNotNull("XML should not be null", xml);
    }
    
    @Test
    public void testToXMLWriter() throws Exception {
        TestBean bean = new TestBean();
        bean.name = "writerTest";
        bean.value = 456;
        StringWriter writer = new StringWriter();
        TCAPJacksonXMLHelper.toXML(bean, writer);
        String xml = writer.toString();
        assertNotNull("XML should not be null", xml);
    }
    
    @Test
    public void testFromXMLReader() throws Exception {
        String xml = "<testBean><name>readerTest</name><value>999</value></testBean>";
        TestBean bean = TCAPJacksonXMLHelper.fromXML(new StringReader(xml), TestBean.class);
        assertNotNull(bean);
        assertEquals("readerTest", bean.name);
    }
    
    @Test
    public void testRoundTrip() throws Exception {
        TestBean original = new TestBean();
        original.name = "roundTrip";
        original.value = 12345;
        String xml = TCAPJacksonXMLHelper.toXML(original);
        TestBean deserialized = TCAPJacksonXMLHelper.fromXML(xml, TestBean.class);
        assertEquals(original.name, deserialized.name);
    }
    
    public static class TestBean {
        public String name;
        public int value;
    }
}
