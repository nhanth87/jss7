package org.restcomm.protocols.ss7.scheduler;

/** Resource profile used by an integration to select an isolated execution pool. */
public enum W2ExecutionProfile {
    CPU,
    BLOCKING_IO,
    NETWORK_IO,
    MEMORY_INTENSIVE,
    GENERAL
}
