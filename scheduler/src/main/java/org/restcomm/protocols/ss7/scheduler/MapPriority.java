package org.restcomm.protocols.ss7.scheduler;

/** Default MAP/CAP business criticality used as W2 classification input. */
public enum MapPriority {

    CRITICAL(W2Priority.CRITICAL),
    NORMAL(W2Priority.HIGH),
    LOW(W2Priority.LOW),
    UNSPECIFIED(W2Priority.LOW);

    private final W2Priority priority;

    MapPriority(W2Priority priority) {
        this.priority = priority;
    }

    public W2Priority priority() {
        return priority;
    }
}
