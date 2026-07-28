/*
 * jSS7 :: ss7-config
 */
package org.restcomm.protocols.ss7.config;

import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.api.Management;
import org.restcomm.protocols.ss7.cap.CAPStackImpl;
import org.restcomm.protocols.ss7.cap.api.CAPStack;
import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.IPSPType;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.NetworkAppearance;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.restcomm.protocols.ss7.map.MAPStackImpl;
import org.restcomm.protocols.ss7.map.api.MAPStack;
import org.restcomm.protocols.ss7.sccp.LoadSharingAlgorithm;
import org.restcomm.protocols.ss7.sccp.OriginationType;
import org.restcomm.protocols.ss7.sccp.Router;
import org.restcomm.protocols.ss7.sccp.RuleType;
import org.restcomm.protocols.ss7.sccp.SccpResource;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDOddEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.sccpext.impl.SccpExtModuleImpl;
import org.restcomm.protocols.ss7.sccpext.router.RouterExt;
import org.restcomm.protocols.ss7.ss7ext.Ss7ExtInterface;
import org.restcomm.protocols.ss7.ss7ext.Ss7ExtInterfaceImpl;
import org.restcomm.protocols.ss7.tcap.TCAPStackImpl;
import org.restcomm.protocols.ss7.tcap.api.TCAPStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Compiles a neutral {@link Ss7Config} into a started jSS7 stack
 * ({@code SCTP -> M3UA -> SCCP -> TCAP -> MAP/CAP}) and returns an
 * {@link Ss7Stack} that exposes only the providers.
 *
 * <p>This is where all the jSS7-specific knowledge lives — and where the
 * config's "abstract" boilerplate is <b>derived</b> rather than demanded from
 * the operator: one ASP per SCTP link, remote point codes and remote SSNs from
 * the reachable PCs × service SSNs, MTP3 destinations from the reachable PCs,
 * the SCCP address indicator from which of pc/ssn/gt are present, and rule /
 * routing-address ids by declaration order.</p>
 */
public final class Ss7StackBuilder {

    private static final Logger LOG = LogManager.getLogger(Ss7StackBuilder.class);

    /** Default SCTP provider (mobicents Netty-based impl), resolved reflectively. */
    private static final String SCTP_IMPL =
            System.getProperty("ss7.sctp.impl",
                    "org.mobicents.protocols.sctp.netty.NettySctpManagementImpl");

    private final Ss7Config cfg;

    private Ss7StackBuilder(Ss7Config cfg) { this.cfg = cfg; }

    // ── entry points ──────────────────────────────────────────
    public static Ss7Stack build(Ss7Config cfg) {
        try {
            return new Ss7StackBuilder(cfg).doBuild();
        } catch (Ss7ConfigException e) {
            throw e;
        } catch (Exception e) {
            throw new Ss7ConfigException("failed to build jSS7 stack: " + e.getMessage(), e);
        }
    }

    public static Ss7Stack build(Path configFile) { return build(Ss7ConfigLoader.load(configFile)); }

    public static Ss7Stack buildFromJson(String json) { return build(Ss7ConfigLoader.parse(json)); }

    // ── the compile ───────────────────────────────────────────
    private Ss7Stack doBuild() throws Exception {
        LOG.info("[ss7-config] compiling jSS7 stack '{}'", cfg.stackName());
        Management sctp = null;
        try {
            sctp = initSctp();
            M3UAManagementImpl m3ua = initM3ua(sctp);
            // Bind/listen only AFTER createAspFactory: M3UA refuses associations that are
            // already started, and Netty startServer() auto-starts server associations.
            startSctpAndM3ua(sctp, m3ua);
            SccpExtModuleImpl sccpExt = new SccpExtModuleImpl();
            SccpStackImpl sccp = initSccp(m3ua, sccpExt);
            TCAPStack tcap = initTcap(sccp);
            MAPStack map = cfg.protocols().map() ? initMap(tcap) : null;
            CAPStack cap = cfg.protocols().cap() ? initCap(tcap) : null;
            LOG.info("[ss7-config] jSS7 stack '{}' STARTED (map={} cap={})",
                    cfg.stackName(), map != null, cap != null);
            return new Ss7Stack(sctp, m3ua, sccp, sccpExt, tcap, map, cap);
        } catch (Exception ex) {
            // Avoid orphan SCTP listeners (e.g. :2905) when M3UA/SCCP fails mid-build.
            if (sctp != null) {
                try {
                    sctp.stop();
                } catch (Exception stopEx) {
                    LOG.warn("[ss7-config] SCTP cleanup after build failure: {}", stopEx.getMessage());
                }
            }
            throw ex;
        }
    }

