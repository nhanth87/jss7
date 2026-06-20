package org.restcomm.protocols.ss7.tcap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.StringReader;
import java.io.StringWriter;

import org.testng.annotations.Test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class TCAPJacksonXMLHelperTest {

    private XmlMapper getXmlMapper() {
        return TCAPJacksonXMLHelper.getXmlMapper();
    }

    @Test
    public void testXmlMapperInitialization() {
        assertNotNull(getXmlMapper());
    }

    @Test
    public void testToXML() throws Exception {
        TestBean bean = new TestBean();
        bean.name = "testBean";
        bean.value = 123;
        assertNotNull(TCAPJacksonXMLHelper.toXML(bean));
    }

    @Test
    public void testToXMLWriter() throws Exception {
        TestBean bean = new TestBean();
        bean.name = "writerTest";
        bean.value = 456;
        StringWriter writer = new StringWriter();
        TCAPJacksonXMLHelper.toXML(bean, writer);
        assertNotNull(writer.toString());
    }

    @Test
    public void testFromXMLReader() throws Exception {
        String xml = "<testBean><name>readerTest</name><value>999</value></testBean>";
        TestBean bean = TCAPJacksonXMLHelper.fromXML(new StringReader(xml), TestBean.class);
        assertNotNull(bean);
        assertEquals(bean.name, "readerTest");
    }

    @Test
    public void testRoundTrip() throws Exception {
        TestBean original = new TestBean();
        original.name = "roundTrip";
        original.value = 12345;
        String xml = TCAPJacksonXMLHelper.toXML(original);
        TestBean deserialized = TCAPJacksonXMLHelper.fromXML(xml, TestBean.class);
        assertEquals(deserialized.name, original.name);
    }

    public static class TestBean {
        public String name;
        public int value;
    }
}
