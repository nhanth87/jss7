package org.restcomm.protocols.ss7.tools.simulator.management;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Legacy Javolution / XStream simulator XML stores simple strings as
 * {@code <localHost value="127.0.0.1"/>} (attribute form). Modern Jackson writes
 * {@code <localHost>127.0.0.1</localHost>}. Accept both on load.
 */
public final class XmlValueAttributeStringModule {
    private XmlValueAttributeStringModule() {
    }

    public static SimpleModule create() {
        SimpleModule module = new SimpleModule("XmlValueAttributeStringModule");
        module.addDeserializer(String.class, new XmlValueAttributeStringDeserializer());
        return module;
    }

    public static final class XmlValueAttributeStringDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken t = p.currentToken();
            if (t == JsonToken.VALUE_NULL) {
                return null;
            }
            if (t == JsonToken.START_OBJECT) {
                String chosen = null;
                while (p.nextToken() != JsonToken.END_OBJECT) {
                    if (p.currentToken() != JsonToken.FIELD_NAME) {
                        continue;
                    }
                    String field = p.currentName();
                    p.nextToken();
                    String v = p.getValueAsString();
                    if (v == null) {
                        p.skipChildren();
                        continue;
                    }
                    if ("value".equals(field) || chosen == null) {
                        chosen = v;
                    }
                }
                return chosen;
            }
            return p.getValueAsString();
        }
    }
}
