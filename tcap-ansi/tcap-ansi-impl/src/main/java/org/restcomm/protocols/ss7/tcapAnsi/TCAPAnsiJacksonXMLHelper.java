package org.restcomm.protocols.ss7.tcapAnsi;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import java.io.*;

/**
 * Jackson XML helper for TCAP ANSI module XML serialization.
 * Replaces XStream for better performance and Java 17+ compatibility.
 */
public class TCAPAnsiJacksonXMLHelper {
    private static final XmlMapper xmlMapper;

    static {
        XmlFactory factory = new XmlFactory(
            new com.ctc.wstx.stax.WstxInputFactory(),
            new com.ctc.wstx.stax.WstxOutputFactory()
        );
        xmlMapper = new XmlMapper(factory);
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);
        // INDENT_OUTPUT disabled to avoid Stax2WriterAdapter.writeRaw() UnsupportedOperationException
        // with Jackson-dataformat-xml 2.15.2 + StAX on WildFly 10
        // xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Remove default pretty printer to prevent Stax2WriterAdapter.writeRaw() exception on WildFly 10
        xmlMapper.setDefaultPrettyPrinter(null);
    }

    public static XmlMapper getXmlMapper() {
        return xmlMapper;
    }

    public static void toXML(Object obj, Writer writer) {
        try {
            xmlMapper.writeValue(writer, obj);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing to XML", e);
        }
    }

    public static String toXML(Object obj) {
        try {
            return xmlMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing to XML", e);
        }
    }

    public static Object fromXML(Reader reader) {
        try {
            return xmlMapper.readValue(reader, Object.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing from XML", e);
        }
    }

    public static Object fromXML(String xml) {
        try {
            return xmlMapper.readValue(xml, Object.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing from XML", e);
        }
    }
}
