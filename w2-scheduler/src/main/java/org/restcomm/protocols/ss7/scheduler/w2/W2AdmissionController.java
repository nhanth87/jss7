package org.restcomm.protocols.ss7.scheduler.w2;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.restcomm.protocols.ss7.scheduler.W2Work;

/**
 * Thread-safe, local admission accounting for global, tenant and ordering-key capacity.
 *
 * <p>The caller must call {@link #release(W2Work)} exactly once for every accepted item after
 * it leaves its local mailbox. This controller intentionally does not decide whether an adapter
 * should retry, shed, fail fast or execute inline.</p>
 */
public final class W2AdmissionController {

    private final W2AdmissionLimits limits;
    private final Map<String, Integer> tenantDepths = new HashMap<>();
    private final Map<String, Integer> orderingKeyDepths = new HashMap<>();
    private int globalDepth;

    public W2AdmissionController(W2AdmissionLimits limits) {
        this.limits = Objects.requireNonNull(limits, "limits");
    }

    public synchronized W2AdmissionResult admit(W2Work<?> work) {
        Objects.requireNonNull(work, "work");
        if (globalDepth >= limits.globalCapacity()) {
            return new W2AdmissionResult(W2AdmissionDecision.REJECTED_GLOBAL_CAPACITY);
        }
        if (tenantDepths.getOrDefault(work.tenantKey(), 0) >= limits.perTenantCapacity()) {
            return new W2AdmissionResult(W2AdmissionDecision.REJECTED_TENANT_CAPACITY);
        }
        if (orderingKeyDepths.getOrDefault(work.orderingKey(), 0) >= limits.perOrderingKeyCapacity()) {
            return new W2AdmissionResult(W2AdmissionDecision.REJECTED_ORDERING_KEY_CAPACITY);
        }
        globalDepth++;
        increment(tenantDepths, work.tenantKey());
        increment(orderingKeyDepths, work.orderingKey());
        return W2AdmissionResult.ACCEPTED;
    }

    public synchronized void release(W2Work<?> work) {
        Objects.requireNonNull(work, "work");
        if (globalDepth <= 0) {
            throw new IllegalStateException("admission release without an accepted work item");
        }
        globalDepth--;
        decrement(tenantDepths, work.tenantKey());
        decrement(orderingKeyDepths, work.orderingKey());
    }

    public synchronized int globalDepth() {
        return globalDepth;
    }

    private static void increment(Map<String, Integer> depths, String key) {
        depths.merge(key, 1, Integer::sum);
    }

    private static void decrement(Map<String, Integer> depths, String key) {
        int depth = depths.getOrDefault(key, 0);
        if (depth <= 0) {
            throw new IllegalStateException("admission release without a matching key");
        }
        if (depth == 1) {
            depths.remove(key);
        } else {
            depths.put(key, depth - 1);
        }
    }
}
