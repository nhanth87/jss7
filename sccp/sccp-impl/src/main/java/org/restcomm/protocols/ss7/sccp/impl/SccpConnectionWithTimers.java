package org.restcomm.protocols.ss7.sccp.impl;

import org.restcomm.protocols.ss7.scheduler.api.TimerHandle;
import org.restcomm.protocols.ss7.scheduler.api.TimerScheduler;
import org.restcomm.protocols.ss7.scheduler.api.TimerType;
import org.restcomm.protocols.ss7.sccp.SccpConnection;
import org.restcomm.protocols.ss7.sccp.SccpConnectionState;
import org.restcomm.protocols.ss7.sccp.SccpListener;
import org.restcomm.protocols.ss7.sccp.impl.message.SccpConnCcMessageImpl;
import org.restcomm.protocols.ss7.sccp.impl.message.SccpConnItMessageImpl;
import org.restcomm.protocols.ss7.sccp.impl.message.SccpConnRscMessageImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.CreditImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ReleaseCauseImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SequenceNumberImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SequencingSegmentingImpl;
import org.restcomm.protocols.ss7.sccp.message.SccpConnMessage;
import org.restcomm.protocols.ss7.sccp.parameter.LocalReference;
import org.restcomm.protocols.ss7.sccp.parameter.ProtocolClass;
import org.restcomm.protocols.ss7.sccp.parameter.ReleaseCauseValue;

import static org.restcomm.protocols.ss7.sccp.SccpConnectionState.CLOSED;
import static org.restcomm.protocols.ss7.sccp.SccpConnectionState.CONNECTION_INITIATED;
import static org.restcomm.protocols.ss7.sccp.SccpConnectionState.DISCONNECT_INITIATED;
import static org.restcomm.protocols.ss7.sccp.SccpConnectionState.RSR_SENT;

abstract class SccpConnectionWithTimers extends SccpConnectionWithTransmitQueueImpl {

    private ConnEstProcess connEstProcess;
    private IasInactivitySendProcess iasInactivitySendProcess;
    private IarInactivityReceiveProcess iarInactivityReceiveProcess;
    private RelProcess relProcess;
    private RepeatRelProcess repeatRelProcess;
    private IntProcess intProcess;
    private GuardProcess guardProcess;
    private ResetProcess resetProcess;

    public SccpConnectionWithTimers(int sls, int localSsn, LocalReference localReference, ProtocolClass protocol, SccpStackImpl stack, SccpRoutingControl sccpRoutingControl) {
        super(sls, localSsn, localReference, protocol, stack, sccpRoutingControl);
        connEstProcess = new ConnEstProcess();
        iasInactivitySendProcess = new IasInactivitySendProcess();
        iarInactivityReceiveProcess = new IarInactivityReceiveProcess();
        relProcess = new RelProcess();
        repeatRelProcess = new RepeatRelProcess();
        intProcess = new IntProcess();
        guardProcess = new GuardProcess();
        resetProcess = new ResetProcess();
    }

    protected void stopTimers() {
        TimerScheduler timerScheduler = stack.getTimerScheduler();
        if (timerScheduler != null) {
            timerScheduler.cancelAll(SccpTimerIds.connectionScopeId(getLocalReference().getValue()));
        }
        connEstProcess.markStopped();
        iasInactivitySendProcess.markStopped();
        iarInactivityReceiveProcess.markStopped();
        relProcess.markStopped();
        repeatRelProcess.markStopped();
        intProcess.markStopped();
        guardProcess.markStopped();
        resetProcess.markStopped();
    }

    protected void receiveMessage(SccpConnMessage message) throws Exception {
        iarInactivityReceiveProcess.resetTimer();

        if (message instanceof SccpConnCcMessageImpl) {
            connEstProcess.stopTimer();

        } else if (message instanceof SccpConnRscMessageImpl) {
            resetProcess.stopTimer();
        }

        super.receiveMessage(message);
    }

    protected void sendMessage(SccpConnMessage message) throws Exception {
        if (stack.state != SccpStackImpl.State.RUNNING) {
            logger.error("Trying to send SCCP message from SCCP user but SCCP stack is not RUNNING");
            return;
        }
        iasInactivitySendProcess.resetTimer();
        super.sendMessage(message);
    }

