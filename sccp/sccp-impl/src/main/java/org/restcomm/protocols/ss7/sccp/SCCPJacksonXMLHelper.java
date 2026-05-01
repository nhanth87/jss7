package org.restcomm.protocols.ss7.sccp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.lang.reflect.Modifier;

import org.restcomm.protocols.ss7.indicator.GlobalTitleIndicator;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;

import java.io.IOException;

/**
 * Jackson XML helper for SCCP module XML serialization.
 * Includes custom deserializers for enums to ensure proper deserialization.
 */
public class SCCPJacksonXMLHelper {
    private static final XmlMapper XML_MAPPER;
    
    static {
        XmlFactory factory = new XmlFactory(
            new com.ctc.wstx.stax.WstxInputFactory(),
            new com.ctc.wstx.stax.WstxOutputFactory()
        );
        XML_MAPPER = new XmlMapper(factory);
        
        // Enable pretty printing for XML output
        XML_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        XML_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule module = new SimpleModule("sccpjacksonxml-module") {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addAbstractTypeResolver(new AutoImplAbstractTypeResolver());
            }
        };
        
        // Add enum deserializers
        module.addDeserializer(GlobalTitleIndicator.class, new GlobalTitleIndicatorDeserializer());
        module.addDeserializer(RoutingIndicator.class, new RoutingIndicatorDeserializer());
        
        XML_MAPPER.registerModule(module);
    }

    public static XmlMapper getXmlMapper() {
        return XML_MAPPER;
    }

    // ========== Enum Deserializers ==========

    /**
     * Custom deserializer for GlobalTitleIndicator enum.
     * Uses integer value from XML to determine the correct enum value.
     */
    public static class GlobalTitleIndicatorDeserializer extends JsonDeserializer<GlobalTitleIndicator> {
        @Override
        public GlobalTitleIndicator deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            int value = p.getValueAsInt();
            GlobalTitleIndicator result = GlobalTitleIndicator.valueOf(value);
            if (result == null) {
                throw new IOException("Unknown GlobalTitleIndicator value: " + value);
            }
            return result;
        }
    }

    /**
     * Custom deserializer for RoutingIndicator enum.
     * Uses integer value from XML to determine the correct enum value.
     */
    public static class RoutingIndicatorDeserializer extends JsonDeserializer<RoutingIndicator> {
        @Override
        public RoutingIndicator deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            int value = p.getValueAsInt();
            RoutingIndicator result = RoutingIndicator.valueOf(value);
            if (result == null) {
                throw new IOException("Unknown RoutingIndicator value: " + value);
            }
            return result;
        }
    }

    private static class AutoImplAbstractTypeResolver extends AbstractTypeResolver {
        @Override
        public JavaType findTypeMapping(DeserializationConfig config, JavaType type) {
            Class<?> raw = type.getRawClass();
            if (!raw.isInterface() && !Modifier.isAbstract(raw.getModifiers())) {
                return null;
            }
            Package pkgObj = raw.getPackage();
            if (pkgObj == null) {
                return null;
            }
            String simple = raw.getSimpleName();
            String pkg = pkgObj.getName();

            String[] candidates = new String[] {
                pkg + "." + simple + "Impl",
                pkg.replace(".api.", ".") + "." + simple + "Impl",
                pkg + ".impl." + simple + "Impl",
            };

            for (String candidate : candidates) {
                if (candidate.equals(raw.getName())) continue;
                try {
                    Class<?> impl = Class.forName(candidate);
                    if (raw.isAssignableFrom(impl)) {
                        return config.constructType(impl);
                    }
                } catch (ClassNotFoundException e) {
                    // ignore
                }
            }
            return null;
        }
    }
}