package org.restcomm.protocols.ss7.scheduler.api;

/**
 * Callback invoked when a protocol timer fires.
 */
@FunctionalInterface
public interface TimerCallback {

    void onTimerFire(TimerRecord record);
}
