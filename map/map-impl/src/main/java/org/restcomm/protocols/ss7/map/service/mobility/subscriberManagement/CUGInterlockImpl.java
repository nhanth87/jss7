package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.xml.bind.DatatypeConverter;


import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CUGInterlock;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "cUGInterlockImpl")
public class CUGInterlockImpl extends OctetStringBase implements CUGInterlock {

    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

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


}
