package org.restcomm.protocols.ss7.m3ua.impl;

import org.restcomm.protocols.ss7.scheduler.api.TimerRecord;
import org.restcomm.protocols.ss7.scheduler.api.TimerType;

/**
 * Stable timer-id encoding for M3UA management timers.
 */
final class M3uaTimerIds {

    /** Sentinel dialog id for stack-global timers (FSM sweep). */
    static final long GLOBAL_DIALOG_ID = 0L;

    private static final long FSM_TICK_TIMER_ID = 1L;

    private M3uaTimerIds() {
    }

    static long fsmTickTimerId() {
        return FSM_TICK_TIMER_ID;
    }

    static TimerRecord newFsmTickRecord(long delayMillis) {
        long now = System.currentTimeMillis();
        return new TimerRecord(FSM_TICK_TIMER_ID, GLOBAL_DIALOG_ID, TimerType.M3UA_FSM_TICK, now + delayMillis, null, 1,
                now);
    }
}
