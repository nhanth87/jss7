
package org.restcomm.protocols.ss7.map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPMessage;

/**
 *
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
@XStreamAlias("mapMessage")
public abstract class MessageImpl implements MAPMessage {

    @XStreamAsAttribute
    private long invokeId;
    private MAPDialog mapDialog;
    @XStreamAsAttribute
    private boolean returnResultNotLast = false;

    public long getInvokeId() {
        return this.invokeId;
    }

    public MAPDialog getMAPDialog() {
        return this.mapDialog;
    }

    public void setInvokeId(long invokeId) {
        this.invokeId = invokeId;
    }

    public void setMAPDialog(MAPDialog mapDialog) {
        this.mapDialog = mapDialog;
    }

    public boolean isReturnResultNotLast() {
        return returnResultNotLast;
    }

    public void setReturnResultNotLast(boolean returnResultNotLast) {
        this.returnResultNotLast = returnResultNotLast;
    }

}
