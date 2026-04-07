
package org.restcomm.protocols.ss7.cap;

import org.restcomm.protocols.ss7.cap.api.CAPDialog;
import org.restcomm.protocols.ss7.cap.api.CAPMessage;

/**
 *
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("message")
public abstract class MessageImpl implements CAPMessage {

    @XStreamAsAttribute
    private long invokeId;
    private CAPDialog capDialog;

    public long getInvokeId() {
        return this.invokeId;
    }

    public CAPDialog getCAPDialog() {
        return this.capDialog;
    }

    public void setInvokeId(long invokeId) {
        this.invokeId = invokeId;
    }

    public void setCAPDialog(CAPDialog capDialog) {
        this.capDialog = capDialog;
    }

    protected void addInvokeIdInfo(StringBuilder sb) {
        sb.append("InvokeId=");
        sb.append(this.invokeId);
    }

}
