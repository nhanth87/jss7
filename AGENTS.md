# AGENTS.md — jSS7-NG: Upgrade to Java 25 + micro-jainslee RA Integration

> **MISSION:** Upgrade jSS7 from Java 11 → Java 25 for maximum performance, then wrap as a micro-jainslee 3-port Resource Adaptor.
> **Target State:** jSS7 runs on ZGC + Virtual Threads + Log4j2 Async, exposes SS7 events via `RaBootstrapPort.fireEvent()` into the LMAX Disruptor-based `EventRouter`.
> **Baseline:** jSS7 9.2.8 | 4,337 Java files | 18 modules | Java 11 | log4j 1.2.14 | junit 3.8.1 | JCTools 4.0.3 | Netty 4.2.11

---

## 1. CONTEXT — READ THESE FIRST (in order)

| # | File | Purpose |
|---|------|---------|
| 1 | `PERFORMANCE_ANALYSIS.md` | JCTools vs Javolution vs java.util analysis + 279 Javolution XML remnants |
| 2 | `README.md` | jSS7-NG overview: JCTools migration, XStream serialization, 82 sub-modules |
| 3 | `Build_JSS7_Source.md` | Build instructions (mvn clean install) |
| 4 | `../jain-slee/micro-jainslee-2/AGENTS.md` | micro-jainslee runtime AGENTS (reference for RA pattern) |
| 5 | `../jain-slee/micro-jainslee-2/docs/en/ra-guide.md` | RA guide: 3-port contract, WRAPPER+DELEGATE pattern |

---

## 2. CURRENT STATE — jSS7 Module Architecture

```
                    service (uber-module — SKIP for RA)
                   /        |         \
              [oam]    [sniffer]    [tools]
                \_________|__________/
                          |
          [map] [cap] [inap]    [isup]
            \      |      /        |
             \     |     /         |
              [tcap] [tcap-ansi]   |
                  \   /            |
                  [sccp]  <--------/
                    |
                  [m3ua]
                    |
                  [mtp]
                    |
    [scheduler] [congestion] [statistics]
                    |
              [management]
                    |
                [ss7-ext]
```

### Layer-by-Layer

| Layer | Modules | Description | Dependencies |
|-------|---------|-------------|--------------|
| **Infrastructure** | `scheduler`, `congestion`, `statistics`, `management`, `ss7-ext` | I/O dispatch, flow control, counters, shell CLI | None (leaf) |
| **Transport** | `mtp`, `m3ua` | SIGTRAN M3UA + legacy MTP | scheduler, congestion |
| **Routing** | `sccp` | SCCP routing: GTT, connection-oriented/connectionless | m3ua, mtp |
| **Transaction** | `tcap`, `tcap-ansi` | TCAP dialog: BEGIN/CONTINUE/END/ABORT | sccp |
| **Application** | `map`, `cap`, `inap`, `isup` | MAP, CAMEL, INAP, ISUP protocols | tcap (map/cap/inap), sccp (isup) |
| **Tooling** | `oam`, `sniffer`, `tools`, `service` | Operations, trace, WildFly extension | All above |


### Key External API Surface (for RA integration)

| Interface | Module | Purpose | RA Mapping |
|-----------|--------|---------|------------|
| `SccpListener` | sccp-api | SCCP data + connection events | → `SS7SccpEvent` sealed hierarchy |
| `TCListener` | tcap-api | TCAP dialog lifecycle (BEGIN/CONTINUE/END/ABORT/UNI/NOTICE/TIMEOUT) | → `SS7TcapEvent` sealed hierarchy |
| `MAPDialogListener` | map-api | MAP dialog + service listeners | → `SS7MapEvent` sealed hierarchy |
| `M3UAManagementEventListener` | m3ua-api | M3UA AS/ASP state changes | → RA lifecycle events |
| `SccpManagementEventListener` | sccp-api | Remote subsystem/SPC state | → RA status events |
| `CAPDialogListener` | cap-api | CAMEL Application Part | → `SS7CapEvent` sealed hierarchy |
| `INAPDialogListener` | inap-api | Intelligent Network AP | → `SS7InapEvent` sealed hierarchy |

### Scheduler Architecture (jSS7 native)

```
Scheduler.java
  ├── OrderedTaskQueue (priority-ordered, lock-free via IntConcurrentLinkedList)
  ├── Clock (abstraction over System.nanoTime)
  ├── Task (Runnable wrapper with deadline + priority)
  └── Thread pool: N platform threads polling task queues
```

