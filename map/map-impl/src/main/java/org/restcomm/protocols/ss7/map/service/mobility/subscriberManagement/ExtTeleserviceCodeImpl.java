package org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtTeleserviceCode;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.TeleserviceCodeValue;
import org.restcomm.protocols.ss7.map.primitives.OctetStringBase;

/**
 *
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "extTeleserviceCodeImpl")
public class ExtTeleserviceCodeImpl extends OctetStringBase implements ExtTeleserviceCode {

    private static final String TELE_SERVICE_CODE_VALUE = "teleserviceCodeValue";
    private static final String DEFAULT_STRING_VALUE = null;

    public ExtTeleserviceCodeImpl() {
        super(1, 5, "ExtTeleserviceCode");
    }

    public ExtTeleserviceCodeImpl(byte[] data) {
        super(1, 5, "ExtTeleserviceCode", data);
    }

    public ExtTeleserviceCodeImpl(TeleserviceCodeValue value) {
        super(1, 5, "ExtTeleserviceCode");
        setTeleserviceCode(value);
    }

    public void setTeleserviceCode(TeleserviceCodeValue value) {
        if (value != null)
            this.data = new byte[] { (byte) (value.getCode()) };
    }

    public byte[] getData() {
        return data;
    }

    public TeleserviceCodeValue getTeleserviceCodeValue() {
        if (data == null || data.length < 1)
            return null;
        else
            return TeleserviceCodeValue.getInstance(this.data[0]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this._PrimitiveName);
        sb.append(" [");

        sb.append("Value=");
        sb.append(this.getTeleserviceCodeValue());

        sb.append(", Data=[");
        if (data != null) {
            for (int i1 : data) {
                sb.append(i1);
                sb.append(", ");
            }
        }
        sb.append("]");

        sb.append("]");

        return sb.toString();
    }

    /**
     * XML Serialization/Deserialization
     */

}
