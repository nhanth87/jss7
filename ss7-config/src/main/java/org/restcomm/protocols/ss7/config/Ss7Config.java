/*
 * jSS7 :: ss7-config
 * Neutral single-file SS7 stack configuration model.
 */
package org.restcomm.protocols.ss7.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * The neutral, human-first SS7 stack configuration — one JSON document that
 * {@link Ss7StackBuilder} "compiles" into a fully wired jSS7 stack
 * ({@code SCTP -> M3UA -> SCCP -> TCAP -> MAP/CAP}).
 *
 * <p>Design goals, in priority order:</p>
 * <ol>
 *   <li><b>Readable</b> — no numeric cross-reference ids, no protocol "magic
 *       numbers" (address indicator, encoding scheme, nature-of-address are
 *       derived or named), host:port written the way an operator reads it.</li>
 *   <li><b>Terse</b> — the mechanical jSS7 boilerplate (one ASP per SCTP link,
 *       remote point codes, remote SSNs, MTP3 destinations, rule/address ids)
 *       is <i>derived</i> by the compiler, not typed by hand.</li>
 *   <li><b>Neutral</b> — this model carries only {@code String}/{@code int};
 *       it has zero jSS7 API types, so it is a stable serialization contract.
 *       All translation to concrete jSS7 enums/objects lives in the compiler.</li>
 * </ol>
 *
 * <p>Records are bound by Jackson through their canonical constructor. Missing
 * collections and scalar defaults are filled by {@link Ss7ConfigLoader}, so the
 * compiler never sees a {@code null} list.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Ss7Config(
        @JsonProperty("stackName") String stackName,
        @JsonProperty("protocols") Protocols protocols,
        @JsonProperty("sctp")      Sctp sctp,
        @JsonProperty("m3ua")      M3ua m3ua,
        @JsonProperty("sccp")      Sccp sccp,
        @JsonProperty("tcap")      Tcap tcap,
        @JsonProperty("services")  List<Service> services
) {

    /** Which application-protocol providers to start over TCAP. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Protocols(
            @JsonProperty("map")  Boolean map,
            @JsonProperty("cap")  Boolean cap,
            @JsonProperty("inap") Boolean inap
    ) { }

    // ══════════════════════════════════════════════════════════
    //  SCTP transport — a flat list of links (associations)
    // ══════════════════════════════════════════════════════════
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sctp(
            @JsonProperty("connectDelay")  int connectDelay,
            @JsonProperty("workerThreads") int workerThreads,
            @JsonProperty("maxInStreams")  int maxInStreams,
            @JsonProperty("maxOutStreams") int maxOutStreams,
            @JsonProperty("links")         List<Link> links
    ) { }

    /**
     * One SCTP link. {@code local}/{@code peer} are written as {@code "ip:port"}.
     * {@code localSecondary} carries the extra local IPs for SCTP multi-homing.
     * A M3UA ASP factory is auto-derived per link (named {@code <name>-ASP}).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Link(
            @JsonProperty("name")           String name,
            @JsonProperty("local")          String local,    // "ip:port"
            @JsonProperty("peer")           String peer,     // "ip:port"
            @JsonProperty("localSecondary") List<String> localSecondary, // multi-homing
            @JsonProperty("channel")        String channel,  // sctp | tcp   (default sctp)
            @JsonProperty("type")           String type,     // client | server (default client)
            @JsonProperty("server")         String server,   // server name, for type=server
            @JsonProperty("aspId")          Integer aspId,   // ASP id for the auto-derived ASP (default: sequential)
            @JsonProperty("heartbeat")      Boolean heartbeat // ASP heartbeat (default false)
    ) { }

    // ══════════════════════════════════════════════════════════
    //  M3UA — application servers + routes (ASPs are auto-derived)
    // ══════════════════════════════════════════════════════════
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record M3ua(
            @JsonProperty("heartbeatTime")   int heartbeatTime,
            @JsonProperty("deliveryThreads") int deliveryThreads,
            @JsonProperty("as")     List<As> as,
            @JsonProperty("routes") List<Route> routes
    ) { }

    /**
     * An Application Server. {@code links} lists the SCTP link names it spans;
     * one ASP is generated per link and assigned to this AS.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record As(
            @JsonProperty("name")             String name,
            @JsonProperty("mode")             String mode,          // override | loadshare | broadcast
            @JsonProperty("functionality")    String functionality, // as | sgw | ipsp   (default as)
            @JsonProperty("ipsp")             String ipsp,          // client | server | null
            @JsonProperty("exchangeType")     String exchangeType,  // se | de   (default se)
            @JsonProperty("routingContext")   Long routingContext,  // single RC (legacy); ignored when routingContexts set
            @JsonProperty("routingContexts")  List<Long> routingContexts, // multi-RC bind; preferred when non-empty
            @JsonProperty("networkAppearance") Long networkAppearance,
            @JsonProperty("minAspActiveForLb") int minAspActiveForLb,
            @JsonProperty("links")            List<String> links    // FK -> Link.name
    ) { }

    /** An M3UA route: traffic to {@code to} is carried {@code via} an AS. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Route(
            @JsonProperty("to")  Dest to,
            @JsonProperty("via") String via   // FK -> As.name
    ) { }

    /** Route destination; {@code si} defaults to -1 (any service indicator). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Dest(
            @JsonProperty("dpc") int dpc,
            @JsonProperty("opc") int opc,
            @JsonProperty("si")  Integer si
    ) { }

    // ══════════════════════════════════════════════════════════
    //  SCCP — local identity + GT routing (ids/remotes auto-derived)
    // ══════════════════════════════════════════════════════════
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sccp(
            @JsonProperty("localPoints") List<LocalPoint> localPoints,
            @JsonProperty("routing")     List<Rule> routing
    ) { }

    /**
     * One local signalling point = one MTP3 SAP. Several may be declared to run
     * multiple local point codes on one stack; {@code networkId} segregates
     * their routing tables (a rule with the same {@code networkId} belongs to
     * this local point). {@code reachablePointCodes} are the adjacent/relayed
     * DPCs this point can reach — the compiler turns each into an MTP3
     * destination plus an auto remote SPC (and a remote SSN per service SSN).
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LocalPoint(
            @JsonProperty("pc")                  int pc,
            @JsonProperty("networkIndicator")    String networkIndicator, // international|spare|national|reserved (default national)
            @JsonProperty("networkId")           int networkId,
            @JsonProperty("reachablePointCodes") List<Integer> reachablePointCodes
    ) { }

    /**
     * A GT-translation rule, read top-to-bottom: an inbound/outbound message
     * whose called address matches {@code match} is translated to {@code to}
     * (with optional {@code backup} for failover). No ids: the compiler assigns
     * rule/address ids by declaration order.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Rule(
            @JsonProperty("from")      String from,      // local | remote | all (default all)
            @JsonProperty("networkId") int networkId,    // ties this rule to a LocalPoint of the same networkId
            @JsonProperty("mask")      String mask,       // GT digit carry mask: "K"=keep, "R"=replace, "/"-separated (default "K")
            @JsonProperty("match")     Addr match,
            @JsonProperty("to")        Addr to,
            @JsonProperty("backup")    Addr backup
    ) { }

    /**
     * An SCCP address. {@code pc}/{@code ssn} are nullable — an <b>absent</b>
     * field is simply not part of the address, which is exactly how you
     * "wildcard" it: omit {@code ssn} to match any SSN, omit {@code pc} to match
     * on GT only. The compiler derives the SCCP address indicator from which of
     * pc/ssn/gt are present (pc&gt;0, ssn&gt;0, gt!=null), so it is never typed.
     *
     * <p>{@code gt} is the Global Title digit string. In a {@code match} pattern
     * it accepts jSS7 wildcards: {@code *} = any remaining digits, {@code ?} =
     * one digit, {@code /} = digit-group separator, {@code -} = ignore. Default
     * {@code "*"} matches any GT. GT format defaults to international/ISDN/even
     * and is only spelled out when it must differ.</p>
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Addr(
            @JsonProperty("pc")       Integer pc,
            @JsonProperty("ssn")      Integer ssn,
            @JsonProperty("gt")       String gt,        // digits or "*"   (default "*")
            @JsonProperty("gtType")   String gtType,    // e.g. GT0100      (default GT0100)
            @JsonProperty("encoding") String encoding,  // odd | even       (default even)
            @JsonProperty("plan")     String plan,      // isdn | ...        (default isdn)
            @JsonProperty("nature")   String nature     // international|... (default international)
    ) { }

    // ══════════════════════════════════════════════════════════
    //  TCAP
    // ══════════════════════════════════════════════════════════
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Tcap(
            @JsonProperty("dialogIdleTimeout")     long dialogIdleTimeout,
            @JsonProperty("invokeTimeout")         long invokeTimeout,
            @JsonProperty("maxDialogs")            int maxDialogs,
            @JsonProperty("dialogIdRangeStart")    long dialogIdRangeStart,
            @JsonProperty("dialogIdRangeEnd")      long dialogIdRangeEnd,
            @JsonProperty("doNotSendProtocolVersion") boolean doNotSendProtocolVersion,
            @JsonProperty("statisticsEnabled")     boolean statisticsEnabled
    ) { }

    // ══════════════════════════════════════════════════════════
    //  Services — SSN -> logical service (drives multi-SSN dispatch)
    // ══════════════════════════════════════════════════════════
    /**
     * Binds a subsystem number to a logical service. The union of all SSNs
     * becomes the TCAP local SSN set (first declared = primary, the rest =
     * extra SSNs); inbound dialogs are dispatched to the matching service by
     * their called-party SSN. Example: {@code hlr=6} + {@code smsc=8} runs
     * home-routing HLR on SSN 6 and SMSC on SSN 8 on one stack.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Service(
            @JsonProperty("name")     String name,
            @JsonProperty("ssn")      int ssn,
            @JsonProperty("protocol") String protocol   // map | cap | tcap | inap
    ) { }
}
