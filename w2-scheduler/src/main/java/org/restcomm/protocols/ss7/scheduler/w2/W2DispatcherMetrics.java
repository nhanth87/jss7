package org.restcomm.protocols.ss7.scheduler.w2;

/** Immutable operational snapshot of a {@link W2Dispatcher}. */
public record W2DispatcherMetrics(int capacity, int depth, long admitted, long rejected, long dispatched,
        long failed, boolean accepting, boolean running) {
}
