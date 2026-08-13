/*
 * jSS7 :: ss7-config
 */
package org.restcomm.protocols.ss7.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Reads the neutral {@link Ss7Config} JSON, fills defaults, and validates it.
 *
 * <ol>
 *   <li><b>parse</b> — Jackson record binding;</li>
 *   <li><b>normalise</b> — empty lists for missing collections and documented
 *       scalar/enum defaults (so {@link Ss7StackBuilder} never sees a null);</li>
 *   <li><b>validate</b> — link/AS/route/service references resolve, host:port
 *       strings parse, enum vocabularies are known, service SSNs are unique.</li>
 * </ol>
 */
public final class Ss7ConfigLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            // let operators annotate config files: // line and # line comments
            .configure(JsonParser.Feature.ALLOW_COMMENTS, true)
            .configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);

    private static final Set<String> CHANNELS      = Set.of("SCTP", "TCP");
    private static final Set<String> LINK_TYPES    = Set.of("CLIENT", "SERVER");
    private static final Set<String> MODES         = Set.of("OVERRIDE", "LOADSHARE", "BROADCAST");
    private static final Set<String> FUNCTIONALITY = Set.of("AS", "SGW", "IPSP");
    private static final Set<String> IPSP          = Set.of("CLIENT", "SERVER");
    private static final Set<String> EXCHANGE      = Set.of("SE", "DE");
    private static final Set<String> ORIGINATIONS  = Set.of("LOCAL", "REMOTE", "ALL");
    private static final Set<String> NIS           = Set.of("INTERNATIONAL", "SPARE", "NATIONAL", "RESERVED");
    private static final Set<String> ENCODINGS     = Set.of("ODD", "EVEN");
    private static final Set<String> PROTOCOLS     = Set.of("MAP", "CAP", "TCAP", "INAP");
    private static final Set<String> SCTP_BACKENDS = Set.of("FSTACK_DPDK", "NETTY_KERNEL");
    private static final Set<String> SCTP_MODES    = Set.of("IN_PROCESS", "SIDECAR");
    private static final Set<String> SCTP_PLANES   = Set.of("LOOPBACK", "DPDK");

    private Ss7ConfigLoader() { }

    // ── entry points ──────────────────────────────────────────
    public static Ss7Config load(Path file) {
        try (InputStream in = Files.newInputStream(file)) {
            return load(in);
        } catch (IOException e) {
            throw new Ss7ConfigException("cannot read SS7 config file: " + file, e);
        }
    }

    public static Ss7Config load(InputStream in) {
        return finish(read(in));
    }

    public static Ss7Config parse(String json) {
        return finish(readString(json));
    }

    private static Ss7Config read(InputStream in) {
        try {
            return MAPPER.readValue(in, Ss7Config.class);
        } catch (IOException e) {
            throw new Ss7ConfigException("malformed SS7 config JSON: " + e.getMessage(), e);
        }
    }

    private static Ss7Config readString(String json) {
        try {
            return MAPPER.readValue(json, Ss7Config.class);
        } catch (IOException e) {
            throw new Ss7ConfigException("malformed SS7 config JSON: " + e.getMessage(), e);
        }
    }

    private static Ss7Config finish(Ss7Config raw) {
        return validate(normalize(raw));
    }

    // ── computed helpers (used by the TCAP layer) ─────────────
    /** Distinct SSNs across all services, in declaration order. */
    public static List<Integer> allSsns(Ss7Config cfg) {
        LinkedHashSet<Integer> ssns = new LinkedHashSet<>();
        for (Ss7Config.Service s : cfg.services()) ssns.add(s.ssn());
        return new ArrayList<>(ssns);
    }

    /** Primary SSN = first declared service SSN (0 if none). */
    public static int primarySsn(Ss7Config cfg) {
        List<Integer> all = allSsns(cfg);
        return all.isEmpty() ? 0 : all.get(0);
    }

    /** Extra SSNs = every declared SSN except the primary. */
    public static List<Integer> extraSsns(Ss7Config cfg) {
        List<Integer> all = allSsns(cfg);
        return all.isEmpty() ? List.of() : new ArrayList<>(all.subList(1, all.size()));
    }

    // ── normalisation ─────────────────────────────────────────
    private static Ss7Config normalize(Ss7Config c) {
        if (c == null) throw new Ss7ConfigException("empty SS7 config document");

        String stackName = orDefault(c.stackName(), "ss7");

        Ss7Config.Protocols p = c.protocols();
        Ss7Config.Protocols protocols = new Ss7Config.Protocols(
                p == null || p.map() == null || p.map(),
                p != null && p.cap() != null && p.cap(),
                p != null && p.inap() != null && p.inap());

        return new Ss7Config(stackName, protocols,
                normSctp(c.sctp()), normM3ua(c.m3ua()), normSccp(c.sccp()),
                normTcap(c.tcap()), nz(c.services()));
    }

    private static Ss7Config.Sctp normSctp(Ss7Config.Sctp s) {
        if (s == null) return new Ss7Config.Sctp(1000, 8, 256, 256, List.of());
        List<Ss7Config.Link> links = new ArrayList<>();
        for (Ss7Config.Link l : nz(s.links())) {
            links.add(new Ss7Config.Link(l.name(), l.local(), l.peer(),
                    nz(l.localSecondary()), orDefault(l.channel(), "sctp"),
                    orDefault(l.type(), "client"), l.server(), l.aspId(), l.heartbeat()));
        }
        return new Ss7Config.Sctp(
                s.connectDelay() > 0 ? s.connectDelay() : 1000,
                s.workerThreads() > 0 ? s.workerThreads() : 8,
                s.maxInStreams() > 0 ? s.maxInStreams() : 256,
                s.maxOutStreams() > 0 ? s.maxOutStreams() : 256,
                links,
                blankToNull(s.backend()),
                blankToNull(s.mode()),
                blankToNull(s.dataplane()),
                blankToNull(s.library()),
                s.inProcess());
    }

    private static Ss7Config.M3ua normM3ua(Ss7Config.M3ua m) {
        if (m == null) return new Ss7Config.M3ua(10000, 1, List.of(), List.of());
        List<Ss7Config.As> as = new ArrayList<>();
        for (Ss7Config.As a : nz(m.as())) {
            as.add(new Ss7Config.As(a.name(), orDefault(a.mode(), "loadshare"),
                    orDefault(a.functionality(), "as"), a.ipsp(),
                    orDefault(a.exchangeType(), "se"),
                    a.routingContext() == null ? 0L : a.routingContext(),
                    a.networkAppearance(),
                    a.minAspActiveForLb() > 0 ? a.minAspActiveForLb() : 1,
                    nz(a.links())));
        }
        List<Ss7Config.Route> routes = new ArrayList<>();
        for (Ss7Config.Route r : nz(m.routes())) {
            Ss7Config.Dest d = r.to();
            Ss7Config.Dest nd = d == null ? null
                    : new Ss7Config.Dest(d.dpc(), d.opc(), d.si() == null ? -1 : d.si());
            routes.add(new Ss7Config.Route(nd, r.via()));
        }
        return new Ss7Config.M3ua(
                m.heartbeatTime() > 0 ? m.heartbeatTime() : 10000,
                m.deliveryThreads() > 0 ? m.deliveryThreads() : 1,
                as, routes);
    }

    private static Ss7Config.Sccp normSccp(Ss7Config.Sccp s) {
        if (s == null) return new Ss7Config.Sccp(List.of(), List.of());
        List<Ss7Config.LocalPoint> points = new ArrayList<>();
        for (Ss7Config.LocalPoint lp : nz(s.localPoints())) {
            points.add(new Ss7Config.LocalPoint(lp.pc(),
                    orDefault(lp.networkIndicator(), "national"), lp.networkId(),
                    lp.reachablePointCodes() == null ? List.of() : lp.reachablePointCodes()));
        }
        List<Ss7Config.Rule> routing = new ArrayList<>();
        for (Ss7Config.Rule r : nz(s.routing())) {
            routing.add(new Ss7Config.Rule(orDefault(r.from(), "all"), r.networkId(),
                    orDefault(r.mask(), "K"),
                    normAddr(r.match()), normAddr(r.to()), normAddr(r.backup())));
        }
        return new Ss7Config.Sccp(points, routing);
    }

    private static Ss7Config.Addr normAddr(Ss7Config.Addr a) {
        if (a == null) return null;
        return new Ss7Config.Addr(a.pc(), a.ssn(),
                orDefault(a.gt(), "*"), orDefault(a.gtType(), "GT0100"),
                orDefault(a.encoding(), "even"), orDefault(a.plan(), "isdn"),
                orDefault(a.nature(), "international"));
    }

    private static Ss7Config.Tcap normTcap(Ss7Config.Tcap t) {
        if (t == null) return new Ss7Config.Tcap(60000, 30000, 5000, 1, Integer.MAX_VALUE, false, false);
        return new Ss7Config.Tcap(
                t.dialogIdleTimeout() > 0 ? t.dialogIdleTimeout() : 60000,
                t.invokeTimeout() > 0 ? t.invokeTimeout() : 30000,
                t.maxDialogs() > 0 ? t.maxDialogs() : 5000,
                t.dialogIdRangeStart() > 0 ? t.dialogIdRangeStart() : 1,
                t.dialogIdRangeEnd() > 0 ? t.dialogIdRangeEnd() : Integer.MAX_VALUE,
                t.doNotSendProtocolVersion(), t.statisticsEnabled());
    }

    // ── validation ────────────────────────────────────────────
    private static Ss7Config validate(Ss7Config c) {
        requireKnown("sctp.backend", c.sctp().backend(), SCTP_BACKENDS, "FSTACK_DPDK|NETTY_KERNEL");
        requireKnown("sctp.mode", c.sctp().mode(), SCTP_MODES, "IN_PROCESS|SIDECAR");
        requireKnown("sctp.dataplane", c.sctp().dataplane(), SCTP_PLANES, "LOOPBACK|DPDK");

        // SCTP links
        Set<String> linkNames = new HashSet<>();
        for (Ss7Config.Link l : c.sctp().links()) {
            require(l.name() != null && !l.name().isBlank(), "an sctp link has no name");
            require(linkNames.add(l.name()), "duplicate sctp link name '" + l.name() + "'");
            require(CHANNELS.contains(up(l.channel())), "link '" + l.name()
                    + "' invalid channel '" + l.channel() + "' (sctp|tcp)");
            require(LINK_TYPES.contains(up(l.type())), "link '" + l.name()
                    + "' invalid type '" + l.type() + "' (client|server)");
            requireHostPort(l.local(), "link '" + l.name() + "' local");
            if ("CLIENT".equals(up(l.type()))) {
                requireHostPort(l.peer(), "link '" + l.name() + "' peer");
            }
        }

        // M3UA AS + routes
        Set<String> asNames = new HashSet<>();
        for (Ss7Config.As a : c.m3ua().as()) {
            require(a.name() != null && !a.name().isBlank(), "an m3ua AS has no name");
            require(asNames.add(a.name()), "duplicate m3ua AS name '" + a.name() + "'");
            require(MODES.contains(up(a.mode())), "AS '" + a.name()
                    + "' invalid mode '" + a.mode() + "' (override|loadshare|broadcast)");
            require(FUNCTIONALITY.contains(up(a.functionality())), "AS '" + a.name()
                    + "' invalid functionality '" + a.functionality() + "' (as|sgw|ipsp)");
            require(EXCHANGE.contains(up(a.exchangeType())), "AS '" + a.name()
                    + "' invalid exchangeType '" + a.exchangeType() + "' (se|de)");
            if (a.ipsp() != null) {
                require(IPSP.contains(up(a.ipsp())), "AS '" + a.name()
                        + "' invalid ipsp '" + a.ipsp() + "' (client|server)");
            }
            require(!a.links().isEmpty(), "AS '" + a.name() + "' has no links");
            for (String ln : a.links()) {
                require(linkNames.contains(ln), "AS '" + a.name()
                        + "' references unknown link '" + ln + "'");
            }
        }
        for (Ss7Config.Route r : c.m3ua().routes()) {
            require(r.to() != null, "an m3ua route has no destination");
            require(asNames.contains(r.via()), "m3ua route (dpc=" + (r.to() == null ? "?" : r.to().dpc())
                    + ") references unknown AS '" + r.via() + "'");
        }

        // SCCP local points
        require(!c.sccp().localPoints().isEmpty(), "sccp needs at least one localPoint");
        Set<Integer> localNetworkIds = new HashSet<>();
        for (Ss7Config.LocalPoint lp : c.sccp().localPoints()) {
            require(lp.pc() > 0, "a sccp localPoint has no point code (pc)");
            require(NIS.contains(up(lp.networkIndicator())), "localPoint pc=" + lp.pc()
                    + " invalid networkIndicator '" + lp.networkIndicator()
                    + "' (international|spare|national|reserved)");
            require(localNetworkIds.add(lp.networkId()), "two sccp localPoints share networkId "
                    + lp.networkId() + " (each local point needs a distinct networkId)");
        }
        // SCCP routing
        int i = 0;
        for (Ss7Config.Rule r : c.sccp().routing()) {
            i++;
            require(ORIGINATIONS.contains(up(r.from())), "routing rule #" + i
                    + " invalid from '" + r.from() + "' (local|remote|all)");
            require(r.match() != null, "routing rule #" + i + " has no match");
            require(r.to() != null, "routing rule #" + i + " has no destination (to)");
            require(localNetworkIds.contains(r.networkId()), "routing rule #" + i
                    + " networkId " + r.networkId() + " has no matching localPoint");
            validateAddr(r.match(), "routing rule #" + i + " match");
            validateAddr(r.to(), "routing rule #" + i + " to");
            if (r.backup() != null) validateAddr(r.backup(), "routing rule #" + i + " backup");
        }

        // Services
        require(!c.services().isEmpty(), "at least one service (SSN binding) is required");
        Set<Integer> ssns = new HashSet<>();
        for (Ss7Config.Service s : c.services()) {
            require(s.name() != null && !s.name().isBlank(), "a service has no name");
            require(PROTOCOLS.contains(up(s.protocol())), "service '" + s.name()
                    + "' invalid protocol '" + s.protocol() + "' (map|cap|tcap|inap)");
            require(ssns.add(s.ssn()), "duplicate service SSN " + s.ssn()
                    + " (service '" + s.name() + "')");
        }
        return c;
    }

    private static void validateAddr(Ss7Config.Addr a, String where) {
        require(ENCODINGS.contains(up(a.encoding())), where
                + " invalid encoding '" + a.encoding() + "' (odd|even)");
        require(a.pc() != null || a.ssn() != null || (a.gt() != null && !a.gt().isBlank()),
                where + " is empty (needs at least pc, ssn, or gt)");
    }

    // ── small helpers ─────────────────────────────────────────
    private static <T> List<T> nz(List<T> in) { return in == null ? List.of() : in; }

    private static String orDefault(String v, String def) {
        return (v == null || v.isBlank()) ? def : v;
    }

    private static String blankToNull(String v) {
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private static void requireKnown(String field, String raw, Set<String> allowed, String vocab) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        String key = up(raw).replace('-', '_');
        require(allowed.contains(key), field + " invalid '" + raw + "' (" + vocab + ")");
    }

    private static String up(String v) { return v == null ? "" : v.toUpperCase(); }

    private static void require(boolean cond, String msg) {
        if (!cond) throw new Ss7ConfigException(msg);
    }

    private static void requireHostPort(String v, String where) {
        require(v != null && !v.isBlank(), where + " address is required (ip:port)");
        int c = v.lastIndexOf(':');
        require(c > 0 && c < v.length() - 1, where + " '" + v + "' must be ip:port");
        try {
            Integer.parseInt(v.substring(c + 1).trim());
        } catch (NumberFormatException e) {
            throw new Ss7ConfigException(where + " '" + v + "' has a non-numeric port");
        }
    }
}
