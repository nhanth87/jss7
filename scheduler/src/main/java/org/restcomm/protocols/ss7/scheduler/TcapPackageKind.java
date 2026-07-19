package org.restcomm.protocols.ss7.scheduler;

/** Protocol-neutral TCAP package metadata accepted by {@link W2PriorityClassifier}. */
public enum TcapPackageKind {

    BEGIN,
    CONTINUE,
    END,
    ABORT,
    UNIDIRECTIONAL,
    UNKNOWN
}
