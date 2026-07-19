package org.restcomm.protocols.ss7.scheduler.w2.infinispan;

import java.io.Serializable;

/** Replicated ownership lease. Epoch is a fencing token, not a wall-clock version. */
public record W2Lease(String ownerNodeId, long epoch, long expiresAtEpochMs) implements Serializable {

    private static final long serialVersionUID = 1L;

    public W2Lease {
        if (ownerNodeId == null || ownerNodeId.isBlank()) {
            throw new IllegalArgumentException("ownerNodeId must not be blank");
        }
        if (epoch <= 0 || expiresAtEpochMs < 0) {
            throw new IllegalArgumentException("invalid lease epoch or expiry");
        }
    }

    public boolean isHeldBy(String nodeId, long expectedEpoch, long nowEpochMs) {
        return ownerNodeId.equals(nodeId) && epoch == expectedEpoch && expiresAtEpochMs > nowEpochMs;
    }

    public boolean isExpired(long nowEpochMs) {
        return expiresAtEpochMs <= nowEpochMs;
    }
}
