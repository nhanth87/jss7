package org.restcomm.protocols.ss7.map.service.mobility.authentication;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.xml.bind.DatatypeConverter;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.UEUsageType;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
@JacksonXmlRootElement(localName = "uEUsageTypeImpl")
public class UEUsageTypeImpl extends OctetStringBase implements UEUsageType {

    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

    public UEUsageTypeImpl() {
        super(4, 4, "UEUsageType");
    }

    public UEUsageTypeImpl(byte[] data) {
        super(4, 4, "UEUsageType", data);
    }

    public byte[] getData() {
        return data;
    }

    /**
     * XML Serialization/Deserialization
     */

}
