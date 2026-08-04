package javolution.xml.stream;

/** Compatibility exception type used by legacy tests. */
public class XMLStreamException extends Exception {
    public XMLStreamException() {
    }

    public XMLStreamException(String message) {
        super(message);
    }

    public XMLStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public XMLStreamException(Throwable cause) {
        super(cause);
    }
}
