package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.xml.bind.DatatypeConverter;


import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAIdentity;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "lSAIdentityImpl")
public class LSAIdentityImpl extends OctetStringBase implements LSAIdentity {

    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

    public LSAIdentityImpl() {
        super(3, 3, "LSAIdentity");
    }

    public LSAIdentityImpl(byte[] data) {
        super(3, 3, "LSAIdentity", data);
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public boolean isPlmnSignificantLSA() {
        return ((this.data[2] & 0x01) == 0x01);
    }

    /**
     * XML Serialization/Deserialization
     */

}
