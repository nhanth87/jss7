package org.restcomm.protocols.ss7.scheduler.api;

/**
 * Handle for a scheduled protocol timer.
 */
public interface TimerHandle {

    void cancel();

    boolean isCancelled();

    boolean hasFired();
}
