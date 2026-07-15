package org.restcomm.protocols.ss7.map.primitives;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.xml.bind.DatatypeConverter;


import org.restcomm.protocols.ss7.map.api.primitives.DiameterIdentity;

/**
 * @author abhayani
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "diameterIdentityImpl")
public class DiameterIdentityImpl extends OctetStringBase implements DiameterIdentity {

    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

    public DiameterIdentityImpl() {
        super(9, 55, "DiameterIdentity");
    }

    public DiameterIdentityImpl(byte[] data) {
        super(9, 55, "DiameterIdentity", data);
    }

    public byte[] getData() {
        return data;
    }

    // TODO: add implementing of internal structure (?)

    /**
     * XML Serialization/Deserialization
     */

}
