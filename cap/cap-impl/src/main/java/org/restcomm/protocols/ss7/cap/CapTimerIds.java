package org.restcomm.protocols.ss7.cap;

import org.restcomm.protocols.ss7.scheduler.api.TimerRecord;
import org.restcomm.protocols.ss7.scheduler.api.TimerType;

final class CapTimerIds {

    private CapTimerIds() {
    }

    static long guardTimerId(long dialogId, Long invokeId) {
        return (dialogId << 16) + 0x9000L + (invokeId + 128L);
    }

    static TimerRecord newGuardRecord(long timerId, long dialogId, long delayMillis) {
        long now = System.currentTimeMillis();
        return new TimerRecord(timerId, dialogId, TimerType.CAP_T_MEDIUM, now + delayMillis, null, 1, now);
    }
}