    // ── SCTP ──────────────────────────────────────────────────
    private Management initSctp() throws Exception {
        Ss7Config.Sctp s = cfg.sctp();
        Management sctp = (Management) Class.forName(SCTP_IMPL)
                .getConstructor(String.class).newInstance(cfg.stackName() + "-sctp");
        sctp.setWorkerThreads(s.workerThreads());
        sctp.setOptionSctpInitMaxstreams_MaxInStreams(s.maxInStreams());
        sctp.setOptionSctpInitMaxstreams_MaxOutStreams(s.maxOutStreams());
        sctp.start();
        sctp.setConnectDelay(s.connectDelay());

        for (Ss7Config.Link link : s.links()) {
            IpChannelType channel = "TCP".equalsIgnoreCase(link.channel())
                    ? IpChannelType.TCP : IpChannelType.SCTP;
            HostPort local = HostPort.parse(link.local());
            String[] extra = link.localSecondary().isEmpty()
                    ? null : link.localSecondary().toArray(new String[0]);
            boolean isServer = "SERVER".equalsIgnoreCase(link.type());
            String serverName = isServer ? link.name() + "-srv" : null;

            // Idempotent build: SCTP persists its servers/associations to disk
            // (<stackName>-sctp_sctp.xml) and reloads them on start(), so re-runs would fail
            // with "Server name=... already exist". Drop any reloaded copies before re-adding.
            dropSctpResource(sctp, serverName, link.name());

            if (isServer) {
                sctp.addServer(serverName, local.host, local.port, channel, extra);
                sctp.addServerAssociation(HostPort.parse(link.peer()).host,
                        HostPort.parse(link.peer()).port, serverName,
                        link.name(), channel);
            } else {
                HostPort peer = HostPort.parse(link.peer());
                sctp.addAssociation(local.host, local.port, peer.host, peer.port,
                        link.name(), channel, extra);
            }
            LOG.info("[ss7-config] SCTP link {} {} {}:{} ({}) configured (start deferred)",
                    link.name(), isServer ? "server" : "client",
                    local.host, local.port, channel);
        }
        return sctp;
    }

    /**
     * Remove an association and (optionally) a server previously restored from persisted SCTP
     * state, so a fresh build does not collide with them. Association is dropped first because a
     * server cannot be removed while it still owns one. Uses only methods stable across sctp-api
     * versions (avoids the removeAllResourses/removeAllResources spelling divergence).
     */
    private static void dropSctpResource(Management sctp, String serverName, String assocName)
            throws Exception {
        if (assocName != null && sctp.getAssociations().containsKey(assocName)) {
            try { sctp.stopAssociation(assocName); } catch (Exception ignore) { /* already stopped */ }
            sctp.removeAssociation(assocName);
        }
        if (serverName != null) {
            boolean exists = false;
            // Plain loop, not stream(): some jSS7 collection types on the classpath trigger
            // IncompatibleClassChangeError (conflicting spliterator default methods) via stream().
            for (org.mobicents.protocols.api.Server sv : sctp.getServers()) {
                if (serverName.equals(sv.getName())) { exists = true; break; }
            }
            if (exists) {
                try { sctp.stopServer(serverName); } catch (Exception ignore) { /* already stopped */ }
                sctp.removeServer(serverName);
            }
        }
    }

