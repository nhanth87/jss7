package org.restcomm.protocols.ss7.scheduler.impl;

import org.restcomm.protocols.ss7.scheduler.api.TimerCallback;
import org.restcomm.protocols.ss7.scheduler.api.TimerHandle;
import org.restcomm.protocols.ss7.scheduler.api.TimerRecord;
import org.restcomm.protocols.ss7.scheduler.api.TimerScheduler;

/**
 * No-op {@link TimerScheduler} for unit tests.
 */
public class NoOpTimerAdapter implements TimerScheduler {

    @Override
    public TimerHandle schedule(TimerRecord record, long delayMillis, TimerCallback callback) {
        return new TimerHandle() {
            @Override
            public void cancel() {
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean hasFired() {
                return false;
            }
        };
    }

    @Override
    public void cancel(long timerId) {
    }

    @Override
    public void cancelAll(long dialogId) {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
