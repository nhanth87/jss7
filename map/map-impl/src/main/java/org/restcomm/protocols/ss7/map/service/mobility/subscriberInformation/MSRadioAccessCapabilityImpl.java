
package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import jakarta.xml.bind.DatatypeConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MSRadioAccessCapability;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("mSRadioAccessCapabilityImpl")
public class MSRadioAccessCapabilityImpl extends OctetStringBase implements MSRadioAccessCapability {
    public MSRadioAccessCapabilityImpl() {
        super(1, 50, "MSRadioAccessCapability");
    }

    public MSRadioAccessCapabilityImpl(byte[] data) {
        super(1, 50, "MSRadioAccessCapability", data);
    }

    public byte[] getData() {
        return data;
    }

    /**
     * XML Serialization/Deserialization
     */
    protected static final XMLFormat<MSRadioAccessCapabilityImpl> MS_RADIO_ACCESS_CAPABILITY_XML = new XMLFormat<MSRadioAccessCapabilityImpl>(MSRadioAccessCapabilityImpl.class) {

        @Override
        public void read(javolution.xml.XMLFormat.InputElement xml, MSRadioAccessCapabilityImpl mSRadioAccessCapability) throws XMLStreamException {
            String s = xml.getAttribute(DATA, DEFAULT_VALUE);
            if (s != null) {
                mSRadioAccessCapability.data = DatatypeConverter.parseHexBinary(s);
            }
        }

        @Override
        public void write(MSRadioAccessCapabilityImpl mSRadioAccessCapability, javolution.xml.XMLFormat.OutputElement xml) throws XMLStreamException {
            if (mSRadioAccessCapability.data != null) {
                xml.setAttribute(DATA, DatatypeConverter.printHexBinary(mSRadioAccessCapability.data));
            }
        }
    };
}
