package org.restcomm.protocols.ss7.tcap.api;

import java.io.Serializable;
import java.util.Arrays;

import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.TRPseudoState;

/**
 * Portable TCAP dialog fields for failover / CONTINUE takeover.
 * <p>
 * This is intentionally <strong>not</strong> a live {@code DialogImpl}: no locks,
 * executors, or scheduled timer handles are serialized. Timers are carried as
 * <em>absolute deadlines</em> and re-armed locally on import.
 *
 * <h3>Why {@link PendingInvoke} exists</h3>
 * A dialog with an outstanding invoke cannot be answered by a peer that does not
 * own it: {@code DialogImpl.processOperationsState} resolves the incoming
 * component's invoke id against {@code operationsSent[]}, and on a miss answers
 * {@code Reject(UnrecognizedInvokeID)} <em>to the real peer</em> — before the MAP
 * layer ever sees the component. Without the pending-invoke list, cross-JVM
 * takeover of a mid-flight MAP operation is not merely lossy, it actively
 * corrupts the dialogue at the far end. Class is a purely local property and is
 * never on the wire, so it must be carried explicitly.
 *
 * <p>
 * <b>Still not sufficient for full STP HA:</b> MAP/CAP/INAP dialogue state above
 * TCAP (references, ERI, extension containers) is not in this record, and the
 * operation-timeout <em>remaining</em> budget is best-effort.
 *
 * @author Tran Nhan
 */
public final class TcapDialogSnapshot implements Serializable {

    private static final long serialVersionUID = 3L;

    private final long localOtid;
    private final byte[] remoteOtid;
    private final SccpAddress localAddress;
    private final SccpAddress remoteAddress;
    private final TRPseudoState state;
    private final long[] applicationContextOid;
    /**
     * Absolute wall-clock deadline (ms since epoch) for the dialog idle timer.
     * <p>
     * <b>Wall clock, not {@code System.nanoTime()}.</b> A monotonic per-JVM clock
     * is meaningless across JVMs: importing a {@code nanoTime()} value silently
     * grants a fresh full idle window (or expires instantly) depending on uptime
     * delta. The importing JVM converts this back into its own {@code nanoTime}
     * base when re-arming.
     */
    private final long idleDeadlineEpochMs;
    private final int networkId;
    private final int localSsn;
    private final int remotePc;
    private final int seqControl;
    private final boolean dpSentInBegin;
    private final boolean[] invokeIdTaken;
    private final String preferredAspName;
    /** Outstanding operations awaiting a result; {@code null}/empty = none. */
    private final PendingInvoke[] pendingInvokes;

    public TcapDialogSnapshot(long localOtid, byte[] remoteOtid, SccpAddress localAddress, SccpAddress remoteAddress,
            TRPseudoState state, long[] applicationContextOid, long idleDeadlineEpochMs, int networkId, int localSsn,
            int remotePc, int seqControl, boolean dpSentInBegin, boolean[] invokeIdTaken) {
        this(localOtid, remoteOtid, localAddress, remoteAddress, state, applicationContextOid, idleDeadlineEpochMs,
                networkId, localSsn, remotePc, seqControl, dpSentInBegin, invokeIdTaken, null, null);
    }

    public TcapDialogSnapshot(long localOtid, byte[] remoteOtid, SccpAddress localAddress, SccpAddress remoteAddress,
            TRPseudoState state, long[] applicationContextOid, long idleDeadlineEpochMs, int networkId, int localSsn,
            int remotePc, int seqControl, boolean dpSentInBegin, boolean[] invokeIdTaken, String preferredAspName) {
        this(localOtid, remoteOtid, localAddress, remoteAddress, state, applicationContextOid, idleDeadlineEpochMs,
                networkId, localSsn, remotePc, seqControl, dpSentInBegin, invokeIdTaken, preferredAspName, null);
    }

    public TcapDialogSnapshot(long localOtid, byte[] remoteOtid, SccpAddress localAddress, SccpAddress remoteAddress,
            TRPseudoState state, long[] applicationContextOid, long idleDeadlineEpochMs, int networkId, int localSsn,
            int remotePc, int seqControl, boolean dpSentInBegin, boolean[] invokeIdTaken, String preferredAspName,
            PendingInvoke[] pendingInvokes) {
        this.localOtid = localOtid;
        this.remoteOtid = remoteOtid == null ? null : Arrays.copyOf(remoteOtid, remoteOtid.length);
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
        this.state = state;
        this.applicationContextOid = applicationContextOid == null ? null
                : Arrays.copyOf(applicationContextOid, applicationContextOid.length);
        this.idleDeadlineEpochMs = idleDeadlineEpochMs;
        this.networkId = networkId;
        this.localSsn = localSsn;
        this.remotePc = remotePc;
        this.seqControl = seqControl;
        this.dpSentInBegin = dpSentInBegin;
        this.invokeIdTaken = invokeIdTaken == null ? null : Arrays.copyOf(invokeIdTaken, invokeIdTaken.length);
        this.preferredAspName = preferredAspName;
        this.pendingInvokes = pendingInvokes == null ? null : pendingInvokes.clone();
    }

