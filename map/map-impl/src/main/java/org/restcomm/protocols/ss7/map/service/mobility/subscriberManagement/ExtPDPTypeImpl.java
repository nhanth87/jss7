
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import jakarta.xml.bind.DatatypeConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtPDPType;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("extPDPTypeImpl")
public class ExtPDPTypeImpl extends OctetStringBase implements ExtPDPType {
    public ExtPDPTypeImpl() {
        super(2, 2, "ExtPDPType");
    }

    public ExtPDPTypeImpl(byte[] data) {
        super(2, 2, "ExtPDPType", data);
    }

    public byte[] getData() {
        return data;
    }

    /**
     * XML Serialization/Deserialization
     */
    protected static final XMLFormat<ExtPDPTypeImpl> EXT_PDP_TYPE_XML = new XMLFormat<ExtPDPTypeImpl>(ExtPDPTypeImpl.class) {

        @Override
        public void read(javolution.xml.XMLFormat.InputElement xml, ExtPDPTypeImpl extPdpType) throws XMLStreamException {
            String s = xml.getAttribute(DATA, DEFAULT_VALUE);
            if (s != null) {
                extPdpType.data = DatatypeConverter.parseHexBinary(s);
            }
        }

        @Override
        public void write(ExtPDPTypeImpl extPdpType, javolution.xml.XMLFormat.OutputElement xml) throws XMLStreamException {
            if (extPdpType.data != null) {
                xml.setAttribute(DATA, DatatypeConverter.printHexBinary(extPdpType.data));
            }
        }
    };
}
