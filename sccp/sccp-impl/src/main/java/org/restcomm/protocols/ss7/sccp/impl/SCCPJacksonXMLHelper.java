package org.restcomm.protocols.ss7.sccp.impl;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.regex.Pattern;
import org.restcomm.protocols.ss7.sccp.impl.parameter.AbstractGlobalTitle;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDOddEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.DefaultEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0001Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0010Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0011Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.GlobalTitle0100Impl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.NoGlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;

/**
 * Jackson XML helper for SCCP module XML serialization.
 * Replaces XStream for better performance and security.
 *
 * <p><b>Integer map keys:</b> default Jackson XML emits {@code <1>} for key {@code 1},
 * which Woodstox rejects (XML names cannot start with a digit). We persist keys as
 * {@code <k1>} and sanitize legacy {@code <N>} tags on read.
 *
 * <p><b>Polymorphic GT / EncodingScheme:</b> field types are interfaces; mix-ins apply
 * the same EXISTING_PROPERTY discriminators as the concrete impl annotations so persist
 * XML without {@code @class} still loads.
 */
public class SCCPJacksonXMLHelper {
    private static final XmlMapper xmlMapper = new XmlMapper();

    /** Illegal legacy map-key element names: {@code <12>} / {@code </12>}. */
    private static final Pattern NUMERIC_ELEMENT_NAME = Pattern.compile("<(/?)(\\d+)>");

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "globalTitleIndicator",
            visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(
                value = GlobalTitle0100Impl.class,
                name = "GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_ENCODING_SCHEME_AND_NATURE_OF_ADDRESS"),
        @JsonSubTypes.Type(
                value = GlobalTitle0011Impl.class,
                name = "GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_NUMBERING_PLAN_AND_ENCODING_SCHEME"),
        @JsonSubTypes.Type(value = GlobalTitle0010Impl.class, name = "GLOBAL_TITLE_INCLUDES_TRANSLATION_TYPE_ONLY"),
        @JsonSubTypes.Type(
                value = GlobalTitle0001Impl.class,
                name = "GLOBAL_TITLE_INCLUDES_NATURE_OF_ADDRESS_INDICATOR_ONLY"),
        @JsonSubTypes.Type(value = NoGlobalTitle.class, name = "NO_GLOBAL_TITLE_INCLUDED")
    })
    private abstract static class GlobalTitleMixin {}

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "type",
            visible = true)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = BCDOddEncodingScheme.class, name = "BCD_ODD"),
        @JsonSubTypes.Type(value = BCDEvenEncodingScheme.class, name = "BCD_EVEN"),
        @JsonSubTypes.Type(value = DefaultEncodingScheme.class, name = "UNKNOWN")
    })
    private abstract static class EncodingSchemeMixin {}

    static {
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        xmlMapper.addMixIn(GlobalTitle.class, GlobalTitleMixin.class);
        xmlMapper.addMixIn(AbstractGlobalTitle.class, GlobalTitleMixin.class);
        xmlMapper.addMixIn(EncodingScheme.class, EncodingSchemeMixin.class);

        SimpleModule intKeyModule = new SimpleModule("SccpIntegerMapKeyXml");
        intKeyModule.addKeySerializer(Integer.class, new JsonSerializer<Integer>() {
            @Override
            public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeFieldName("k" + value);
            }
        });
        intKeyModule.addKeyDeserializer(Integer.class, new KeyDeserializer() {
            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt) {
                if (key == null || key.isEmpty()) {
                    throw new IllegalArgumentException("Empty Integer map key");
                }
                if (key.charAt(0) == 'k' && key.length() > 1) {
                    return Integer.valueOf(key.substring(1));
                }
                return Integer.valueOf(key);
            }
        });
        xmlMapper.registerModule(intKeyModule);
    }

    public static XmlMapper getXmlMapper() {
        return xmlMapper;
    }

    /**
     * Rewrite legacy illegal numeric element names ({@code <1>}) to valid {@code <k1>}
     * so Woodstox/Jackson can parse Integer-keyed SCCP maps.
     */
    public static String sanitizeNumericElementNames(String xml) {
        if (xml == null || xml.isEmpty()) {
            return xml;
        }
        return NUMERIC_ELEMENT_NAME.matcher(xml).replaceAll("<$1k$2>");
    }

    public static void toXML(Object obj, java.io.Writer writer) throws IOException {
        xmlMapper.writeValue(writer, obj);
    }

    public static String toXML(Object obj) throws IOException {
        return xmlMapper.writeValueAsString(obj);
    }

    public static <T> T fromXML(Reader reader, Class<T> valueType) throws IOException {
        StringWriter sw = new StringWriter();
        reader.transferTo(sw);
        return fromXML(sw.toString(), valueType);
    }

    public static <T> T fromXML(String xml, Class<T> valueType) throws IOException {
        return xmlMapper.readValue(sanitizeNumericElementNames(xml), valueType);
    }

    @Deprecated
    public static Object fromXML(Reader reader) throws IOException {
        throw new UnsupportedOperationException("Use fromXML(Reader, Class<T>) instead for type safety");
    }

    @Deprecated
    public static Object fromXML(String xml) throws IOException {
        throw new UnsupportedOperationException("Use fromXML(String, Class<T>) instead for type safety");
    }
}
