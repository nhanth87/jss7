package org.restcomm.protocols.ss7.map;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPMessage;

/**
 *
 * @author amit bhayani
 * @author sergey vetyutnev
 *
 */
@JacksonXmlRootElement(localName = "messageImpl")
public abstract class MessageImpl implements MAPMessage {

    private static final String INVOKE_ID = "invokeId";
    private static final String RETURN_RESULT_NOT_LAST = "returnResultNotLast";

    private long invokeId;
    private MAPDialog mapDialog;
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

    /**
     * XML Serialization/Deserialization
     */


}