    /**
     * One outstanding operation: enough to rebuild an {@code InvokeImpl} in
     * {@code Sent} state so a peer's {@code ReturnResult(Last)} / {@code ReturnError}
     * is accepted instead of rejected.
     *
     * @param invokeId             TCAP invoke id (-128..127)
     * @param invokeClass          1..4 (Q.771 §2.3.1.3); locally assigned, never on the wire
     * @param localOperationCode   operation code for local (non-global) ops; {@code null} for global
     * @param invokeTimeoutMs      configured operation timeout
     * @param remainingMillis      budget left on the operation timer at export time; {@code <= 0} means unknown
     */
    public static final class PendingInvoke implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int invokeId;
        private final int invokeClass;
        private final Long localOperationCode;
        private final long invokeTimeoutMs;
        private final long remainingMillis;

        public PendingInvoke(int invokeId, int invokeClass, Long localOperationCode, long invokeTimeoutMs,
                long remainingMillis) {
            this.invokeId = invokeId;
            this.invokeClass = invokeClass;
            this.localOperationCode = localOperationCode;
            this.invokeTimeoutMs = invokeTimeoutMs;
            this.remainingMillis = remainingMillis;
        }

        public int getInvokeId() {
            return invokeId;
        }

        public int getInvokeClass() {
            return invokeClass;
        }

        public Long getLocalOperationCode() {
            return localOperationCode;
        }

        public long getInvokeTimeoutMs() {
            return invokeTimeoutMs;
        }

        public long getRemainingMillis() {
            return remainingMillis;
        }

        @Override
        public String toString() {
            return "PendingInvoke{id=" + invokeId + ", class=" + invokeClass + ", op="
                    + localOperationCode + ", remainingMs=" + remainingMillis + "}";
        }
    }

    public PendingInvoke[] getPendingInvokes() {
        return pendingInvokes == null ? null : pendingInvokes.clone();
    }

    public long getLocalOtid() {
        return localOtid;
    }

    public byte[] getRemoteOtid() {
        return remoteOtid == null ? null : Arrays.copyOf(remoteOtid, remoteOtid.length);
    }

    public SccpAddress getLocalAddress() {
        return localAddress;
    }

    public SccpAddress getRemoteAddress() {
        return remoteAddress;
    }

    public TRPseudoState getState() {
        return state;
    }

    public long[] getApplicationContextOid() {
        return applicationContextOid == null ? null
                : Arrays.copyOf(applicationContextOid, applicationContextOid.length);
    }

    /**
     * Absolute wall-clock idle deadline, ms since epoch. {@code 0} = disarmed /
     * unknown. See the field javadoc for why this is not {@code nanoTime}.
     */
    public long getIdleDeadlineEpochMs() {
        return idleDeadlineEpochMs;
    }

    /**
     * @deprecated renamed to {@link #getIdleDeadlineEpochMs()}; the old name said
     * "nanos" and callers that cross a JVM boundary would have applied a
     * per-JVM monotonic value as if it were a wall clock.
     */
    @Deprecated
    public long getIdleDeadlineNanos() {
        return idleDeadlineEpochMs;
    }

    public int getNetworkId() {
        return networkId;
    }

    public int getLocalSsn() {
        return localSsn;
    }

    public int getRemotePc() {
        return remotePc;
    }

    public int getSeqControl() {
        return seqControl;
    }

    public boolean isDpSentInBegin() {
        return dpSentInBegin;
    }

    public boolean[] getInvokeIdTaken() {
        return invokeIdTaken == null ? null : Arrays.copyOf(invokeIdTaken, invokeIdTaken.length);
    }

    public String getPreferredAspName() {
        return preferredAspName;
    }

    @Override
    public String toString() {
        return "TcapDialogSnapshot{localOtid=" + localOtid + ", remoteOtid="
                + (remoteOtid == null ? "null" : Arrays.toString(remoteOtid)) + ", state=" + state + ", networkId="
                + networkId + ", preferredAsp=" + preferredAspName + ", pendingInvokes="
                + (pendingInvokes == null ? 0 : pendingInvokes.length) + "}";
    }
}
