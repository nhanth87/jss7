package org.restcomm.protocols.ss7.scheduler;

/** Trusted, application-defined traffic grouping used by W2 policy and metrics. */
public enum W2TrafficClass {
    INTERACTIVE,
    DELIVERY,
    CORE_TRANSACTION,
    QUERY,
    NORMAL,
    BULK,
    SYSTEM
}
