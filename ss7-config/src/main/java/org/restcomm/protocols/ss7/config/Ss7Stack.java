/*
 * jSS7 :: ss7-config
 */
package org.restcomm.protocols.ss7.config;

import org.mobicents.protocols.api.Management;
import org.restcomm.protocols.ss7.cap.api.CAPProvider;
import org.restcomm.protocols.ss7.cap.api.CAPStack;
import org.restcomm.protocols.ss7.map.api.MAPProvider;
import org.restcomm.protocols.ss7.map.api.MAPStack;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.sccp.SccpProvider;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImpl;
import org.restcomm.protocols.ss7.sccpext.impl.SccpExtModuleImpl;
import org.restcomm.protocols.ss7.tcap.api.TCAPProvider;
import org.restcomm.protocols.ss7.tcap.api.TCAPStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A started jSS7 stack produced by {@link Ss7StackBuilder}. This is the neutral
 * hand-off point: a resource adaptor holds one of these and reads the provider
 * accessors it needs; it never touches the per-layer wiring, which lives in the
 * builder. Call {@link #stop()} to tear the stack down in reverse order.
 */
public final class Ss7Stack {

    private static final Logger LOG = LogManager.getLogger(Ss7Stack.class);

    private final Management sctp;
    private final M3UAManagementImpl m3ua;
    private final SccpStackImpl sccp;
    private final SccpExtModuleImpl sccpExt;
    private final TCAPStack tcap;
    private final MAPStack map;    // nullable
    private final CAPStack cap;    // nullable

    Ss7Stack(Management sctp, M3UAManagementImpl m3ua, SccpStackImpl sccp,
             SccpExtModuleImpl sccpExt, TCAPStack tcap, MAPStack map, CAPStack cap) {
        this.sctp = sctp;
        this.m3ua = m3ua;
        this.sccp = sccp;
        this.sccpExt = sccpExt;
        this.tcap = tcap;
        this.map = map;
        this.cap = cap;
    }

    // ── provider accessors ────────────────────────────────────
    public TCAPProvider tcapProvider() { return tcap.getProvider(); }
    public SccpProvider sccpProvider() { return sccp.getSccpProvider(); }
    public MAPProvider mapProvider()   { return map == null ? null : map.getMAPProvider(); }
    public CAPProvider capProvider()   { return cap == null ? null : cap.getCAPProvider(); }

    public Management sctpManagement()      { return sctp; }
    public M3UAManagementImpl m3uaManagement() { return m3ua; }
    public SccpStackImpl sccpStack()        { return sccp; }
    public TCAPStack tcapStack()            { return tcap; }

    // ── lifecycle ─────────────────────────────────────────────
    public synchronized void stop() {
        quietly(() -> { if (cap != null) cap.stop(); });
        quietly(() -> { if (map != null) map.stop(); });
        quietly(() -> { if (tcap != null) tcap.stop(); });
        quietly(() -> { if (sccp != null) sccp.stop(); });
        quietly(() -> { if (m3ua != null) m3ua.stop(); });
        quietly(() -> { if (sctp != null) sctp.stop(); });
        LOG.info("[ss7-config] jSS7 stack STOPPED");
    }

    private void quietly(ThrowingRunnable r) {
        try { r.run(); } catch (Exception e) {
            LOG.warn("[ss7-config] stack teardown step failed: {}", e.toString());
        }
    }

    @FunctionalInterface
    interface ThrowingRunnable { void run() throws Exception; }
}
