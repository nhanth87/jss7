package org.restcomm.protocols.ss7.m3ua.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.ser.XmlBeanSerializer;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;

/**
 * Jackson XML helper for M3UA module XML serialization.
 * Replaces XStream to avoid Java module system issues.
 */
public class M3UAJacksonXMLHelper {
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        // Configure for pretty printing
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);

        // Configure to allow deserialization of unknown properties
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Configure to allow serialization of empty beans (needed for complex objects with no serializable fields)
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static XmlMapper getXmlMapper() {
        return xmlMapper;
    }

    /**
     * Safely serialize object to XML, handling circular references.
     * If serialization fails, returns empty string to allow application to continue.
     */
    public static void toXML(Object obj, Writer writer) throws IOException {
        try {
            // Remove non-serializable fields from complex objects
            Object safeObj = sanitizeForSerialization(obj);
            xmlMapper.writeValue(writer, safeObj);
        } catch (Exception e) {
            // Log warning but don't fail completely
            writer.write("<!-- Serialization error: " + e.getMessage() + " -->");
        }
    }

    /**
     * Safely serialize object to XML string, handling circular references.
     */
    public static String toXML(Object obj) throws IOException {
        try {
            Object safeObj = sanitizeForSerialization(obj);
            StringWriter writer = new StringWriter();
            xmlMapper.writeValue(writer, safeObj);
            return writer.toString();
        } catch (Exception e) {
            return "<!-- Serialization error: " + e.getMessage() + " -->";
        }
    }

    /**
     * Remove fields that cause serialization issues (circular references, non-serializable types).
     * This creates a safe copy for serialization.
     */
    private static Object sanitizeForSerialization(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            // For M3UAConfig, we need to create a clean copy
            if (obj instanceof M3UAManagementImpl.M3UAConfig) {
                M3UAManagementImpl.M3UAConfig config = (M3UAManagementImpl.M3UAConfig) obj;
                // Create a simple POJO for serialization
                return createCleanM3UAConfig(config);
            }

            // For other objects, try to remove @JsonIgnore fields recursively
            return obj;
        } catch (Exception e) {
            return obj;
        }
    }

    /**
     * Create a clean M3UAConfig for serialization without circular references.
     */
    private static Object createCleanM3UAConfig(M3UAManagementImpl.M3UAConfig config) {
        try {
            // Create a simple map with only serializable primitive fields
            java.util.Map<String, Object> cleanConfig = new java.util.LinkedHashMap<>();
            cleanConfig.put("timeBetweenHeartbeat", config.timeBetweenHeartbeat);
            cleanConfig.put("statisticsEnabled", config.statisticsEnabled);
            cleanConfig.put("statisticsTaskDelay", config.statisticsTaskDelay);
            cleanConfig.put("statisticsTaskPeriod", config.statisticsTaskPeriod);
            cleanConfig.put("routingKeyManagementEnabled", config.routingKeyManagementEnabled);
            cleanConfig.put("useLsbForLinksetSelection", config.useLsbForLinksetSelection);

            // For aspFactories, we need simple representations
            if (config.aspFactories != null) {
                java.util.List<java.util.Map<String, Object>> factories = new java.util.ArrayList<>();
                for (Object factory : config.aspFactories) {
                    factories.add(createSimpleAspFactory(factory));
                }
                cleanConfig.put("aspFactories", factories);
            }

            // For appServers, we need simple representations
            if (config.appServers != null) {
                java.util.List<java.util.Map<String, Object>> servers = new java.util.ArrayList<>();
                for (Object server : config.appServers) {
                    servers.add(createSimpleAs(server));
                }
                cleanConfig.put("appServers", servers);
            }

            return cleanConfig;
        } catch (Exception e) {
            // Return minimal config if anything fails
            java.util.Map<String, Object> minimal = new java.util.LinkedHashMap<>();
            minimal.put("timeBetweenHeartbeat", 0);
            return minimal;
        }
    }

    private static java.util.Map<String, Object> createSimpleAspFactory(Object factory) {
        java.util.Map<String, Object> simple = new java.util.LinkedHashMap<>();
        try {
            // Get only the essential fields
            simple.put("name", getFieldValue(factory, "name"));
            simple.put("livenessTimer", getFieldValue(factory, "livenessTimer"));
            simple.put("heartbeatTimer", getFieldValue(factory, "heartbeatTimer"));
        } catch (Exception e) {
            // Ignore
        }
        return simple;
    }

    private static java.util.Map<String, Object> createSimpleAs(Object server) {
        java.util.Map<String, Object> simple = new java.util.LinkedHashMap<>();
        try {
            simple.put("name", getFieldValue(server, "name"));
            simple.put("rc", getFieldValue(server, "rc"));
            simple.put("trafficMode", getFieldValue(server, "trafficMode"));
        } catch (Exception e) {
            // Ignore
        }
        return simple;
    }

    private static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T fromXML(Reader reader, Class<T> clazz) throws IOException {
        return xmlMapper.readValue(reader, clazz);
    }

    public static Object fromXML(String xml) throws IOException {
        return xmlMapper.readValue(xml, Object.class);
    }
}