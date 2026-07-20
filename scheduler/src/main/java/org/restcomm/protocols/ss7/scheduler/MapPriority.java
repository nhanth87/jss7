package org.restcomm.protocols.ss7.scheduler;

/** Default MAP/CAP business criticality used as W2 classification input. */
public enum MapPriority {

    CRITICAL(W2Priority.CRITICAL),
    NORMAL(W2Priority.HIGH),
    LOW(W2Priority.LOW),
    /**
     * No application-specific policy matched.  This must remain serviceable:
     * TCAP END/ABORT are the only default low-priority path, while an unknown
     * decoded application operation is ordinary work.
     */
    UNSPECIFIED(W2Priority.NORMAL);

    private final W2Priority priority;

    MapPriority(W2Priority priority) {
        this.priority = priority;
    }

    public W2Priority priority() {
        return priority;
    }
}
