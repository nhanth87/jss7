package org.restcomm.protocols.ss7.scheduler;

/**
 * Default W2 priority by TCAP package class.
 *
 * <p>The package kind is not a TCAP component kind. In particular, BEGIN,
 * CONTINUE, END and ABORT are TCAP packages; components are INVOKE, RESULT,
 * ERROR and REJECT.</p>
 */
public enum TcapPriority {

    BEGIN_HIGH(W2Priority.CRITICAL),
    CONTINUE_LARGE(W2Priority.HIGH),
    CONTINUE_NORMAL(W2Priority.NORMAL),
    END_LOW(W2Priority.LOW),
    ABORT_LOW(W2Priority.LOW),
    OTHER_NORMAL(W2Priority.NORMAL);

    private final W2Priority priority;

    TcapPriority(W2Priority priority) {
        this.priority = priority;
    }

    public W2Priority priority() {
        return priority;
    }
}
