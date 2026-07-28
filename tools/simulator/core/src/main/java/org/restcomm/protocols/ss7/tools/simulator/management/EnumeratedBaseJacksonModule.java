package org.restcomm.protocols.ss7.tools.simulator.management;

import java.io.IOException;
import java.lang.reflect.Constructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.restcomm.protocols.ss7.tools.simulator.common.EnumeratedBase;

/**
 * Forces Jackson to persist {@link EnumeratedBase} as a scalar int (or legacy name
 * string), instead of bean getters like {@code getList()} that recurse.
 */
public final class EnumeratedBaseJacksonModule {
    private EnumeratedBaseJacksonModule() {
    }

    /** Mixin applied to every EnumeratedBase subtype. */
    @JsonSerialize(using = EnumeratedBaseSerializer.class)
    @JsonDeserialize(using = EnumeratedBaseDeserializer.class)
    abstract static class EnumeratedBaseMixin {
        @JsonIgnore
        public abstract EnumeratedBase[] getList();
    }

    public static SimpleModule create() {
        SimpleModule module = new SimpleModule("EnumeratedBaseModule");
        module.setMixInAnnotation(EnumeratedBase.class, EnumeratedBaseMixin.class);
        module.addSerializer(EnumeratedBase.class, new EnumeratedBaseSerializer());
        module.addDeserializer(EnumeratedBase.class, new EnumeratedBaseDeserializer());
        return module;
    }

    public static final class EnumeratedBaseSerializer extends JsonSerializer<EnumeratedBase> {
        @Override
        public void serialize(EnumeratedBase value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                // Prefer the human-readable name for readability; int also accepted on load
                gen.writeString(value.toString());
            }
        }
    }

    public static final class EnumeratedBaseDeserializer extends JsonDeserializer<EnumeratedBase>
            implements ContextualDeserializer {
        private Class<? extends EnumeratedBase> target = EnumeratedBase.class;

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
                throws JsonMappingException {
            JavaType type = ctxt.getContextualType();
            if (type == null && property != null) {
                type = property.getType();
            }
            EnumeratedBaseDeserializer d = new EnumeratedBaseDeserializer();
            if (type != null && EnumeratedBase.class.isAssignableFrom(type.getRawClass())) {
                @SuppressWarnings("unchecked")
                Class<? extends EnumeratedBase> raw = (Class<? extends EnumeratedBase>) type.getRawClass();
                d.target = raw;
            }
            return d;
        }

        @Override
        public EnumeratedBase deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (target == null || target == EnumeratedBase.class) {
                throw JsonMappingException.from(p, "Cannot deserialize abstract EnumeratedBase without concrete type");
            }
            JsonToken t = p.currentToken();
            if (t == JsonToken.VALUE_NULL) {
                return null;
            }
            if (t != null && t.isNumeric()) {
                return constructInt(p.getIntValue());
            }
            String text = p.getValueAsString();
            if (text == null) {
                return null;
            }
            text = text.trim();
            if (text.isEmpty()) {
                return null;
            }
            // Numeric string ("1") or symbolic name ("M3UA")
            try {
                return constructInt(Integer.parseInt(text));
            } catch (NumberFormatException nfe) {
                return constructString(text);
            }
        }

        private EnumeratedBase constructInt(int value) throws IOException {
            try {
                Constructor<? extends EnumeratedBase> c = target.getConstructor(int.class);
                return c.newInstance(value);
            } catch (ReflectiveOperationException e) {
                try {
                    Constructor<? extends EnumeratedBase> c = target.getConstructor(Integer.class);
                    return c.newInstance(Integer.valueOf(value));
                } catch (ReflectiveOperationException e2) {
                    // Fall back to createInstance(String) if present
                    return constructString(Integer.toString(value));
                }
            }
        }

        private EnumeratedBase constructString(String value) throws IOException {
            // Prefer static createInstance(String) — accepts names and numeric strings
            try {
                java.lang.reflect.Method m = target.getMethod("createInstance", String.class);
                Object rs = m.invoke(null, value);
                if (rs instanceof EnumeratedBase) {
                    return (EnumeratedBase) rs;
                }
            } catch (ReflectiveOperationException ignore) {
            }
            try {
                Constructor<? extends EnumeratedBase> c = target.getConstructor(String.class);
                return c.newInstance(value);
            } catch (ReflectiveOperationException e) {
                throw new IOException("Cannot construct " + target.getName() + " from '" + value + "'", e);
            }
        }
    }
}
