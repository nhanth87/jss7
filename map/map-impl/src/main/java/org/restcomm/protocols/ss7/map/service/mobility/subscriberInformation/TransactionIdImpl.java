package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.xml.bind.DatatypeConverter;


import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TransactionId;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "transactionIdImpl")
public class TransactionIdImpl extends OctetStringBase implements TransactionId {

    private static final String DATA = "data";

    private static final String DEFAULT_VALUE = null;

    public TransactionIdImpl() {
        super(1, 2, "TransactionId");
    }

    public TransactionIdImpl(byte[] data) {
        super(1, 2, "TransactionId", data);
    }

    public byte[] getData() {
        return data;
    }

    /**
     * XML Serialization/Deserialization
     */

}
