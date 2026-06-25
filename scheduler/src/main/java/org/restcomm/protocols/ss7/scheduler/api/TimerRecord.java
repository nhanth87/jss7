package org.restcomm.protocols.ss7.scheduler.api;

import java.io.Serializable;

/**
 * Serializable metadata for a scheduled protocol timer.
 */
public class TimerRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final long timerId;
    private final long dialogId;
    private final TimerType timerType;
    private final long expiresAtMillis;
    private final String nodeId;
    private final int version;
    private final long createdAtMillis;

    public TimerRecord(long timerId, long dialogId, TimerType timerType, long expiresAtMillis, String nodeId,
            int version, long createdAtMillis) {
        this.timerId = timerId;
        this.dialogId = dialogId;
        this.timerType = timerType;
        this.expiresAtMillis = expiresAtMillis;
        this.nodeId = nodeId;
        this.version = version;
        this.createdAtMillis = createdAtMillis;
    }

    public long getTimerId() {
        return timerId;
    }

    public long getDialogId() {
        return dialogId;
    }

    public TimerType getTimerType() {
        return timerType;
    }

    public long getExpiresAtMillis() {
        return expiresAtMillis;
    }

    public String getNodeId() {
        return nodeId;
    }

    public int getVersion() {
        return version;
    }

    public long getCreatedAtMillis() {
        return createdAtMillis;
    }
}
