package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.xml.bind.DatatypeConverter;


import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.PDPAddress;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "pDPAddressImpl")
public class PDPAddressImpl extends OctetStringBase implements PDPAddress {

    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

    public PDPAddressImpl() {
        super(1, 16, "PDPAddress");
    }

    public PDPAddressImpl(byte[] data) {
        super(1, 16, "PDPAddress", data);
    }

    public byte[] getData() {
        return data;
    }

    /**
     * XML Serialization/Deserialization
     */


}
