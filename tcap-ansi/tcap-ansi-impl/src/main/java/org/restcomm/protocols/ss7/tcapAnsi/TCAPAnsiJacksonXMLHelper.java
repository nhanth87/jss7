package org.restcomm.protocols.ss7.tcapAnsi;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.*;

import org.restcomm.protocols.ss7.utility.SS7XmlMapperFactory;

/**
 * Jackson XML helper for TCAP ANSI module XML serialization.
 * Replaces XStream for better performance and Java 17+ compatibility.
 */
public class TCAPAnsiJacksonXMLHelper {
    private static final XmlMapper xmlMapper;

    static {
        xmlMapper = SS7XmlMapperFactory.createSccpStackConfigMapper();
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
