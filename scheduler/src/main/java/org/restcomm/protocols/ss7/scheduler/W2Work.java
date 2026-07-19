package org.restcomm.protocols.ss7.scheduler;

import java.util.Objects;

/**
 * Immutable generic scheduling metadata and payload submitted to {@link W2PriorityQueue}.
 *
 * <p>{@code orderingKey} identifies work which must remain causally ordered. It is a dialog
 * key for SS7, but may identify an account, session, topic partition or aggregate in another
 * integration. {@code deadlineNanos} is a local monotonic-clock deadline; it is scheduling
 * metadata and never replaces an application's protocol timer/state transition.</p>
 *
 * @param <T> payload owned by the integration layer
 */
public final class W2Work<T> {

    private final String orderingKey;
    private final String tenantKey;
    private final W2TrafficClass trafficClass;
    private final W2CostHint costHint;
    private final W2ExecutionProfile executionProfile;
    private final W2Priority priority;
    private final long deadlineNanos;
    private final T payload;

    /**
     * Compatibility constructor. The supplied dialog key is now an ordering key; callers are
     * encouraged to use the full constructor for tenant, class and resource metadata.
     */
    public W2Work(String dialogKey, W2Priority priority, long deadlineNanos, T payload) {
        this(dialogKey, W2WorkMetadata.defaults(), priority, deadlineNanos, payload);
    }

    public W2Work(String orderingKey, W2WorkMetadata metadata, W2Priority priority, long deadlineNanos, T payload) {
        this.orderingKey = requireKey(orderingKey, "orderingKey");
        Objects.requireNonNull(metadata, "metadata");
        this.tenantKey = metadata.tenantKey();
        this.trafficClass = metadata.trafficClass();
        this.costHint = metadata.costHint();
        this.executionProfile = metadata.executionProfile();
        this.priority = Objects.requireNonNull(priority, "priority");
        if (deadlineNanos < 0) {
            throw new IllegalArgumentException("deadlineNanos must be non-negative");
        }
        this.deadlineNanos = deadlineNanos;
        this.payload = Objects.requireNonNull(payload, "payload");
    }

    public String orderingKey() {
        return orderingKey;
    }

    /** @deprecated use {@link #orderingKey()}; W2 is not restricted to TCAP dialogs. */
    @Deprecated
    public String dialogKey() {
        return orderingKey;
    }

    public String tenantKey() {
        return tenantKey;
    }

    public W2TrafficClass trafficClass() {
        return trafficClass;
    }

    public W2CostHint costHint() {
        return costHint;
    }

    public W2ExecutionProfile executionProfile() {
        return executionProfile;
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

    private static String requireKey(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