    public void setState(SccpConnectionState state) {
        try {
            connectionLock.lock();
            super.setState(state);

            if (state == RSR_SENT) {
                resetProcess.startTimer();

            } else if (state == DISCONNECT_INITIATED) {
                relProcess.startTimer();
                iasInactivitySendProcess.stopTimer();
                iarInactivityReceiveProcess.stopTimer();

            } else if (state == CONNECTION_INITIATED) {
                connEstProcess.startTimer();
            }
        } finally {
            connectionLock.unlock();
        }
    }

    protected class ConnEstProcess extends BaseProcess {
        ConnEstProcess() {
            super(SccpTimerIds.SLOT_CONN_EST, TimerType.SCCP_CONN_EST, stack.getConnEstTimerDelay());
        }

        @Override
        public void run() {
            try {
                connectionLock.lock();
                if (getState() == CLOSED) {
                    return;
                }

                disconnect(new ReleaseCauseImpl(ReleaseCauseValue.SCCP_FAILURE), new byte[]{});

            } catch (Exception e) {
                logger.error(e);
            } finally {
                connectionLock.unlock();
            }
        }
    }

    protected class IasInactivitySendProcess extends BaseProcess {
        IasInactivitySendProcess() {
            super(SccpTimerIds.SLOT_IAS, TimerType.SCCP_IAS, stack.getIasTimerDelay());
        }

        @Override
        public void run() {
            try {
                connectionLock.lock();
                if (getState() == CLOSED || getState() == CONNECTION_INITIATED) {
                    return;
                }

                SccpConnItMessageImpl it = new SccpConnItMessageImpl(getSls(), getLocalSsn());
                it.setProtocolClass(getProtocolClass());
                it.setSourceLocalReferenceNumber(getLocalReference());
                it.setDestinationLocalReferenceNumber(getRemoteReference());

                // could be overwritten during preparing
                it.setCredit(new CreditImpl(0));
                it.setSequencingSegmenting(new SequencingSegmentingImpl(new SequenceNumberImpl(0, false),
                        new SequenceNumberImpl(0, false), lastMoreDataSent));
                prepareMessageForSending(it);
                sendMessage(it);

            } catch (Exception e) {
                logger.error(e);
            } finally {
                connectionLock.unlock();
            }
        }
    }

    protected class IarInactivityReceiveProcess extends BaseProcess {
        IarInactivityReceiveProcess() {
            super(SccpTimerIds.SLOT_IAR, TimerType.SCCP_IAR, stack.getIarTimerDelay());
        }

        @Override
        public void run() {
            try {
                connectionLock.lock();
                if (getState() == CLOSED) {
                    return;
                }

                disconnect(new ReleaseCauseImpl(ReleaseCauseValue.EXPIRATION_OF_RECEIVE_INACTIVITY_TIMER), new byte[] {});

            } catch (Exception e) {
                logger.error(e);
            } finally {
                connectionLock.unlock();
            }
        }
    }

    protected class RelProcess extends BaseProcess {
        RelProcess() {
            super(SccpTimerIds.SLOT_REL, TimerType.SCCP_REL, stack.getRelTimerDelay());
        }

        @Override
        public void startTimer() {
            try {
                connectionLock.lock();
                if (this.isStarted()) {
                    return; // ignore if already started
                }
                super.startTimer();

            } finally {
                connectionLock.unlock();
            }
        }

        @Override
        public void run() {
            try {
                connectionLock.lock();
                if (getState() == CLOSED) {
                    return;
                }

                disconnect(new ReleaseCauseImpl(ReleaseCauseValue.SCCP_FAILURE), new byte[]{});
                intProcess.startTimer();
                repeatRelProcess.startTimer();

            } catch (Exception e) {
                logger.error(e);
            } finally {
                connectionLock.unlock();
            }
        }
    }

    protected class RepeatRelProcess extends BaseProcess {
        RepeatRelProcess() {
            super(SccpTimerIds.SLOT_REPEAT_REL, TimerType.SCCP_REPEAT_REL, stack.getRepeatRelTimerDelay());
        }

        @Override
        public void startTimer() {
            try {
                connectionLock.lock();
                if (this.isStarted()) {
                    return; // ignore if already started
                }
                super.startTimer();

            } finally {
                connectionLock.unlock();
            }
        }

