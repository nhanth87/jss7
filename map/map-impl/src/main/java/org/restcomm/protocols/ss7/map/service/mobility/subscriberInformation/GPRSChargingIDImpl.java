
package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import jakarta.xml.bind.DatatypeConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GPRSChargingID;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("gPRSChargingIDImpl")
public class GPRSChargingIDImpl extends OctetStringBase implements GPRSChargingID {
    public GPRSChargingIDImpl() {
        super(4, 4, "GPRSChargingID");
    }

    public GPRSChargingIDImpl(byte[] data) {
        super(4, 4, "GPRSChargingID", data);
    }

    public byte[] getData() {
        return data;
    }

    /**
     * XML Serialization/Deserialization
     */
    protected static final XMLFormat<GPRSChargingIDImpl> GPRS_CHARGING_ID_XML = new XMLFormat<GPRSChargingIDImpl>(GPRSChargingIDImpl.class) {

        @Override
        public void read(javolution.xml.XMLFormat.InputElement xml, GPRSChargingIDImpl charingId) throws XMLStreamException {
            String s = xml.getAttribute(DATA, DEFAULT_VALUE);
            if (s != null) {
                charingId.data = DatatypeConverter.parseHexBinary(s);
            }
        }

        @Override
        public void write(GPRSChargingIDImpl charingId, javolution.xml.XMLFormat.OutputElement xml) throws XMLStreamException {
            if (charingId.data != null) {
                xml.setAttribute(DATA, DatatypeConverter.printHexBinary(charingId.data));
            }
        }
    };
}