**Constraint:** jSS7 Scheduler is for **internal I/O dispatch only**. The micro-jainslee `SleeTimerSchedulerBridge` (Netty `HashedWheelTimer`) provides JAIN SLEE timer facility. They MUST NOT be merged — jSS7 Scheduler drives M3UA/SCCP/TCAP retransmission timers independently.

---

## 3. JAVA 25 UPGRADE BLOCKERS

### CRITICAL — Must fix before Java 25 compiles

| # | Blocker | Count | Modules | Fix |
|---|---------|-------|---------|-----|
| 1 | log4j 1.x (`log4j:log4j:1.2.14`) | 97 pom.xml + ~2,800 Java files | ALL | Migrate to Log4j2: `log4j-core:2.23.1` + `log4j-api:2.23.1` + async |
| 2 | junit 3.8.1 (`junit:junit:3.8.1`) | Root pom.xml + test classes | ALL test modules | Migrate to JUnit 5: `junit-jupiter:5.11.x` |
| 3 | Java version (`<compile.source>11</compile.source>`) | Root + all 97 child pom.xml | ALL | Bump to `<maven.compiler.release>25</maven.compiler.release>` |

### HIGH — Must fix for Virtual Threads & ZGC safety

| # | Blocker | Count | Modules | Fix |
|---|---------|-------|---------|-----|
| 4 | Javolution XML remnants | ~279 files | cap,inap,isup,map,m3ua,sccp,tcap,oam,tools | Complete XStream migration |
| 5 | Javolution collections | ~98 usages | Protocol modules | JCTools or java.util.concurrent |
| 6 | ThreadLocal usage | ~150+ files | ALL | Audit for VT safety; migrate TX to ScopedValue (JEP 506) |
| 7 | javax.* → jakarta.* | SS7Service.java (javax.naming.* + JBoss JMX) | service | Drop WildFly dependency for RA mode |
| 8 | WildFly/JBoss coupling | SS7Service, service/wildfly/extension/* | service | Extract core from extension |

### MEDIUM — Performance & Compatibility

| # | Blocker | Fix |
|---|---------|-----|
| 9 | `finalize()` methods | Remove — deprecated in Java 18, removed in Java 25 |
| 10 | SecurityManager / AccessController | Remove — deprecated in Java 17 |
| 11 | sun.misc.Unsafe (possible in scheduler) | Replace with VarHandle (Java 9+) |
| 12 | concurrent:concurrent:1.3.4 (obsolete backport) | Remove — java.util.concurrent built-in |

### Dependency Upgrade Map

| Dependency | Current | Target | Reason |
|------------|---------|--------|--------|
| `log4j:log4j` | 1.2.14 | REMOVE | CVE-2021-44228 |
| `org.apache.logging.log4j:log4j-core` | — | 2.23.1 | Async logging |
| `junit:junit` | 3.8.1 | REMOVE | Legacy test |
| `org.junit.jupiter:junit-jupiter` | — | 5.11.x | Modern testing |
| `org.jctools:jctools-core` | 4.0.3 | 4.0.5+ | Latest |
| `netty-transport-sctp` | 4.2.11.Final | Keep | Already current |
| `concurrent:concurrent` | 1.3.4 | REMOVE | Built-in since Java 5 |

---

## 4. TARGET STATE — Phase Roadmap

### Phase 1: Java 25 Build (CRITICAL — ~5 days)

**Goal:** `mvn clean install -DskipTests` passes on Java 25

1. **Bump Java version** in root `pom.xml`:
   `<maven.compiler.release>25</maven.compiler.release>`
2. **Migrate log4j 1.x → Log4j2** (bulk replace):
   - `import org.apache.log4j.Logger` → `import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger`
   - `Logger.getLogger(X.class)` → `LogManager.getLogger(X.class)`
   - All 97 pom.xml: replace log4j:log4j with log4j-core + log4j-api
   - Add log4j2.properties with: `log4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector`
3. **Remove concurrent:concurrent:1.3.4** — java.util.concurrent built-in
4. **Fix finalize()** → try-with-resources
5. **Remove SecurityManager** usage
6. **Build baseline:** `mvn clean install -DskipTests` must pass

### Phase 2: Test Modernization (~3 days)

1. **Migrate junit 3 → JUnit 5**:
   - `extends TestCase` → `@Test` annotations
   - `import junit.framework.*` → `import org.junit.jupiter.api.*`
2. **Run all tests:** `mvn test` — fix failures
3. **Remove testng 6.2** or bump to 7.10.x

### Phase 3: Javolution Complete Removal (~3 days)

1. **Finish XStream migration:** 279 remaining Javolution XML → XStream annotations
2. **Replace Javolution collections** (~98 usages):
   - `FastMap` → `NonBlockingHashMap` (JCTools) or `ConcurrentHashMap`
   - `FastList` → `MpscArrayQueue` (JCTools) or `ArrayList` + `synchronized`
   - `FastSet` → `NonBlockingHashSet` (JCTools) or `ConcurrentHashMap.newKeySet()`
3. **Remove javolution dependency** from all pom.xml

### Phase 4: Virtual Thread + ZGC Safety (~5 days)

1. **ThreadLocal → ScopedValue audit** for hot-path TX context
2. **Replace synchronized with ReentrantLock** where VT pinning risk exists
3. **Add ZGC tuning flags:**
   `-XX:+UseZGC -XX:+ZGenerational -XX:+UseVirtualThreads -Djdk.virtualThreadScheduler.maxPoolSize=512`

### Phase 5: RA Wrapper (~7 days)

**Goal:** `vendor-ras/ra-ss7/` module wrapping jSS7 as micro-jainslee 3-port RA

#### 5.1 Package Structure

```
vendor-ras/ra-ss7/
├── pom.xml                    ← deps: jainslee-api, jainslee-ra-spi, jSS7 modules
└── src/main/java/com/microjainslee/ra/ss7/
    ├── SS7RaEndpoint.java          ← WRAPPER: RaEndpointPort + RaCommandPort
    ├── SS7StackResourceAdaptor.java ← DELEGATE: wraps SccpStack + TcapStack + MapStack
    ├── SS7RaConfig.java            ← point codes, SSNs, SCTP addresses
    ├── command/
    │   └── SS7Command.java         ← sealed interface extends OutboundCommand
    ├── events/
    │   ├── SS7SccpEvent.java       ← SCCP layer events (sealed)
    │   ├── SS7TcapEvent.java       ← TCAP dialog events (sealed)
    │   ├── SS7MapEvent.java        ← MAP service events (sealed)
    │   ├── SS7CapEvent.java        ← CAP service events (sealed)
    │   └── SS7InapEvent.java       ← INAP service events (sealed)
    └── collab/
        ├── SS7SessionStore.java    ← Maps dialogId → ActivityHandle
        ├── SS7ListenerBridge.java  ← Bridges jSS7 listeners → fireEvent()
        └── SchedulerBridge.java    ← Integrates jSS7 Scheduler with SLEE TimerPort
```

#### 5.2 Event Types (sealed — fires into SLEE EventRouter)

```java
public sealed interface SS7TcapEvent extends SleeEvent
    permits SS7TcapEvent.TCBegin, SS7TcapEvent.TCContinue, SS7TcapEvent.TCEnd,
            SS7TcapEvent.TCAbort, SS7TcapEvent.TCUni, SS7TcapEvent.TCNotice,
            SS7TcapEvent.TCDialogTimeout, SS7TcapEvent.TCDialogReleased {

    record TCBegin(String dialogId, long remoteSpc, int remoteSsn,
                   byte[] userInfo, boolean hasApplicationContext)
            implements SS7TcapEvent, SleeEvent {}
    record TCContinue(String dialogId, byte[] userInfo)
            implements SS7TcapEvent, SleeEvent {}
    record TCEnd(String dialogId, byte[] userInfo)
            implements SS7TcapEvent, SleeEvent {}
    record TCAbort(String dialogId, boolean isProviderAbort, String reason)
            implements SS7TcapEvent, SleeEvent {}
    record TCUni(String sessionId, long remoteSpc, int remoteSsn, byte[] userInfo)
            implements SS7TcapEvent, SleeEvent {}
    record TCNotice(String dialogId, String problemDiagnostic)
            implements SS7TcapEvent, SleeEvent {}
    record TCDialogTimeout(String dialogId)
            implements SS7TcapEvent, SleeEvent {}
    record TCDialogReleased(String dialogId)
            implements SS7TcapEvent, SleeEvent {}
}
```

#### 5.3 Command Types (sealed — SBB → RA)

```java
public sealed interface SS7Command extends OutboundCommand
    permits SS7Command.SendSccpData,
            SS7Command.SendTcapBegin, SS7Command.SendTcapContinue,
            SS7Command.SendTcapEnd, SS7Command.SendTcapAbort,
            SS7Command.SendMapMessage, SS7Command.SendCapMessage,
            SS7Command.SendInapMessage, SS7Command.ConfigureStack {

    record SendSccpData(String sessionId, long calledGt, int calledSsn,
                        int translationType, byte[] data)
            implements SS7Command, OutboundCommand {}
    record SendTcapBegin(String dialogId, long remoteSpc, int remoteSsn,
                         long appCtxOid, byte[] userInfo)
            implements SS7Command, OutboundCommand {}
    record SendTcapContinue(String dialogId, byte[] userInfo)
            implements SS7Command, OutboundCommand {}
    record SendTcapEnd(String dialogId, byte[] userInfo)
            implements SS7Command, OutboundCommand {}
    record SendTcapAbort(String dialogId, boolean userAbort, String reason)
            implements SS7Command, OutboundCommand {}
    record SendMapMessage(String dialogId, int opCode, byte[] encodedPayload)
            implements SS7Command, OutboundCommand {}
    record SendCapMessage(String dialogId, int opCode, byte[] encodedPayload)
            implements SS7Command, OutboundCommand {}
    record SendInapMessage(String dialogId, int opCode, byte[] encodedPayload)
            implements SS7Command, OutboundCommand {}
    record ConfigureStack(String stackConfigJson)
            implements SS7Command, OutboundCommand {}
}
```

#### 5.4 SS7RaEndpoint (WRAPPER) — Design Sketch

```java
public final class SS7RaEndpoint implements RaEndpointPort, RaCommandPort {
    private static final Logger LOG = LogManager.getLogger(SS7RaEndpoint.class);
    private final SS7StackResourceAdaptor delegate;
    private RaBootstrapPort bootstrapPort;

    public SS7RaEndpoint(SS7StackResourceAdaptor delegate) { this.delegate = delegate; }
    public void setConfig(SS7RaConfig config) { delegate.setConfig(config); }

    @Override public String getRaName() { return "ss7-ra"; }

    @Override
    public void activate(RaBootstrapPort bootstrap) {
        this.bootstrapPort = bootstrap;
        delegate.setBootstrap(bootstrap);
        delegate.doConfigure();
        delegate.doStart();
        LOG.info("SS7 RA endpoint activated");
    }

    @Override
    public void deactivate() {
        delegate.doStop();
        this.bootstrapPort = null;
        LOG.info("SS7 RA endpoint deactivated");
    }

    @Override
    public void sendCommand(OutboundCommand command) {
        switch (command) {
            case SS7Command.SendSccpData cmd -> delegate.sendSccpData(cmd);
            case SS7Command.SendTcapBegin cmd -> delegate.sendTcapBegin(cmd);
            case SS7Command.SendTcapContinue cmd -> delegate.sendTcapContinue(cmd);
            case SS7Command.SendTcapEnd cmd -> delegate.sendTcapEnd(cmd);
            case SS7Command.SendTcapAbort cmd -> delegate.sendTcapAbort(cmd);
            case SS7Command.SendMapMessage cmd -> delegate.sendMapMessage(cmd);
            case SS7Command.ConfigureStack cmd -> delegate.handleConfigure(cmd);
            default -> LOG.warn("Unknown SS7 command: {}", command.getClass().getName());
        }
    }
    public SS7StackResourceAdaptor delegate() { return delegate; }
}
```

#### 5.5 SS7StackResourceAdaptor (DELEGATE) — Design Sketch

```java
public final class SS7StackResourceAdaptor {
    private static final Logger LOG = LogManager.getLogger(SS7StackResourceAdaptor.class);
    private volatile RaBootstrapPort bootstrap;
    private final AtomicBoolean active = new AtomicBoolean(false);
    private final ConcurrentHashMap<String, ActivityHandle> sessions = new ConcurrentHashMap<>();

    // jSS7 stack instances
    private SccpStack sccpStack;
    private TcapStack tcapStack;
    private MAPStack mapStack;
    private CapStack capStack;
    private InapStack inapStack;
    private M3UAManagement m3uaManagement;
    private SS7RaConfig config;
    private final SS7ListenerBridge listenerBridge = new SS7ListenerBridge(this);

    public void setBootstrap(RaBootstrapPort bp) { this.bootstrap = bp; }
    public void setConfig(SS7RaConfig c) { this.config = c; }

    public void doConfigure() {
        // Create jSS7 stacks from config
        // Configure: SCTP associations, point codes, SSNs, GTT rules, timeouts
    }

    public void doStart() {
        m3uaManagement.start();
        sccpStack.start();
        tcapStack.start();
        mapStack.start(); capStack.start(); inapStack.start();
        listenerBridge.register();  // bridges all jSS7 listeners → fireEvent
        active.set(true);
    }

    public void doStop() {
        active.set(false);
        listenerBridge.unregister();
        mapStack.stop(); capStack.stop(); inapStack.stop();
        tcapStack.stop(); sccpStack.stop(); m3uaManagement.stop();
        sessions.clear();
    }

    void fireInboundEvent(SleeEvent event, String sessionId) {
        if (!active.get()) return;
        ActivityHandle ah = bootstrap.createActivityHandle(sessionId);
        sessions.put(sessionId, ah);
        bootstrap.fireEvent(event, ah, null);
    }
}
```

#### 5.6 Scheduler Integration

```java
/**
 * SchedulerBridge — ties jSS7 internal scheduler lifecycle to SLEE container.
 * jSS7 Scheduler: SS7 protocol timers (M3UA retransmit, SCCP supervision, TCAP dialog timeout)
 * SLEE SleeTimerSchedulerBridge: Netty HashedWheelTimer for app-level SLEE TimerFacility
 * They COEXIST — different tick sources, different timer pools.
 */
