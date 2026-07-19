package org.restcomm.protocols.ss7.scheduler;

import java.util.Objects;

/** Generic policy metadata that does not belong to a protocol payload. */
public record W2WorkMetadata(String tenantKey, W2TrafficClass trafficClass, W2CostHint costHint,
        W2ExecutionProfile executionProfile) {

    public static final String DEFAULT_TENANT = "default";

    public W2WorkMetadata {
        if (tenantKey == null || tenantKey.isBlank()) {
            throw new IllegalArgumentException("tenantKey must not be blank");
        }
        Objects.requireNonNull(trafficClass, "trafficClass");
        Objects.requireNonNull(costHint, "costHint");
        Objects.requireNonNull(executionProfile, "executionProfile");
    }

    public static W2WorkMetadata defaults() {
        return new W2WorkMetadata(DEFAULT_TENANT, W2TrafficClass.NORMAL, W2CostHint.UNKNOWN,
                W2ExecutionProfile.GENERAL);
    }
}