        @Override
        public void run() {
            try {
                connectionLock.lock();
                if (getState() == CLOSED) {
                    return;
                }

                disconnect(new ReleaseCauseImpl(ReleaseCauseValue.SCCP_FAILURE), new byte[]{});
                repeatRelProcess.startTimer();

            } catch (Exception e) {
                logger.error(e);
            } finally {
                connectionLock.unlock();
            }
        }
    }

    protected class IntProcess extends BaseProcess {
        IntProcess() {
            super(SccpTimerIds.SLOT_INT, TimerType.SCCP_INT, stack.getIntTimerDelay());
        }

        @Override
        public void startTimer() {
            try {
                connectionLock.lock();
                if (this.isStarted()) {
                    return; // ignore if already started
                }
                super.startTimer();

            } finally {
                connectionLock.unlock();
            }
        }

        @Override
        public void run() {
            try {
                connectionLock.lock();
                if (getState() == CLOSED) {
                    return;
                }

                repeatRelProcess.stopTimer();

                SccpListener listener = getListener();
                if (listener != null) {
                    listener.onDisconnectIndication((SccpConnection) SccpConnectionWithTimers.this, new ReleaseCauseImpl(ReleaseCauseValue.SCCP_FAILURE), new byte[] {});
                }
                stack.removeConnection(getLocalReference());

            } finally {
                connectionLock.unlock();
            }
        }
    }

    protected class GuardProcess extends BaseProcess {
        GuardProcess() {
            super(SccpTimerIds.SLOT_GUARD, TimerType.SCCP_GUARD, stack.getGuardTimerDelay());
        }

        @Override
        public void run() {
            try {
                connectionLock.lock();
                if (getState() == CLOSED) {
                    return;
                }
                // do smt...

            } finally {
                connectionLock.unlock();
            }
        }
    }

    protected class ResetProcess extends BaseProcess {
        ResetProcess() {
            super(SccpTimerIds.SLOT_RESET, TimerType.SCCP_RESET, stack.getResetTimerDelay());
        }

        @Override
        public void run() {
            try {
                connectionLock.lock();
                if (getState() == CLOSED) {
                    return;
                }

                disconnect(new ReleaseCauseImpl(ReleaseCauseValue.SCCP_FAILURE), new byte[]{});
                stack.removeConnection(getLocalReference());

            } catch (Exception e) {
                logger.error(e);
            } finally {
                connectionLock.unlock();
            }
        }
    }

    private abstract class BaseProcess implements Runnable {
        private final int timerSlot;
        private final TimerType timerType;
        protected long delay;
        private TimerHandle timerHandle;
        private boolean scheduled;

        BaseProcess(int timerSlot, TimerType timerType, long delay) {
            this.timerSlot = timerSlot;
            this.timerType = timerType;
            this.delay = delay;
        }

        private long connectionId() {
            return getLocalReference().getValue();
        }

        private long timerId() {
            return SccpTimerIds.connectionTimerId(connectionId(), timerSlot);
        }

        public void startTimer() {
            try {
                connectionLock.lock();
                if (this.scheduled) {
                    logger.error(new IllegalStateException(String.format("Already started %s timer", getClass())));
                }
                TimerScheduler timerScheduler = stack.getTimerScheduler();
                if (timerScheduler == null) {
                    return;
                }
                timerScheduler.cancel(timerId());
                final BaseProcess self = this;
                this.timerHandle = timerScheduler.schedule(
                        SccpTimerIds.newConnectionRecord(connectionId(), timerSlot, timerType, delay),
                        delay,
                        record -> self.run());
                this.scheduled = true;

            } finally {
                connectionLock.unlock();
            }
        }

        public void stopTimer() {
            try {
                connectionLock.lock();
                TimerScheduler timerScheduler = stack.getTimerScheduler();
                if (timerScheduler != null) {
                    timerScheduler.cancel(timerId());
                }
                if (this.timerHandle != null) {
                    this.timerHandle.cancel();
                    this.timerHandle = null;
                }
                this.scheduled = false;

            } finally {
                connectionLock.unlock();
            }
        }

        void markStopped() {
            this.timerHandle = null;
            this.scheduled = false;
        }

        public void resetTimer() {
            stopTimer();
            startTimer();
        }

        public boolean isStarted() {
            return scheduled;
        }
    }
}
