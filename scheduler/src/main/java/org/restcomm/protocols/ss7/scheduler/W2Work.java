package org.restcomm.protocols.ss7.scheduler;

import java.util.Objects;

/**
 * Immutable metadata and payload submitted to {@link W2PriorityQueue}.
 *
 * <p>{@code deadlineNanos} is a monotonic-clock deadline. Use
 * {@link Long#MAX_VALUE} when no product scheduling deadline exists. It is
 * scheduling metadata, not permission to alter a TCAP timer/state transition.</p>
 *
 * @param <T> payload owned by the integration layer
 */
public final class W2Work<T> {

    private final String dialogKey;
    private final W2Priority priority;
    private final long deadlineNanos;
    private final T payload;

    public W2Work(String dialogKey, W2Priority priority, long deadlineNanos, T payload) {
        this.dialogKey = Objects.requireNonNull(dialogKey, "dialogKey");
        this.priority = Objects.requireNonNull(priority, "priority");
        if (deadlineNanos < 0) {
            throw new IllegalArgumentException("deadlineNanos must be non-negative");
        }
        this.deadlineNanos = deadlineNanos;
        this.payload = Objects.requireNonNull(payload, "payload");
    }

    public String dialogKey() {
        return dialogKey;
    }

    public W2Priority priority() {
        return priority;
    }

    public long deadlineNanos() {
        return deadlineNanos;
    }

    public T payload() {
        return payload;
    }
}
