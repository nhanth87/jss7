package org.restcomm.protocols.ss7.scheduler.api;

/**
 * Protocol timer categories managed by {@link TimerScheduler}, separate from the event dispatcher.
 */
public enum TimerType {

    TCAP_INVOKE_TIMEOUT,
    TCAP_DIALOG_TIMEOUT,
    MAP_T_GUARD_SHORT,
    MAP_T_GUARD_MEDIUM,
    MAP_T_GUARD_LONG,
    CAP_T_SHORT,
    CAP_T_MEDIUM,
    CAP_T_LONG,
    M3UA_FSM_TICK,
    SCCP_CONN_EST,
    SCCP_IAS,
    SCCP_IAR,
    SCCP_REL,
    SCCP_REPEAT_REL,
    SCCP_INT,
    SCCP_GUARD,
    SCCP_RESET,
    SCCP_REASSEMBLY
}
