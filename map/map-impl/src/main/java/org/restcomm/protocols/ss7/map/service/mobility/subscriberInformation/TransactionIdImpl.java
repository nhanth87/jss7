
package org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation;

import jakarta.xml.bind.DatatypeConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TransactionId;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("transactionIdImpl")
public class TransactionIdImpl extends OctetStringBase implements TransactionId {
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
    protected static final XMLFormat<TransactionIdImpl> TRANSACTION_ID_XML = new XMLFormat<TransactionIdImpl>(TransactionIdImpl.class) {

        @Override
        public void read(javolution.xml.XMLFormat.InputElement xml, TransactionIdImpl transactionId) throws XMLStreamException {
            String s = xml.getAttribute(DATA, DEFAULT_VALUE);
            if (s != null) {
                transactionId.data = DatatypeConverter.parseHexBinary(s);
            }
        }

        @Override
        public void write(TransactionIdImpl transactionId, javolution.xml.XMLFormat.OutputElement xml) throws XMLStreamException {
            if (transactionId.data != null) {
                xml.setAttribute(DATA, DatatypeConverter.printHexBinary(transactionId.data));
            }
        }
    };
}
