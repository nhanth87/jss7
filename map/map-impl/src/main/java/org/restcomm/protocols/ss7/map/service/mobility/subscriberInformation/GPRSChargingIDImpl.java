package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.xml.bind.DatatypeConverter;


import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GPRSChargingID;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "gPRSChargingIDImpl")
public class GPRSChargingIDImpl extends OctetStringBase implements GPRSChargingID {

    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

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

}
