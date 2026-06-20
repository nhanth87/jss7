package org.restcomm.protocols.ss7.oam.common.jmx;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.restcomm.protocols.ss7.utility.SS7XmlMapperFactory;

/**
 * Jackson XML helper for OAM module XML serialization.
 * Replaces XStream for better performance and Java 17+ compatibility.
 */
public class OAMJacksonXMLHelper {
    private static final XmlMapper xmlMapper = SS7XmlMapperFactory.createSccpStackConfigMapper();

    public static XmlMapper getXmlMapper() {
        return xmlMapper;
    }

    public static String toXML(Object obj) {
        try {
            return xmlMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing to XML", e);
        }
    }

    public static Object fromXML(String xml) {
        try {
            return xmlMapper.readValue(xml, Object.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing from XML", e);
        }
    }
}