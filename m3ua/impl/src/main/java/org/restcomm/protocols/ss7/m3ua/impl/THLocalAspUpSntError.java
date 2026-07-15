package org.restcomm.protocols.ss7.m3ua.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.m3ua.impl.fsm.FSM;
import org.restcomm.protocols.ss7.m3ua.impl.fsm.FSMState;
import org.restcomm.protocols.ss7.m3ua.impl.fsm.TransitionHandler;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class THLocalAspUpSntError implements TransitionHandler {

    private AspImpl aspImpl;
    private FSM fsm;
    private static final Logger logger = LogManager.getLogger(THLocalAspUpSntError.class);

    public THLocalAspUpSntError(AspImpl aspImpl, FSM fsm) {
        this.aspImpl = aspImpl;
        this.fsm = fsm;
    }

    public boolean process(FSMState state) {
        this.aspImpl.getAspFactory().sendAspUp();
        return true;
    }
}
