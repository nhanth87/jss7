package org.restcomm.protocols.ss7.utility;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * Unit tests for {@link SS7XmlMapperFactory} baseline configuration.
 * MAP primitive javolution golden fixtures are in map-impl (avoids Maven reactor cycle).
 */
public class JavolutionXmlCompatibilityTest {

    @Test
    public void createConfigMapperHasExpectedDefaults() {
        XmlMapper mapper = SS7XmlMapperFactory.createConfigMapper();

        assertNotNull(mapper);
        assertFalse(mapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
        assertFalse(mapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertNotEquals(mapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion(),
                JsonInclude.Include.NON_NULL);
    }

    @Test
    public void createProtocolMapperHasExpectedDefaults() {
        XmlMapper mapper = SS7XmlMapperFactory.createProtocolMapper();

        assertNotNull(mapper);
        assertFalse(mapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
        assertFalse(mapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertEquals(mapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion(),
                JsonInclude.Include.NON_NULL);
    }

    @Test
    public void createWstxXmlFactoryIsNotNull() {
        assertNotNull(SS7XmlMapperFactory.createWstxXmlFactory());
    }

    @Test
    public void createSccpStackConfigMapperUsesXml11() throws Exception {
        XmlMapper mapper = SS7XmlMapperFactory.createSccpStackConfigMapper();
        assertNotNull(mapper);
        String xml = mapper.writeValueAsString(new Object() {
            @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(isAttribute = true)
            public int value = 1;
        });
        assertTrue(xml.contains("1.1"), "SCCP stack config should emit XML 1.1 declaration");
    }
}