    // ── M3UA ──────────────────────────────────────────────────
    private M3UAManagementImpl initM3ua(Management sctp) throws Exception {
        Ss7Config.M3ua m = cfg.m3ua();
        M3UAManagementImpl m3ua = new M3UAManagementImpl(
                cfg.stackName() + "-m3ua", null, new Ss7ExtInterfaceImpl());
        m3ua.setTransportManagement(sctp);
        m3ua.setDeliveryMessageThreadCount(m.deliveryThreads());
        m3ua.start();
        m3ua.removeAllResources();

        ParameterFactoryImpl factory = new ParameterFactoryImpl();
        java.util.Map<String, Ss7Config.Link> linksByName = new java.util.HashMap<>();
        for (Ss7Config.Link l : cfg.sctp().links()) linksByName.put(l.name(), l);
        java.util.Set<String> aspCreated = new java.util.HashSet<>();
        int aspIdSeq = 1;

        for (Ss7Config.As as : m.as()) {
            RoutingContext rc = factory.createRoutingContext(new long[] { as.routingContext() });
            TrafficModeType tmt = factory.createTrafficModeType(trafficMode(as.mode()));
            NetworkAppearance na = as.networkAppearance() == null ? null
                    : factory.createNetworkAppearance(as.networkAppearance());

            m3ua.createAs(as.name(), functionality(as.functionality()), exchange(as.exchangeType()),
                    ipsp(as.ipsp()), rc, tmt, as.minAspActiveForLb(), na);

            for (String linkName : as.links()) {
                String aspName = linkName + "-ASP";
                if (aspCreated.add(aspName)) {
                    Ss7Config.Link link = linksByName.get(linkName);
                    long aspId = link != null && link.aspId() != null ? link.aspId() : aspIdSeq++;
                    boolean hb = link != null && link.heartbeat() != null && link.heartbeat();
                    m3ua.createAspFactory(aspName, linkName, aspId, hb);
                }
                m3ua.assignAspToAs(as.name(), aspName);
            }
        }
        for (Ss7Config.Route r : m.routes()) {
            m3ua.addRoute(r.to().dpc(), r.to().opc(), r.to().si(), r.via());
        }
        // Transport start (startServer / startAsp) happens in startSctpAndM3ua() after
        // createAspFactory — M3UA requires associations to be stopped at factory create time.
        return m3ua;
    }

    /**
     * Bring SCTP servers/associations and M3UA ASPs up. Order matters for Netty SCTP:
     * {@code startServer} auto-starts server associations, so ASP factories must already exist.
     */
    private void startSctpAndM3ua(Management sctp, M3UAManagementImpl m3ua) throws Exception {
        for (Ss7Config.Link link : cfg.sctp().links()) {
            if (!"SERVER".equalsIgnoreCase(link.type())) {
                continue;
            }
            String serverName = link.name() + "-srv";
            sctp.startServer(serverName);
            HostPort local = HostPort.parse(link.local());
            LOG.info("[ss7-config] SCTP server {} listening {}:{}",
                    serverName, local.host, local.port);
        }
        java.util.Set<String> aspNames = new java.util.LinkedHashSet<>();
        for (Ss7Config.As as : cfg.m3ua().as()) {
            for (String linkName : as.links()) {
                aspNames.add(linkName + "-ASP");
            }
        }
        for (String aspName : aspNames) {
            try {
                m3ua.startAsp(aspName);
                LOG.info("[ss7-config] M3UA ASP {} started", aspName);
            } catch (Exception ex) {
                // Server ASP may stay COMM_DOWN until peer connects — still treat as wired.
                LOG.warn("[ss7-config] M3UA ASP {} start deferred/failed (peer may be down): {}",
                        aspName, ex.getMessage());
            }
        }
    }

