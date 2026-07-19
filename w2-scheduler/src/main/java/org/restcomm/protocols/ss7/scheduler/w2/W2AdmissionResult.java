package org.restcomm.protocols.ss7.scheduler.w2;

/** Detailed result returned by an admission policy without prescribing a caller response. */
public record W2AdmissionResult(W2AdmissionDecision decision) {

    public static final W2AdmissionResult ACCEPTED = new W2AdmissionResult(W2AdmissionDecision.ACCEPTED);

    public boolean accepted() {
        return decision == W2AdmissionDecision.ACCEPTED;
    }
}
