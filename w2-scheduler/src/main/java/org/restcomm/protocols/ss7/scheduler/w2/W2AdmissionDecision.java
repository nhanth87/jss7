package org.restcomm.protocols.ss7.scheduler.w2;

/** Outcome of a non-blocking W2 admission attempt. */
public enum W2AdmissionDecision {
    ACCEPTED,
    REJECTED_STOPPED,
    REJECTED_GLOBAL_CAPACITY,
    REJECTED_TENANT_CAPACITY,
    REJECTED_ORDERING_KEY_CAPACITY
}
