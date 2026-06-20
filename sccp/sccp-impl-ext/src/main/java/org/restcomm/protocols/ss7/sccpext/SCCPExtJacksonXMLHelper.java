package org.restcomm.protocols.ss7.sccpext;

import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.lang.reflect.Modifier;

import org.restcomm.protocols.ss7.utility.SS7XmlMapperFactory;

public class SCCPExtJacksonXMLHelper {
    private static final XmlMapper XML_MAPPER;
    static {
        XML_MAPPER = SS7XmlMapperFactory.createProtocolMapper();
        SimpleModule module = new SimpleModule("sccpextjacksonxml-module") {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addAbstractTypeResolver(new AutoImplAbstractTypeResolver());
            }
        };
        XML_MAPPER.registerModule(module);
    }

    public static XmlMapper getXmlMapper() {
        return XML_MAPPER;
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
