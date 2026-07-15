
package org.restcomm.protocols.ss7.sccpext.impl.congestion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restcomm.protocols.ss7.sccpext.impl.RemoteSignalingPointCodeExtImpl;

/**
*
* @author sergey vetyutnev
*
*/
public class CongStateTimerA implements Runnable {
    private RemoteSignalingPointCodeExtImpl remoteSignalingPointCodeExtImpl;
    private Logger logger = LogManager.getLogger(CongStateTimerA.class.getCanonicalName());

    public CongStateTimerA(RemoteSignalingPointCodeExtImpl remoteSignalingPointCodeExtImpl) {
        if (logger.isDebugEnabled()) {
            logger.debug("SCCP cong TimerA has started: " + remoteSignalingPointCodeExtImpl);
        }
        this.remoteSignalingPointCodeExtImpl = remoteSignalingPointCodeExtImpl;
    }

    @Override
    public void run() {
        if (logger.isDebugEnabled()) {
            logger.debug("SCCP cong TimerA is over: " + remoteSignalingPointCodeExtImpl);
        }
        remoteSignalingPointCodeExtImpl.clearTimerA();
    }
}
