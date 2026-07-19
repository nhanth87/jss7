package org.restcomm.protocols.ss7.scheduler;

/** Immutable operational snapshot of a {@link W2PriorityQueue}. */
public record W2QueueMetrics(int capacity, int depth, long admitted, long rejected, long polled) {
}
