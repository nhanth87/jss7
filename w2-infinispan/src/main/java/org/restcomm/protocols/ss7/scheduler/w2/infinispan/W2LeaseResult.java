package org.restcomm.protocols.ss7.scheduler.w2.infinispan;

/** Result of acquiring or renewing a fenced ownership lease. */
public record W2LeaseResult(boolean acquired, W2Lease lease) {

    public static W2LeaseResult denied(W2Lease lease) {
        return new W2LeaseResult(false, lease);
    }

    public static W2LeaseResult acquired(W2Lease lease) {
        return new W2LeaseResult(true, lease);
    }
}
