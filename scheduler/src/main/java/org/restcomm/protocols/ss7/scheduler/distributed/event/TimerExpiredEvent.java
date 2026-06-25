package org.restcomm.protocols.ss7.scheduler.distributed.event;

import org.restcomm.protocols.ss7.scheduler.api.TimerRecord;

/**
 * Published when a distributed (or local fallback) timer fires.
 */
public final class TimerExpiredEvent {

    private final long timerId;
    private final long dialogId;
    private final long expiredAtMillis;
    private final TimerRecord record;

    public TimerExpiredEvent(long timerId, long dialogId, long expiredAtMillis, TimerRecord record) {
        this.timerId = timerId;
        this.dialogId = dialogId;
        this.expiredAtMillis = expiredAtMillis;
        this.record = record;
    }

    public long getTimerId() {
        return timerId;
    }

    public long getDialogId() {
        return dialogId;
    }

    public long getExpiredAtMillis() {
        return expiredAtMillis;
    }

    /**
     * Original timer metadata when available (may be null for edge cases during cache eviction).
     */
    public TimerRecord getRecord() {
        return record;
    }

    @Override
    public String toString() {
        return "TimerExpiredEvent{timerId=" + timerId + ", dialogId=" + dialogId
                + ", expiredAtMillis=" + expiredAtMillis + '}';
    }
}
