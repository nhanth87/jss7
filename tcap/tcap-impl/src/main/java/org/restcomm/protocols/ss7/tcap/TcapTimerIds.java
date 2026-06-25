package org.restcomm.protocols.ss7.tcap;

import org.restcomm.protocols.ss7.scheduler.api.TimerRecord;
import org.restcomm.protocols.ss7.scheduler.api.TimerType;

/**
 * Stable timer-id encoding for TCAP dialog and invoke timers.
 */
final class TcapTimerIds {

    private TcapTimerIds() {
    }

    static long dialogIdleTimerId(long dialogId) {
        return (dialogId << 16);
    }

    static long invokeTimerId(long dialogId, Long invokeId) {
        return (dialogId << 16) + (invokeId + 128L);
    }

    static TimerRecord newRecord(long timerId, long dialogId, TimerType timerType, long delayMillis) {
        long now = System.currentTimeMillis();
        return new TimerRecord(timerId, dialogId, timerType, now + delayMillis, null, 1, now);
    }
}
