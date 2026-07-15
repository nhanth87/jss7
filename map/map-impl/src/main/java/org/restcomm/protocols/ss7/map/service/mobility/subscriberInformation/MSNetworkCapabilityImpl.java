package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.xml.bind.DatatypeConverter;


import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MSNetworkCapability;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "mSNetworkCapabilityImpl")
public class MSNetworkCapabilityImpl extends OctetStringBase implements MSNetworkCapability {

    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

    public MSNetworkCapabilityImpl() {
        super(1, 8, "MSNetworkCapability");
    }

    public MSNetworkCapabilityImpl(byte[] data) {
        super(1, 8, "MSNetworkCapability", data);
    }

    public byte[] getData() {
        return data;
    }

    /**
     * XML Serialization/Deserialization
     */

}
