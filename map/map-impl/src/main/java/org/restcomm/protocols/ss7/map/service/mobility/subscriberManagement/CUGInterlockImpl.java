
package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import jakarta.xml.bind.DatatypeConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CUGInterlock;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("cUGInterlockImpl")
public class CUGInterlockImpl extends OctetStringBase implements CUGInterlock {
    public CUGInterlockImpl() {
        super(4, 4, "CUGInterlock");
    }

    public CUGInterlockImpl(byte[] data) {
        super(4, 4, "CUGInterlock", data);
    }

    public byte[] getData() {
        return data;
    }

    /**
     * XML Serialization/Deserialization
     */
    protected static final XMLFormat<CUGInterlockImpl> CARRIER_XML = new XMLFormat<CUGInterlockImpl>(CUGInterlockImpl.class) {

        @Override
        public void read(javolution.xml.XMLFormat.InputElement xml, CUGInterlockImpl cugInterlock) throws XMLStreamException {
            String s = xml.getAttribute(DATA, DEFAULT_VALUE);
            if (s != null) {
                cugInterlock.data = DatatypeConverter.parseHexBinary(s);
            }
        }

        @Override
        public void write(CUGInterlockImpl cugInterlock, javolution.xml.XMLFormat.OutputElement xml) throws XMLStreamException {
            if (cugInterlock.data != null) {
                xml.setAttribute(DATA, DatatypeConverter.printHexBinary(cugInterlock.data));
            }
        }
    };

}
