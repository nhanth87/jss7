
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall;


import org.restcomm.protocols.ss7.cap.MessageImpl;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CircuitSwitchedCallMessage;
import org.restcomm.protocols.ss7.cap.primitives.CAPAsnPrimitive;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author sergey vetyutnev
 * @author Amit Bhayani
 *
 */
@XStreamAlias("circuitSwitchedCallMessage")
 extends MessageImpl implements CircuitSwitchedCallMessage, CAPAsnPrimitive {

    public CAPDialogCircuitSwitchedCall getCAPDialog() {
        return (CAPDialogCircuitSwitchedCall) super.getCAPDialog();
    }
}
