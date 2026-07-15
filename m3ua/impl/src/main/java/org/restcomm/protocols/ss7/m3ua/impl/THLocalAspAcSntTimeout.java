package org.restcomm.protocols.ss7.m3ua.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.m3ua.impl.fsm.FSM;
import org.restcomm.protocols.ss7.m3ua.impl.fsm.FSMState;
import org.restcomm.protocols.ss7.m3ua.impl.fsm.TransitionHandler;

/**
 * The purpose of this class is to resend M3UA ASPAC if T(Ack) times out (2000 ms)
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class THLocalAspAcSntTimeout implements TransitionHandler {

    private AspImpl aspImpl;
    private FSM fsm;
    private static final Logger logger = LogManager.getLogger(THLocalAspAcSntTimeout.class);

    public THLocalAspAcSntTimeout(AspImpl aspImpl, FSM fsm) {
        this.aspImpl = aspImpl;
        this.fsm = fsm;
    }

    public boolean process(FSMState state) {
        this.aspImpl.getAspFactory().sendAspActive(this.aspImpl.asImpl);
        return true;
    }
}