    // ── SCCP (+ ext) ──────────────────────────────────────────
    private SccpStackImpl initSccp(M3UAManagementImpl m3ua, SccpExtModuleImpl sccpExt) throws Exception {
        Ss7Config.Sccp s = cfg.sccp();
        Ss7ExtInterface ss7Ext = new Ss7ExtInterfaceImpl();
        ss7Ext.setSs7ExtSccpInterface(sccpExt);
        SccpStackImpl sccp = new SccpStackImpl(cfg.stackName() + "-sccp", ss7Ext);
        sccp.setMtp3UserPart(1, m3ua);
        sccp.start();
        sccp.removeAllResources();

        Router router = sccp.getRouter();
        RouterExt routerExt = sccpExt.getRouterExt();
        SccpResource resource = sccp.getSccpResource();

        // one MTP3 SAP per local point code; a destination per reachable PC.
        // remote SPCs/SSNs are global to the resource, so dedupe across points.
        List<Integer> serviceSsns = Ss7ConfigLoader.allSsns(cfg);
        java.util.Set<Integer> spcSeen = new java.util.HashSet<>();
        int sapId = 1, spcId = 1, ssnId = 1;
        for (Ss7Config.LocalPoint lp : s.localPoints()) {
            router.addMtp3ServiceAccessPoint(sapId, 1, lp.pc(),
                    networkIndicator(lp.networkIndicator()), lp.networkId(), null);
            int destId = 1;
            for (int pc : lp.reachablePointCodes()) {
                router.addMtp3Destination(sapId, destId++, pc, pc, 0, 255, 255);
                if (spcSeen.add(pc)) {
                    resource.addRemoteSpc(spcId++, pc, 0, 0);
                    for (int ssn : serviceSsns) {
                        resource.addRemoteSsn(ssnId++, pc, ssn, 0, false);
                    }
                }
            }
            sapId++;
        }

        // routing: each rule declares its own translation target inline
        int addrId = 1, ruleId = 1;
        for (Ss7Config.Rule rule : s.routing()) {
            SccpAddress primary = toSccpAddress(rule.to());
            int primaryId = addrId++;
            routerExt.addRoutingAddress(primaryId, primary);

            int secondaryId = -1;
            if (rule.backup() != null) {
                secondaryId = addrId++;
                routerExt.addRoutingAddress(secondaryId, toSccpAddress(rule.backup()));
            }

            SccpAddress pattern = toSccpAddress(rule.match());
            RuleType rt = secondaryId >= 0 ? RuleType.DOMINANT : RuleType.SOLITARY;
            String mask = rule.mask() != null ? rule.mask() : "K";
            routerExt.addRule(ruleId++, rt, LoadSharingAlgorithm.Bit0, origination(rule.from()),
                    pattern, mask, primaryId, secondaryId, null, rule.networkId(), null);
        }
        return sccp;
    }

    // ── TCAP (multi-SSN) ──────────────────────────────────────
    private TCAPStack initTcap(SccpStackImpl sccp) throws Exception {
        Ss7Config.Tcap t = cfg.tcap();
        int primary = Ss7ConfigLoader.primarySsn(cfg);
        TCAPStack tcap = new TCAPStackImpl(cfg.stackName() + "-tcap", sccp.getSccpProvider(), primary);
        tcap.setExtraSsns(new ArrayList<>(Ss7ConfigLoader.extraSsns(cfg)));
        tcap.start();
        tcap.setInvokeTimeout(t.invokeTimeout());
        tcap.setDialogIdleTimeout(t.dialogIdleTimeout());
        tcap.setMaxDialogs(t.maxDialogs());
        return tcap;
    }

    private MAPStack initMap(TCAPStack tcap) throws Exception {
        MAPStack map = new MAPStackImpl(cfg.stackName() + "-map", tcap.getProvider());
        map.start();
        return map;
    }

    private CAPStack initCap(TCAPStack tcap) throws Exception {
        CAPStack cap = new CAPStackImpl(cfg.stackName() + "-cap", tcap.getProvider());
        cap.start();
        return cap;
    }

