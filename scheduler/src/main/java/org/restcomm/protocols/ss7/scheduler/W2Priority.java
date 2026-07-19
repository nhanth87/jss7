package org.restcomm.protocols.ss7.scheduler;

/**
 * Immutable business-priority tier used by the W2 work-selection policy.
 *
 * <p>This priority is intentionally separate from the legacy numeric Scheduler
 * lanes. A higher score is selected first; deadline and submission sequence
 * resolve ties.</p>
 */
public enum W2Priority {

    LOW(0),
    NORMAL(1),
    HIGH(2),
    CRITICAL(3);

    private final int score;

    W2Priority(int score) {
        this.score = score;
    }

    public int score() {
        return score;
    }
}
