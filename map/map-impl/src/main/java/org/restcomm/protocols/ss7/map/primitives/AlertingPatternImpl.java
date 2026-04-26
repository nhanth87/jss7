
package org.restcomm.protocols.ss7.map.primitives;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.restcomm.protocols.ss7.map.api.primitives.AlertingCategory;
import org.restcomm.protocols.ss7.map.api.primitives.AlertingLevel;
import org.restcomm.protocols.ss7.map.api.primitives.AlertingPattern;

/**
 *
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "alertingPattern")
@JsonTypeName("AlertingPattern")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertingPatternImpl extends OctetStringLength1Base implements AlertingPattern {

    public AlertingPatternImpl() {
        super("AlertingPattern");
    }

    public AlertingPatternImpl(int data) {
        super("AlertingPattern", data);
    }

    public AlertingPatternImpl(AlertingLevel alertingLevel) {
        super("AlertingPattern", alertingLevel.getLevel());
    }

    public AlertingPatternImpl(AlertingCategory alertingCategory) {
        super("AlertingPattern", alertingCategory.getCategory());
    }

    @JsonProperty("value")
    @JacksonXmlProperty(isAttribute = true, localName = "value")
    public int getData() {
        return data;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.AlertingPattern#getAlertingLevel()
     */
    public AlertingLevel getAlertingLevel() {
        return AlertingLevel.getInstance(this.data);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.restcomm.protocols.ss7.map.api.primitives.AlertingPattern#getAlertingCategory()
     */
    public AlertingCategory getAlertingCategory() {
        return AlertingCategory.getInstance(this.data);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(_PrimitiveName);
        sb.append(" [");

        AlertingLevel al = this.getAlertingLevel();
        if (al != null) {
            sb.append("AlertingLevel=");
            sb.append(al);
        }
        AlertingCategory ac = this.getAlertingCategory();
        if (ac != null) {
            sb.append(" AlertingCategory=");
            sb.append(ac);
        }
        sb.append("]");

        return sb.toString();
    }

}