public final class SchedulerBridge {
    private final org.restcomm.protocols.ss7.scheduler.Scheduler jss7Scheduler;
    public SchedulerBridge(int poolSize) {
        this.jss7Scheduler = new org.restcomm.protocols.ss7.scheduler.Scheduler();
        jss7Scheduler.setPoolSize(poolSize);
    }
    public void start() { jss7Scheduler.start(); }
    public void stop() { jss7Scheduler.stop(); }
    public Scheduler scheduler() { return jss7Scheduler; }
}
```

---

## 6. STRICT RULES

| ❌ NEVER | ✅ ALWAYS |
|----------|----------|
| Modify micro-jainslee runtime (jainslee-api, core, scheduler) | Keep jSS7 changes self-contained under vendor-ras/ra-ss7/ |
| Replace jSS7 Scheduler with Netty HashedWheelTimer | Keep jSS7 Scheduler for SS7 I/O dispatch only |
| Use new Disruptor<>() in RA | Always bootstrap.fireEvent() through listener bridge |
| Add Spring/Quarkus imports to ra-ss7 | Pure Java 25 + jSS7 deps + jainslee-api only |
| extends TestCase (JUnit 3) after Phase 2 | @Test annotation (JUnit 5) |
| import org.apache.log4j.Logger after Phase 1 | import org.apache.logging.log4j.LogManager |
| Use ThreadLocal for transaction state | ScopedValue for TX context (JEP 506) |
| Keep WildFly/JBoss SS7Service in RA path | Extract core stack init from service/service only |
| 1 generic SS7 event type | Sealed event hierarchy per layer (SCCP/TCAP/MAP/CAP/INAP) |
| extends AbstractResourceAdaptor for new RA | RaEndpointPort + RaCommandPort WRAPPER pattern |
| `java.nio.ByteBuffer` for buffers/slices in the SS7 data path | Netty `io.netty.buffer.ByteBuf` (+ `Unpooled`) everywhere |
| `ByteBuffer.wrap/allocate(8)` for long/double↔bytes scratch | Plain bit-ops (`(byte)(v>>56)…`, big-endian) — no allocation |

### 6.1 Buffer policy — Netty ByteBuf only

**Rule:** the canonical buffer/slice type across the SS7 stack is Netty
`io.netty.buffer.ByteBuf` (heap or pooled direct). `java.nio.ByteBuffer` is
**banned in the data path** — decode views, slices, zero-copy carriers, and public
buffer APIs must be `ByteBuf` or `byte[]`. Zero-copy decode uses `BerCursor`
(`byte[]`/`AsnBufferBackend`) and `BerSlice.asByteBuf()`; there is intentionally no
`asByteBuffer()`.

**The ONLY permitted `java.nio.ByteBuffer` uses:**
1. **Charset text codecs** — `Charset.decode()/encode()`, `CharsetDecoder/Encoder`
   require `java.nio.ByteBuffer`/`CharBuffer` (Java API constraint). This covers
   GSM7/UCS2/BCD text in `USSDStringImpl`, `GSMCharset*`, `smstpdu/*`,
   `CalledPartyBCDNumberImpl`, `SMSAddressStringImpl`, etc. — **do not "convert"
   these; it would break USSD/SMS text.** Bridge to Netty only via
   `byteBuf.nioBuffer()` at the codec boundary when a `ByteBuf` must be fed to a
   Charset.
2. **Management shell transport** (`management/shell-transport/*`) — built on
   `java.nio` `SocketChannel`; out of the SS7 data path (OAM CLI). Leave as-is.

**Migrated across the whole SS7 data path (2026-07-11):**
- `asn`: `BerSlice` (dropped `asByteBuffer()`, kept Netty `asByteBuf()`);
  `AsnInputStream` + `AsnOutputStream` (REAL/double ↔ bytes → bit-ops).
- `tcap` + `tcap-ansi` `Utils` (txid long ↔ bytes → `beLong` bit-ops).
- `map` `TimeImpl` (long ↔ bytes → bit-ops).
- `m3ua` `AspFactoryImpl`: removed the vestigial `ByteBuffer.allocateDirect(8192)`
  `txBuffer` (used only as a lock; TX already uses Netty `ByteBuf`) → plain `txLock`.
- `mtp` `Utils`: removed the unused `dump(ByteBuffer)` overload (callers pass `byte[]`).
- **Deleted dead pilots** `AsnOptimizedInputStream` + `AsnOptimizedOutputStream`.
All numeric conversions verified byte-exact vs the old `ByteBuffer` path (2M random
iters, 0 mismatch); all touched modules recompile clean on JDK 25.

**Remaining `java.nio.ByteBuffer` is ONLY the two permitted cases above** (Charset
text codecs + `management/shell-transport` OAM console on `java.nio` channels).
Converting the shell transport means porting `ShellChannel` to Netty channels — a
separate OAM-only task, no SS7-data-path benefit; deferred.

---

## 7. EXECUTION ORDER

1. **Phase 1** — Java 25 build (pom.xml bulk update + log4j2 migration) → `mvn clean install -DskipTests` passes
2. **Phase 2** — JUnit 5 migration → `mvn test` passes with same/similar test count
3. **Phase 3** — Javolution removal (XStream completion + collections → JCTools) → zero javolution imports
4. **Phase 4** — Virtual Thread safety audit (ThreadLocal → ScopedValue, synchronized → ReentrantLock)
5. **Phase 5** — RA wrapper (vendor-ras/ra-ss7/) → compile + integration test:

```java
// Integration test: bootstrap micro-jainslee + jSS7 RA
MicroSleeContainer c = new MicroSleeContainer(
    MicroSleeConfiguration.builder()
        .preferVirtualThreads(true).sbbPoolMax(10_000).build());
c.start();
var ra = new SS7RaEndpoint(new SS7StackResourceAdaptor());
ra.setConfig(SS7RaConfig.fromJson(testConfigJson));
c.registerRa(ra, ra);
// Verify: M3UA/SCCP/TCAP stacks started, listener bridge registered
// Send synthetic SCCP N-UNITDATA → assert SBB receives SS7SccpEvent
```

---

## 8. DONE WHEN

- [ ] `mvn clean install` passes on Java 25 (all 18 modules + ra-ss7)
- [ ] `mvn test` passes (zero failures, similar test count)
- [ ] Zero `import org.apache.log4j` anywhere
- [ ] Zero `import javolution` anywhere
- [ ] Zero `import junit.framework` anywhere
- [ ] Zero `javax.naming.*` in non-service modules
- [ ] `SS7RaEndpoint` implements `RaEndpointPort` + `RaCommandPort`
- [ ] `SS7StackResourceAdaptor.doStart()` initializes full jSS7 stack (M3UA→SCCP→TCAP→MAP)
- [ ] `SS7ListenerBridge` routes TCAP BEGIN/CONTINUE/END/ABORT → `bootstrap.fireEvent()`
- [ ] `SS7ListenerBridge` routes SCCP N-UNITDATA/N-NOTICE → `bootstrap.fireEvent()`
- [ ] SBB receives `SS7TcapEvent.TCBegin` and can reply with `SS7Command.SendTcapEnd`
- [ ] jSS7 Scheduler coexists with SLEE SleeTimerSchedulerBridge (no conflict)
- [ ] Integration test: synthetic M3UA→SCCP→TCAP→MAP roundtrip through RA→SBB→RA
- [ ] ZGC + Virtual Threads flags applied; no carrier-thread starvation under load
- [ ] Async Log4j2 logging configured; hot-path logging guarded with `if (log.isDebugEnabled())`

---

## 9. TEAMUP REPORT — Multi-Agent Analysis Summary

### Agent: jss7-architect
- **Duration:** ~12 min, 9 iterations
- **Findings:** Full module dependency graph mapped. 6 architectural layers identified (Infrastructure → Transport → Routing → Transaction → Application → Tooling). Key API surface: `SccpListener`, `TCListener`, `MAPDialogListener`, `M3UAManagementEventListener`, `SccpManagementEventListener`. Scheduler is internal I/O dispatcher — must NOT be replaced by SLEE timer. WildFly service module is dead weight for RA path.

### Agent: java25-upgrader
- **Duration:** ~5.6 min, 13 iterations
- **Findings:** 
  - log4j 1.2.14 across ALL modules (~2,800 imports)
  - junit 3.8.1 in test modules
  - ~279 Javolution XML remnants + ~98 collection usages
  - ~150+ ThreadLocal usages across codebase
  - `concurrent:concurrent:1.3.4` (obsolete backport)
  - `javax.naming.*` + JBoss JMX in service module
  - Java 11 source/target in root + all 97 pom.xml

### Agent: ra-designer
- **Duration:** ~19.5 min, 14 iterations
- **Findings:** Complete RA design produced. Package structure, sealed event/command hierarchies, SS7RaEndpoint (WRAPPER), SS7StackResourceAdaptor (DELEGATE) with full jSS7 stack lifecycle. SS7ListenerBridge pattern for bridging jSS7 listeners → SLEE fireEvent. SchedulerBridge for jSS7 internal scheduler coexistence. SS7RaConfig for point codes, SSNs, SCTP addresses, GTT rules.

---

## 10. REFERENCE FILES

| File | Path | Purpose |
|------|------|---------|
| jSS7 root pom | `pom.xml` | Java version, module list, dependency management |
| Performance analysis | `PERFORMANCE_ANALYSIS.md` | JCTools migration status, Javolution remnants |
| Build guide | `Build_JSS7_Source.md` | Maven commands, Dialogic/MXTool deps |
| micro-jainslee AGENTS | `../jain-slee/micro-jainslee-2/AGENTS.md` | Runtime AGENTS (3-port contract, GOALs 1-5) |
| RA design guide | `../jain-slee/micro-jainslee-2/docs/en/ra-guide.md` | Full RA implementation guide |
| Reference RA (HTTP) | `../jain-slee/micro-jainslee-2/vendor-ras/ra-http-server/` | WRAPPER+DELEGATE pattern |
| Reference RA (Diameter) | `../jain-slee/micro-jainslee-2/vendor-ras/ra-diameter/` | Complex protocol RA pattern |
| Audit v2 | `~/Desktop/ethiopia-working-dir/docs/micro-jainslee-audit-v2.md` | micro-jainslee compliance + perf gaps |

---

**Last Updated:** 2026-07-12  
**Team:** `jss7-architect` + `java25-upgrader` + `ra-designer` (3 multi-agents)  
**Status:** Phase 1-2 complete; Phase 3-5 pending. Ant builds fixed for Java 25 + Log4j2 + Lombok.

---

## 11. DEVELOPMENT SETUP — Java 25 & Build Commands

### 11.1 Java 25 via mise

```bash
# Java 25 is managed via mise (Zulu 25.34.17)
export JAVA_HOME=/home/meodien/.local/share/mise/installs/java/zulu-25.34.17.0
export PATH=$JAVA_HOME/bin:$PATH
java -version  # openjdk version "25.0.3" LTS
```

### 11.2 Build commands

```bash
# Full project build once (first time or after changing dependencies)
cd /home/meodien/Desktop/ethiopia-working-dir/worktrees/jSS7/coral-valley/jSS7
JAVA_HOME=~/.local/share/mise/installs/java/zulu-25.34.17.0 mvn compile -pl map/load -am -Dmaven.test.skip=true

# Generate Ant classpath (once after pom.xml changes)
cd map/load
ant -f ussd_build.xml setup
```

### 11.3 Running MAP Load Tests — NEW JSON CONFIG (ss7-config)

**Single JSON config replaces 17 command-line args + 5 XML files.**

```bash
cd map/load

# Build once
JAVA_HOME=~/.local/share/mise/installs/java/zulu-25.34.17.0 mvn compile -pl map/load -am -Dmaven.test.skip=true

# Start server (Terminal 1)
ant -f ussd_build.xml server

# Start client (Terminal 2) 
ant -f ussd_build.xml client

# Or with custom load params:
ant -f ussd_build.xml client -Dtest.client.numOfDialogs=10000 -Dtest.client.concurrentDialog=500

# Cleanup when done
ant -f ussd_build.xml clean
```

**Config files** (`ss7-server.json`, `ss7-client.json`) contain everything:
- SCTP: host, port, peer host, peer port, channel type
- M3UA: point codes, routing context, network appearance, service indicator, network indicator
- SCCP: remote SSNs, GTT routing rules
- TCAP: timers, SSN list
- MAP: service list

**IMPORTANT:** Before running, kill old processes:
```bash
ant -f ussd_build.xml clean    # or: pkill -9 -f 'ussd.Server' && pkill -9 -f 'ussd.Client'
```

### 11.3b Legacy SMS load tests (still use XML + args)

```bash
JAVA_HOME=~/.local/share/mise/installs/java/zulu-25.34.17.0 ant -f mo_sms_build.xml compile server
JAVA_HOME=~/.local/share/mise/installs/java/zulu-25.34.17.0 ant -f mo_sms_build.xml compile client
JAVA_HOME=~/.local/share/mise/installs/java/zulu-25.34.17.0 ant -f mt_sms_build.xml server
JAVA_HOME=~/.local/share/mise/installs/java/zulu-25.34.17.0 ant -f mt_sms_build.xml client
```

### 11.4 Lombok — Annotations in use

All load-test Client/Server classes use **Lombok** `@Log4j2` for automatic logger generation:

```java
@Log4j2  // generates: private static final Logger log = LogManager.getLogger(XXX.class);
public class Client extends TestHarnessUssd {
    // no explicit Logger field needed — use log.info(), log.error(), etc.
}
```

Other useful Lombok annotations available:
- `@ToString` — auto-generate toString()
- `@EqualsAndHashCode` — auto equals/hashCode
- `@Data` — combines @Getter, @Setter, @ToString, @EqualsAndHashCode
- `@Builder` — builder pattern
- `@Slf4j` — if SLF4J bridge preferred over direct Log4j2

**File coverage:** Lombok `@Log4j2` applied to:
- `ussd/Client.java`, `ussd/Server.java`
- `sms/mo/Client.java`, `sms/mo/Server.java`
- `sms/mt/Client.java`, `sms/mt/Server.java`

Files NOT using Lombok (named loggers or non-static patterns):
- `ussd/TestHarnessUssd.java` — named logger `"map.test"`
- `sms/mo/TestHarnessSmsMo.java` — named logger
- `sms/mt/TestHarnessSmsMt.java` — named logger
- `MapServiceUssdClient.java` — non-static instance logger
- `CsvWriter.java` — no logger field

### 11.5 Dependency Notes

| Dependency | Status | Notes |
|------------|--------|-------|
| `log4j-core` + `log4j-api` + `log4j-slf4j-impl` | ✅ In pom.xml | Async logging via log4j2.xml |
| `lombok` 1.18.42 | ✅ In pom.xml | `provided` scope |
| `jctools-core` | ✅ In pom.xml | Lock-free queues |
| `jackson-dataformat-xml` + transitive | ✅ In pom.xml | Replaces XStream |
| `concurrent:concurrent:1.3.4` | ❌ REMOVED | Obsolete backport of java.util.concurrent |
| `javolution:javolution:5.3.1` | ❌ REMOVED | Replaced by Jackson + JCTools |
| `xstream` / `mxparser` / `xmlpull` | ❌ REMOVED | Replaced by Jackson XML |
| `log4j:log4j:1.2.14` | ❌ REMOVED | Replaced by Log4j2 |

### 11.6 SCTP Port Matching (critical for localhost testing)

Server and client MUST have matching SCTP ports for `addServerAssociation` to work:

```
Server: addServer(SERVER_NAME, HOST_IP, 8011)           → listen on :8011
Server: addServerAssociation(PEER_IP, 8012, ...)         → expect client at :8012
Client: addAssociation(HOST_IP, 8012, PEER_IP, 8011, ...) → bind :8012, connect to :8011
```

| Role | hostPort | peerPort | Meaning |
|------|----------|----------|---------|
| Server | 8011 | 8012 | Listen :8011, expect client :8012 |
| Client | 8012 | 8011 | Bind :8012, connect server :8011 |

**Port 0 is NOT valid** for `addServerAssociation` — throws `Peer port cannot be less than 1`.
Client MUST bind to a specific port matching server's `peerPort`.

| Ant property | Server value | Client value |
|--------------|-------------|-------------|
| `hostPort` | 8011 | 8012 |
| `peerPort` | 8012 | 8011 |

### 11.7 log4j 1.x cleanup

Old `src/main/resources/log4j.properties` was deleted — it caused log4j 1.x `FileNotFoundException` warnings.
Only `log4j2-server.xml` / `log4j2-client.xml` are used, loaded via `-Dlog4j.configurationFile=` JVM arg.
