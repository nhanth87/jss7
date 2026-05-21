
package org.restcomm.protocols.ss7.map.service.supplementary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.MessageImpl;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.datacoding.CBSDataCodingScheme;
import org.restcomm.protocols.ss7.map.api.primitives.USSDString;
import org.restcomm.protocols.ss7.map.api.service.supplementary.MAPDialogSupplementary;
import org.restcomm.protocols.ss7.map.api.service.supplementary.SupplementaryMessage;
import org.restcomm.protocols.ss7.map.datacoding.CBSDataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive;
import org.restcomm.protocols.ss7.map.primitives.USSDStringImpl;

/**
 * @author amit bhayani
 *
 */
@JacksonXmlRootElement(localName = "supplementaryMessageImpl")
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SupplementaryMessageImpl extends MessageImpl implements SupplementaryMessage, MAPAsnPrimitive {

    private static final Logger logger = Logger.getLogger(SupplementaryMessageImpl.class);

    private static final byte DEFAULT_DATA_CODING_SCHEME = 0x0f;
    protected CBSDataCodingScheme ussdDataCodingSch;
    protected USSDString ussdString;

    /**
     *
     */
    public SupplementaryMessageImpl() {
        super();
    }

    public SupplementaryMessageImpl(CBSDataCodingScheme ussdDataCodingSch, USSDString ussdString) {
        this.ussdDataCodingSch = ussdDataCodingSch;
        this.ussdString = ussdString;
    }

    @JsonIgnore
    public MAPDialogSupplementary getMAPDialog() {
        return (MAPDialogSupplementary) super.getMAPDialog();
    }

    @JsonIgnore
    public CBSDataCodingScheme getDataCodingScheme() {
        return ussdDataCodingSch;
    }

    @JsonIgnore
    public void setDataCodingScheme(CBSDataCodingScheme ussdDataCodingSch) {
        this.ussdDataCodingSch = ussdDataCodingSch;
    }

    @JsonProperty("dataCodingScheme")
    @JacksonXmlProperty(localName = "dataCodingScheme", isAttribute = true)
    public int getDataCodingSchemeValue() {
        if (ussdDataCodingSch == null) {
            return DEFAULT_DATA_CODING_SCHEME;
        }
        return ((CBSDataCodingSchemeImpl) ussdDataCodingSch).getCode();
    }

    @JsonProperty("dataCodingScheme")
    @JacksonXmlProperty(localName = "dataCodingScheme", isAttribute = true)
    public void setDataCodingSchemeValue(int code) {
        this.ussdDataCodingSch = new CBSDataCodingSchemeImpl(code);
    }

    @JsonIgnore
    public USSDString getUSSDString() {
        return this.ussdString;
    }

    @JsonIgnore
    public void setUSSDString(USSDString ussdString) {
        this.ussdString = ussdString;
    }

    @JsonProperty("string")
    @JacksonXmlProperty(localName = "string")
    public String getUssdStringValue() {
        if (ussdString == null) {
            return null;
        }
        try {
            return ussdString.getString(null);
        } catch (MAPException e) {
            return null;
        }
    }

    @JsonProperty("string")
    @JacksonXmlProperty(localName = "string")
    public void setUssdStringValue(String value) {
        if (value != null) {
            try {
                this.ussdString = new USSDStringImpl(value, new CBSDataCodingSchemeImpl(DEFAULT_DATA_CODING_SCHEME), null);
            } catch (MAPException e) {
                // ignore
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(", ussdDataCodingSch=");
        sb.append(ussdDataCodingSch);
        if (ussdString != null) {
            sb.append(", ussdString=");
            try {
                sb.append(ussdString.getString(null));
            } catch (Exception e) {
            }
        }

        sb.append("]");

        return sb.toString();
    }

}
