package org.restcomm.protocols.ss7.scheduler.w2;

/** Bounded local admission limits. All limits must be positive. */
public record W2AdmissionLimits(int globalCapacity, int perTenantCapacity, int perOrderingKeyCapacity) {

    public W2AdmissionLimits {
        if (globalCapacity <= 0 || perTenantCapacity <= 0 || perOrderingKeyCapacity <= 0) {
            throw new IllegalArgumentException("all admission capacities must be positive");
        }
    }

    public static W2AdmissionLimits globalOnly(int globalCapacity) {
        return new W2AdmissionLimits(globalCapacity, globalCapacity, globalCapacity);
    }
}
