package org.restcomm.protocols.ss7.sccp.impl;

import org.restcomm.protocols.ss7.scheduler.api.TimerRecord;
import org.restcomm.protocols.ss7.scheduler.api.TimerType;

/**
 * Stable timer-id encoding for SCCP connection and reassembly timers.
 */
final class SccpTimerIds {

    static final int SLOT_CONN_EST = 0;
    static final int SLOT_IAS = 1;
    static final int SLOT_IAR = 2;
    static final int SLOT_REL = 3;
    static final int SLOT_REPEAT_REL = 4;
    static final int SLOT_INT = 5;
    static final int SLOT_GUARD = 6;
    static final int SLOT_RESET = 7;
    static final int SLOT_REASSEMBLY = 8;

    private static final int SLOT_BITS = 8;

    private SccpTimerIds() {
    }

    static long connectionTimerId(long connectionId, int slot) {
        return (connectionId << SLOT_BITS) | (slot & 0xFF);
    }

    static long connectionScopeId(long connectionId) {
        return connectionId;
    }

    static long reassemblyTimerId(int segmentationLocalRef) {
        return (reassemblyScopeId(segmentationLocalRef) << SLOT_BITS) | SLOT_REASSEMBLY;
    }

    static long reassemblyScopeId(int segmentationLocalRef) {
        return 0x100000000L | (segmentationLocalRef & 0xFFFFFFFFL);
    }

    static TimerRecord newConnectionRecord(long connectionId, int slot, TimerType timerType, long delayMillis) {
        long timerId = connectionTimerId(connectionId, slot);
        return newRecord(timerId, connectionScopeId(connectionId), timerType, delayMillis);
    }

    static TimerRecord newReassemblyRecord(int segmentationLocalRef, long delayMillis) {
        long scopeId = reassemblyScopeId(segmentationLocalRef);
        long timerId = reassemblyTimerId(segmentationLocalRef);
        return newRecord(timerId, scopeId, TimerType.SCCP_REASSEMBLY, delayMillis);
    }

    static TimerRecord newRecord(long timerId, long scopeId, TimerType timerType, long delayMillis) {
        long now = System.currentTimeMillis();
        return new TimerRecord(timerId, scopeId, timerType, now + delayMillis, null, 1, now);
    }
}
