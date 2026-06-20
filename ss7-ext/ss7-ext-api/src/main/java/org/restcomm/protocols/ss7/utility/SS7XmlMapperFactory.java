package org.restcomm.protocols.ss7.utility;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

/**
 * Central factory for Jackson XML mappers used across jSS7 modules.
 * <p>
 * {@link #createConfigMapper()} is for cold-path config persist/load (management.xml).
 * {@link #createProtocolMapper()} is for MAP/CAP/SCCP protocol primitive serialization.
 */
public final class SS7XmlMapperFactory {

    private SS7XmlMapperFactory() {
    }

    public static XmlFactory createWstxXmlFactory() {
        return new XmlFactory(new WstxInputFactory(), new WstxOutputFactory());
    }

    /**
     * Config/persist mapper: no indent, tolerate unknown properties, emit null fields.
     */
    public static XmlMapper createConfigMapper() {
        XmlMapper mapper = new XmlMapper(createWstxXmlFactory());
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDefaultPrettyPrinter(null);
        return mapper;
    }

    /**
     * Protocol primitive mapper: no indent, tolerate unknown properties, skip null fields.
     */
    public static XmlMapper createProtocolMapper() {
        XmlMapper mapper = new XmlMapper(createWstxXmlFactory());
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setDefaultPrettyPrinter(null);
        return mapper;
    }

    /**
     * SCCP stack timer/config persist ({@code SccpConfig} in sccpresource persist file).
     * Uses XML 1.1 declaration to match legacy production files.
     */
    public static XmlMapper createSccpStackConfigMapper() {
        XmlMapper mapper = createConfigMapper();
        mapper.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }

    /**
     * M3UA management persist mapper: indented XML with declaration.
     */
    public static XmlMapper createM3uaConfigMapper() {
        XmlMapper mapper = createConfigMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }
}
