package org.restcomm.protocols.ss7.tcap.api;

import java.io.Serializable;
import java.util.Arrays;

import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.tcap.api.tc.dialog.TRPseudoState;

/**
 * Portable TCAP dialog fields for failover / CONTINUE takeover spikes.
 * <p>
 * This is intentionally <strong>not</strong> a live {@code DialogImpl}: no locks,
 * executors, invoke operation objects, or scheduled timers are serialized.
 * <p>
 * <b>SPIKE / not production HA:</b> rehydrating this into another JVM's
 * {@code dialogs} map is necessary but not sufficient for multi-node STP failover
 * (multi-ASP routing, OTID ranges, MAP dialogue state, invoke tables, timers).
 *
 * @author Tran Nhan
 */
public final class TcapDialogSnapshot implements Serializable {

    private static final long serialVersionUID = 2L;

    private final long localOtid;
    private final byte[] remoteOtid;
    private final SccpAddress localAddress;
    private final SccpAddress remoteAddress;
    private final TRPseudoState state;
    private final long[] applicationContextOid;
    private final long idleDeadlineNanos;
    private final int networkId;
    private final int localSsn;
    private final int remotePc;
    private final int seqControl;
    private final boolean dpSentInBegin;
    private final boolean[] invokeIdTaken;
    private final String preferredAspName;

    public TcapDialogSnapshot(long localOtid, byte[] remoteOtid, SccpAddress localAddress, SccpAddress remoteAddress,
            TRPseudoState state, long[] applicationContextOid, long idleDeadlineNanos, int networkId, int localSsn,
            int remotePc, int seqControl, boolean dpSentInBegin, boolean[] invokeIdTaken) {
        this(localOtid, remoteOtid, localAddress, remoteAddress, state, applicationContextOid, idleDeadlineNanos,
                networkId, localSsn, remotePc, seqControl, dpSentInBegin, invokeIdTaken, null);
    }

    public TcapDialogSnapshot(long localOtid, byte[] remoteOtid, SccpAddress localAddress, SccpAddress remoteAddress,
            TRPseudoState state, long[] applicationContextOid, long idleDeadlineNanos, int networkId, int localSsn,
            int remotePc, int seqControl, boolean dpSentInBegin, boolean[] invokeIdTaken, String preferredAspName) {
        this.localOtid = localOtid;
        this.remoteOtid = remoteOtid == null ? null : Arrays.copyOf(remoteOtid, remoteOtid.length);
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
        this.state = state;
        this.applicationContextOid = applicationContextOid == null ? null
                : Arrays.copyOf(applicationContextOid, applicationContextOid.length);
        this.idleDeadlineNanos = idleDeadlineNanos;
        this.networkId = networkId;
        this.localSsn = localSsn;
        this.remotePc = remotePc;
        this.seqControl = seqControl;
        this.dpSentInBegin = dpSentInBegin;
        this.invokeIdTaken = invokeIdTaken == null ? null : Arrays.copyOf(invokeIdTaken, invokeIdTaken.length);
        this.preferredAspName = preferredAspName;
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

    public long getIdleDeadlineNanos() {
        return idleDeadlineNanos;
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
                + networkId + ", preferredAsp=" + preferredAspName + "}";
    }
}
