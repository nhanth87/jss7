
package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import jakarta.xml.bind.DatatypeConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TEID;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("tEIDImpl")
public class TEIDImpl extends OctetStringBase implements TEID {
    public TEIDImpl() {
        super(4, 4, "TEID");
    }

    public TEIDImpl(byte[] data) {
        super(4, 4, "TEID", data);
    }

    public byte[] getData() {
        return data;
    }

    /**
     * XML Serialization/Deserialization
     */
    protected static final XMLFormat<TEIDImpl> TEID_XML = new XMLFormat<TEIDImpl>(TEIDImpl.class) {

        @Override
        public void read(javolution.xml.XMLFormat.InputElement xml, TEIDImpl teid) throws XMLStreamException {
            String s = xml.getAttribute(DATA, DEFAULT_VALUE);
            if (s != null) {
                teid.data = DatatypeConverter.parseHexBinary(s);
            }
        }

        @Override
        public void write(TEIDImpl teid, javolution.xml.XMLFormat.OutputElement xml) throws XMLStreamException {
            if (teid.data != null) {
                xml.setAttribute(DATA, DatatypeConverter.printHexBinary(teid.data));
            }
        }
    };
}
