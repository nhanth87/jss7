package org.restcomm.protocols.ss7.tools.simulator.management;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Modifier;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import org.restcomm.protocols.ss7.tools.simulator.common.ConfigurationData;

/**
 * Jackson XML helper for TOOLS simulator module XML serialization.
 * Replaces XStream for better performance and Java 17+ compatibility.
 */
public class ToolsJacksonXMLHelper {
    private static final XmlMapper xmlMapper;

    /**
     * Skip getters/setters whose type is a MAP/CAP API object (abstract ASN.1).
     * Those are rebuilt at runtime from primitive fields in the configuration beans.
     */
    private static final class SkipProtocolApiIntrospector extends JacksonAnnotationIntrospector {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean hasIgnoreMarker(AnnotatedMember m) {
            if (super.hasIgnoreMarker(m)) {
                return true;
            }
            JavaType t = m.getType();
            if (t == null) {
                return false;
            }
            return isNonPersistableProtocolType(t.getRawClass());
        }

        private static boolean isNonPersistableProtocolType(Class<?> raw) {
            if (raw == null || raw.isPrimitive() || raw == String.class || raw.isEnum()) {
                return false;
            }
            String n = raw.getName();
            if (n.startsWith("org.restcomm.protocols.ss7.map.api.")
                    || n.startsWith("org.restcomm.protocols.ss7.cap.api.")
                    || n.startsWith("org.restcomm.protocols.ss7.inap.api.")
                    || n.startsWith("org.restcomm.protocols.ss7.isup.api.")) {
                return true;
            }
            // Concrete impls under map.service.* that leaked into getters
            if ((raw.isInterface() || Modifier.isAbstract(raw.getModifiers()))
                    && (n.startsWith("org.restcomm.protocols.ss7.map.")
                            || n.startsWith("org.restcomm.protocols.ss7.cap.")
                            || n.startsWith("org.restcomm.protocols.ss7.inap.")
                            || n.startsWith("org.restcomm.protocols.ss7.isup."))) {
                return true;
            }
            // Common concrete ASN.1 carriers that still cannot round-trip via Jackson beans
            if (n.startsWith("org.restcomm.protocols.ss7.map.service.")
                    || n.startsWith("org.restcomm.protocols.ss7.map.primitives.")) {
                return true;
            }
            return false;
        }
    }

    static {
        xmlMapper = new XmlMapper();
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlMapper.configure(MapperFeature.USE_STD_BEAN_NAMING, true);
        xmlMapper.setAnnotationIntrospector(new SkipProtocolApiIntrospector());
        xmlMapper.registerModule(EnumeratedBaseJacksonModule.create());
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
            return xmlMapper.readValue(reader, ConfigurationData.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing from XML", e);
        }
    }

    public static Object fromXML(String xml) {
        try {
            return xmlMapper.readValue(xml, ConfigurationData.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing from XML", e);
        }
    }
}
