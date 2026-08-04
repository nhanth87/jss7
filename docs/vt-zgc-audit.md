# Virtual Thread + ZGC audit (jSS7 Phase 4)

> **Date:** 2026-08-04 · **Branch:** `j25` · **JDK:** 25 (mise zulu-25)  
> **Status:** Checklist — run under MAP load before claiming VT/ZGC safe.

## Goals

1. No carrier-thread pinning on MAP/TCAP hot paths under Virtual Threads.
2. No `ThreadLocal` TX / dialog state that breaks when VT hops carriers.
3. ZGC pauses acceptable under target dialog concurrency.

## Suggested JVM flags (lab)

```bash
-XX:+UseZGC -XX:+ZGenerational \
-Djdk.virtualThreadScheduler.maxPoolSize=512 \
-Xms2g -Xmx2g
```

## Audit items

| Area | Risk | Check |
|------|------|-------|
| TCAP dialog maps | Concurrent maps OK; watch synchronized blocks around export/import | Thread dump under load — no long `synchronized` held across I/O |
| M3UA / SCTP Netty | Platform threads for I/O — OK | Do not schedule protocol timers on VT carriers that pin |
| `ThreadLocal` in stack | Legacy TX / codec locals | `rg 'ThreadLocal' --glob '**/src/main/**/*.java'` — migrate hot ones to `ScopedValue` or explicit context |
| Java serialization leases | Replaced by versioned bytes (2026-08) | Confirm no `ObjectOutputStream` on dialog lease path |
| ra-jss7 sticky / failover | Fail-closed REJECT when route down | Metrics: `ss7_tcap_sticky_reject_total` |

## Soak recipe

1. Start jSS7 simulator + micro-jainslee RA (or OTA lab pair).
2. Drive MAP/USSD or SRI+MT at sustained rate for ≥30 min with ZGC flags.
3. Sample: `jcmd <pid> Thread.dump_to_file` looking for pinned virtual threads.
4. Record p99 pause from ZGC logs; fail if >50 ms sustained under lab target.

## Done when

- [ ] No unexpected VT pin storms under MAP load
- [ ] Hot-path ThreadLocals inventoried and cleared or documented
- [ ] ZGC p99 within lab SLA
- [ ] Link status truth unchanged (`isM3uaRouteReady` only)
