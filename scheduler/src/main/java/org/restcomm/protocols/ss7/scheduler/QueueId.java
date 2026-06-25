package org.restcomm.protocols.ss7.scheduler;

/**
 * Typed queue identifiers for the event dispatcher scheduler.
 */
public enum QueueId {

    MANAGEMENT(0),
    L2READ(1),
    L3READ(2),
    L4READ(3),
    TCAP_READ(4),
    APP_READ(5),
    APP_WRITE(6),
    TCAP_WRITE(7),
    L4WRITE(8),
    L3WRITE(9),
    L2WRITE(10);

    private final int index;

    QueueId(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