    // ── address building (derives the SCCP address indicator) ──
    private SccpAddress toSccpAddress(Ss7Config.Addr a) {
        int pc = a.pc() == null ? 0 : a.pc();
        int ssn = a.ssn() == null ? 0 : a.ssn();
        boolean hasGt = a.gt() != null && !a.gt().isBlank();
        if (hasGt) {
            GlobalTitle gt = globalTitle(a);
            return new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, pc, ssn);
        }
        return new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, null, pc, ssn);
    }

    private GlobalTitle globalTitle(Ss7Config.Addr a) {
        if (!"GT0100".equalsIgnoreCase(a.gtType())) {
            throw new Ss7ConfigException("unsupported gtType '" + a.gtType()
                    + "' (only GT0100 is supported)");
        }
        EncodingScheme es = "ODD".equalsIgnoreCase(a.encoding())
                ? new BCDOddEncodingScheme() : new BCDEvenEncodingScheme();
        org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl fact =
                new org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl();
        return fact.createGlobalTitle(a.gt(), 0, numberingPlan(a.plan()), es, natureOfAddress(a.nature()));
    }

    // ── enum mapping (named strings -> jSS7 enums) ────────────
    private static int trafficMode(String v) {
        return switch (v.toUpperCase()) {
            case "OVERRIDE"  -> TrafficModeType.Override;
            case "BROADCAST" -> TrafficModeType.Broadcast;
            default          -> TrafficModeType.Loadshare;
        };
    }

    private static Functionality functionality(String v) {
        return switch (v.toUpperCase()) {
            case "SGW"  -> Functionality.SGW;
            case "IPSP" -> Functionality.IPSP;
            default     -> Functionality.AS;
        };
    }

    private static IPSPType ipsp(String v) {
        if (v == null) return null;
        return "SERVER".equalsIgnoreCase(v) ? IPSPType.SERVER : IPSPType.CLIENT;
    }

    private static ExchangeType exchange(String v) {
        return "DE".equalsIgnoreCase(v) ? ExchangeType.DE : ExchangeType.SE;
    }

    private static OriginationType origination(String v) {
        return switch (v.toUpperCase()) {
            case "LOCAL"  -> OriginationType.LOCAL;
            case "REMOTE" -> OriginationType.REMOTE;
            default       -> OriginationType.ALL;
        };
    }

    /** ITU network indicator: international=0, spare=1, national=2, reserved=3. */
    private static int networkIndicator(String v) {
        return switch (v.toUpperCase()) {
            case "INTERNATIONAL" -> 0;
            case "SPARE"         -> 1;
            case "RESERVED"      -> 3;
            default              -> 2; // national
        };
    }

    private static NumberingPlan numberingPlan(String v) {
        return switch (v.toLowerCase()) {
            case "generic"   -> NumberingPlan.GENERIC;
            case "data"      -> NumberingPlan.DATA;
            case "telex"     -> NumberingPlan.TELEX;
            case "maritime"  -> NumberingPlan.MERITIME_MOBILE;
            case "land"      -> NumberingPlan.LAND_MOBILE;
            default          -> NumberingPlan.ISDN_TELEPHONY;
        };
    }

    private static NatureOfAddress natureOfAddress(String v) {
        return switch (v.toLowerCase()) {
            case "subscriber" -> NatureOfAddress.SUBSCRIBER;
            case "national"   -> NatureOfAddress.NATIONAL;
            case "unknown"    -> NatureOfAddress.UNKNOWN;
            default           -> NatureOfAddress.INTERNATIONAL;
        };
    }

    // ── "ip:port" ─────────────────────────────────────────────
    private record HostPort(String host, int port) {
        static HostPort parse(String v) {
            int c = v.lastIndexOf(':');
            return new HostPort(v.substring(0, c).trim(), Integer.parseInt(v.substring(c + 1).trim()));
        }
    }
}
