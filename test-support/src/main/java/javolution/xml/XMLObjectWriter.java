package javolution.xml;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Jackson-backed stand-in for the removed javolution {@code XMLObjectWriter}.
 * Prefer migrating callers to module-specific Jackson helpers.
 */
public final class XMLObjectWriter implements AutoCloseable {

    private static final XmlMapper MAPPER = createMapper();

    private final Writer writer;
    private boolean closed;

    private XMLObjectWriter(Writer writer) {
        this.writer = writer;
    }

    public static XMLObjectWriter newInstance(OutputStream out) {
        return new XMLObjectWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }

    public static XMLObjectWriter newInstance(Writer writer) {
        return new XMLObjectWriter(writer);
    }

    public void setIndentation(String indentation) {
        // XmlMapper is preconfigured with indentation; ignore per-call tabs.
    }

    public void setBinding(XMLBinding binding) {
        // no-op — Jackson uses annotations / defaults
    }

    public <T> void write(T value, String rootName, Class<T> type) throws IOException {
        if (closed) {
            throw new IOException("XMLObjectWriter closed");
        }
        MAPPER.writer().withRootName(rootName).writeValue(writer, value);
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        closed = true;
        writer.flush();
    }

    private static XmlMapper createMapper() {
        XmlMapper mapper = new XmlMapper();
        mapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, false);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return mapper;
    }
}
