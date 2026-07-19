package org.restcomm.protocols.ss7.scheduler.w2;

/** Immutable operational snapshot of a {@link W2KeyedMailboxDispatcher}. */
public record W2KeyedMailboxMetrics(int capacity, int depth, int activeMailboxes, long admitted, long rejected,
        long dispatched, long failed, boolean accepting, boolean running) {
}
