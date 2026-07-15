package org.restcomm.protocols.ss7.cap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

/**
 * Jackson XML helper for CAP module XML serialization.
 * Replaces XStream XML serialization.
 */
public class CAPJacksonXMLHelper {
    private static final XmlMapper xmlMapper = new XmlMapper();
    
    static {
        // Configure XML mapper
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }
    
    public static XmlMapper getXmlMapper() {
        return xmlMapper;
    }
    
    public static String toXML(Object obj) {
        try {
            return xmlMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing to XML", e);
        }
    }
    
    public static <T> T fromXML(String xml, Class<T> clazz) {
        try {
            return xmlMapper.readValue(xml, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing from XML", e);
        }
    }
    
    public static Object fromXML(String xml) {
        try {
            return xmlMapper.readValue(xml, Object.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing from XML", e);
        }
    }
}
