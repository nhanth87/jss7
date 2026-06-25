package org.restcomm.protocols.ss7.tcap;

import org.restcomm.protocols.ss7.scheduler.api.TimerRecord;
import org.restcomm.protocols.ss7.scheduler.api.TimerType;

/**
 * Stable timer-id encoding for TCAP dialog and invoke timers.
 * <p>
 * Timer ids and {@link TimerScheduler#cancelAll(long)} scope keys include a per-provider
 * stack scope so parallel stacks (and tests) do not collide on the shared scheduler.
 */
final class TcapTimerIds {

    private TcapTimerIds() {
    }

    static long dialogIdleTimerId(int stackScope, long dialogId) {
        return ((long) stackScope << 40) | (dialogId << 16);
    }

    static long invokeTimerId(int stackScope, long dialogId, Long invokeId) {
        return ((long) stackScope << 40) | (dialogId << 16) + (invokeId + 128L);
    }

    static long timerDialogScope(int stackScope, long dialogId) {
        return ((long) stackScope << 32) | (dialogId & 0xFFFFFFFFL);
    }

    static TimerRecord newRecord(long timerId, long dialogScopeId, TimerType timerType, long delayMillis) {
        long now = System.currentTimeMillis();
        return new TimerRecord(timerId, dialogScopeId, timerType, now + delayMillis, null, 1, now);
    }
}
