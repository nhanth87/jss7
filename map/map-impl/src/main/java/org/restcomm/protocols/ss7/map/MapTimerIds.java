package org.restcomm.protocols.ss7.map;

import org.restcomm.protocols.ss7.scheduler.api.TimerRecord;
import org.restcomm.protocols.ss7.scheduler.api.TimerType;

final class MapTimerIds {

    private MapTimerIds() {
    }

    static long guardTimerId(long dialogId, Long invokeId) {
        return (dialogId << 16) + 0x8000L + (invokeId + 128L);
    }

    static TimerRecord newGuardRecord(long timerId, long dialogId, long delayMillis) {
        long now = System.currentTimeMillis();
        return new TimerRecord(timerId, dialogId, TimerType.MAP_T_GUARD_MEDIUM, now + delayMillis, null, 1, now);
    }
}
