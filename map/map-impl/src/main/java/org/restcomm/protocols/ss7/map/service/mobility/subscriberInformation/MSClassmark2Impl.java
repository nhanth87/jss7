package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.xml.bind.DatatypeConverter;


import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MSClassmark2;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "mSClassmark2Impl")
public class MSClassmark2Impl extends OctetStringBase implements MSClassmark2 {

    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

    public MSClassmark2Impl() {
        super(3, 3, "MSClassmark2");
    }

    public MSClassmark2Impl(byte[] data) {
        super(3, 3, "MSClassmark2", data);
    }

    public byte[] getData() {
        return data;
    }

    /**
     * XML Serialization/Deserialization
     */

}
