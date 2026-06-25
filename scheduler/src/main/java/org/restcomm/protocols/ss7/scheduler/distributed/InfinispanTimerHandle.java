package org.restcomm.protocols.ss7.scheduler.distributed;

import org.restcomm.protocols.ss7.scheduler.api.TimerHandle;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Timer handle with CAS-based state transitions for cancel and fire.
 */
final class InfinispanTimerHandle implements TimerHandle {

    private static final int STATE_PENDING = 0;
    private static final int STATE_CANCELLED = 1;
    private static final int STATE_FIRED = 2;

    private final AtomicInteger state = new AtomicInteger(STATE_PENDING);

    @Override
    public void cancel() {
        state.compareAndSet(STATE_PENDING, STATE_CANCELLED);
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
