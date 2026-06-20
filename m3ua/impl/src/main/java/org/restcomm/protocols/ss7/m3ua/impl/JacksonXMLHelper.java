package org.restcomm.protocols.ss7.m3ua.impl;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Jackson XML helper for M3UA module XML serialization.
 * Replaces XStream to avoid Java module system issues.
 * @deprecated Use {@link M3UAJacksonXMLHelper} instead
 */
@Deprecated
public class JacksonXMLHelper {

    public static XmlMapper getXmlMapper() {
        return M3UAJacksonXMLHelper.getXmlMapper();
    }

    public static void toXML(Object obj, Writer writer) throws IOException {
        M3UAJacksonXMLHelper.toXML(obj, writer);
    }

    public static String toXML(Object obj) throws IOException {
        return M3UAJacksonXMLHelper.toXML(obj);
    }

    public static <T> T fromXML(Reader reader, Class<T> clazz) throws IOException {
        return M3UAJacksonXMLHelper.fromXML(reader, clazz);
    }

    public static <T> T fromXML(String xml, Class<T> clazz) throws IOException {
        return M3UAJacksonXMLHelper.fromXML(xml, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromXML(String xml) throws IOException {
        return (T) M3UAJacksonXMLHelper.fromXML(xml);
    }
}
