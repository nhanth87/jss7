package org.restcomm.protocols.ss7.scheduler.api;

/**
 * Protocol timer scheduler backed by a hashed-wheel timer, separate from the event dispatcher.
 */
public interface TimerScheduler {

    TimerHandle schedule(TimerRecord record, long delayMillis, TimerCallback callback);

    void cancel(long timerId);

    void cancelAll(long dialogId);

    void start();

    void stop();
}
