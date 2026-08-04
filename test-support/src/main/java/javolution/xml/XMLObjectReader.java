package javolution.xml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Jackson-backed stand-in for the removed javolution {@code XMLObjectReader}.
 */
public final class XMLObjectReader implements AutoCloseable {

    private static final XmlMapper MAPPER = createMapper();

    private final Reader reader;
    private boolean closed;

    private XMLObjectReader(Reader reader) {
        this.reader = reader;
    }

    public static XMLObjectReader newInstance(InputStream in) {
        return new XMLObjectReader(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public static XMLObjectReader newInstance(Reader reader) {
        return new XMLObjectReader(reader);
    }

    public void setBinding(XMLBinding binding) {
        // no-op
    }

    public <T> T read(String rootName, Class<T> type) throws IOException {
        if (closed) {
            throw new IOException("XMLObjectReader closed");
        }
        return MAPPER.readerFor(type).withRootName(rootName).readValue(reader);
    }

    @Override
    public void close() throws IOException {
        closed = true;
        reader.close();
    }

    private static XmlMapper createMapper() {
        XmlMapper mapper = new XmlMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }
}
