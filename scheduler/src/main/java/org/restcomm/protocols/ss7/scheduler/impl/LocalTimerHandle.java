package org.restcomm.protocols.ss7.scheduler.impl;

import io.netty.util.Timeout;
import org.restcomm.protocols.ss7.scheduler.api.TimerHandle;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Timer handle with CAS-based state transitions for cancel and fire.
 */
public class LocalTimerHandle implements TimerHandle {

    private static final int STATE_PENDING = 0;
    private static final int STATE_CANCELLED = 1;
    private static final int STATE_FIRED = 2;

    private final AtomicInteger state = new AtomicInteger(STATE_PENDING);
    private volatile Timeout timeout;

    void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    @Override
    public void cancel() {
        if (state.compareAndSet(STATE_PENDING, STATE_CANCELLED)) {
            Timeout current = timeout;
            if (current != null) {
                current.cancel();
            }
        }
    }

    @Override
    public boolean isCancelled() {
        return state.get() == STATE_CANCELLED;
    }

    @Override
    public boolean hasFired() {
        return state.get() == STATE_FIRED;
    }

    boolean markFired() {
        return state.compareAndSet(STATE_PENDING, STATE_FIRED);
    }
}
