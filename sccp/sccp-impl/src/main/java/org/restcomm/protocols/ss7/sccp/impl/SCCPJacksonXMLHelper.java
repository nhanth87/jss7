package org.restcomm.protocols.ss7.sccp.impl;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import java.io.*;

import org.restcomm.protocols.ss7.sccp.impl.router.LongMessageRuleMap;
import org.restcomm.protocols.ss7.sccp.impl.router.Mtp3DestinationMap;
import org.restcomm.protocols.ss7.sccp.impl.router.Mtp3ServiceAccessPointMap;
import org.restcomm.protocols.ss7.sccp.impl.router.RouterImpl;

/**
 * Jackson XML helper for SCCP module XML serialization.
 * Replaces XStream for better performance and security.
 */
public class SCCPJacksonXMLHelper {
    private static final XmlMapper xmlMapper = new XmlMapper();
    
    static {
        // Configure XmlMapper
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        // Register aliases for SCCP Resource classes via Jackson mixin annotations
        // or by using @JacksonXmlRootElement annotations on the classes themselves
    }
    
    public static XmlMapper getXmlMapper() {
        return xmlMapper;
    }
    
    public static void toXML(Object obj, Writer writer) throws IOException {
        xmlMapper.writeValue(writer, obj);
    }
    
    public static String toXML(Object obj) throws IOException {
        return xmlMapper.writeValueAsString(obj);
    }
    
    public static <T> T fromXML(Reader reader, Class<T> valueType) throws IOException {
        return xmlMapper.readValue(reader, valueType);
    }
    
    public static <T> T fromXML(String xml, Class<T> valueType) throws IOException {
        return xmlMapper.readValue(xml, valueType);
    }
    
    @Deprecated
    public static Object fromXML(Reader reader) throws IOException {
        // This method is deprecated because Jackson needs type information
        // Use the typed version fromXML(Reader, Class) instead
        throw new UnsupportedOperationException("Use fromXML(Reader, Class<T>) instead for type safety");
    }
    
    @Deprecated
    public static Object fromXML(String xml) throws IOException {
        // This method is deprecated because Jackson needs type information
        // Use the typed version fromXML(String, Class) instead
        throw new UnsupportedOperationException("Use fromXML(String, Class<T>) instead for type safety");
    }
}
