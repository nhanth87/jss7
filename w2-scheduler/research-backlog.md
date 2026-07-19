# W2 Research Backlog, Benchmark Plan and Decision Log

## Questions to answer before implementation

- What is the true unit of work: TC package, TCAP component, MAP/CAP operation, or application callback? Initial hypothesis: schedule at dialog mailbox turn, classify from decoded operation metadata, never split causal dialog events.
- Which deadlines are normative TCAP/MAP/CAP timers versus product SLOs? Record hard/soft semantics separately.
- How accurate is operation-based cost estimation (CPU + downstream wait)? Does ATI vary by route/HLR/MSC/tenant enough to defeat static class labels?
- Can high-priority/urgent work starve normal work? What quota/aging gives bounded waiting?
- Which overload action is protocol-safe per class: reject, provider abort, local fast-fail, drop only before dialog creation, or defer?
- How are callbacks/reentrancy/user objects affected by moving from synchronous delivery to a mailbox thread?

## Mandatory correctness tests

1. Same-dialog inbound order remains deterministic under concurrent ingress.
2. RESULT vs operation-timeout race: exactly one terminal action; stale timeout ignored.
3. END/ABORT vs idle-expiry race: no duplicate abort/notification.
4. Cancellation vs wheel fire race; no callback after successful completion except a safely ignored stale event.
5. Bounded queue at saturation: defined admission outcome; no unbounded memory growth.
6. Noisy tenant/source cannot starve other fair-share lanes.
7. Long job cannot be permanently starved by short/urgent jobs (aging/budget test).
8. Feature-flag off reproduces current ordering/thread behavior as far as API contract requires.

## Benchmark matrix

### Baselines

- Current direct dispatch / current TCAP timers.
- Per-dialog mailbox + global FIFO eligible dialog selection.
- Fair keyed scheduling only.
- Fair + deadline urgency.
- Fair + deadline + estimated cost, with confidence fallback.

### Workloads

| Workload | Purpose |
|---|---|
| Homogeneous short | quantify pure scheduler overhead |
| Bimodal short/long | demonstrate/reject head-of-line benefit |
| Heavy-tailed cost | production-like unknown size stress |
| Deadline burst | expiry/urgent-tier behavior |
| Single noisy source | fairness and admission |
| Hot dialog + many cold dialogs | keyed serialization/affinity |
| Near-timeout result/end races | protocol integrity |
| Sustained overload + recovery | hysteresis, tail latency and shedding |

### Measurements

**Latency:** queue wait, service time, end-to-end response, p50/p95/p99/p99.9 per operation and cost bucket.

**Deadline:** arrival slack, start slack, timer fire lateness, feasible-but-missed vs infeasible-at-arrival, timeout ratio.

**Fairness:** admitted/rejected per tenant/source/class, normalized service share, Jain fairness index, max starvation age.

**Cost estimator:** estimate vs actual, error quantiles, confidence calibration, overruns/demotions.

**Integrity:** ordering violations, stale/duplicate timeout, duplicate terminal action, state-transition failures.

**Resources:** CPU, allocations/GC, queue depth, active mailbox count, timer count.

## Experiment record template

```text
Date / author:
Commit SHA:
JDK, host CPU/RAM, JVM flags:
Policy and config (capacity, weights, urgency threshold, aging, estimator):
Traffic model and seed:
Protocol/message fixture:
Warmup / duration / repetitions:
Raw output location and dashboard link:
Baseline comparison:
Results (including confidence interval):
Correctness anomalies:
Decision / next action:
```

## Delivery milestones

| Milestone | Exit criteria |
|---|---|
| M0 — instrumentation | no behavior change; timer/message metrics emitted and tested |
| M1 — timer abstraction | invoke timer behind compatible API; race tests pass; idle timer unchanged |
| M2 — keyed mailbox | feature-flagged; order/cancel/terminal tests pass; bounded backpressure |
| M3 — policy canary | shadow decision logs; benchmark improves target tail/deadline metric without fairness/integrity regression |
| M4 — production rollout | canary, rollback switch, dashboards, overload runbook |
| M5 — distributed feasibility | ownership epoch, routing, handoff/outbox and crash/rebalance tests designed and approved |

## Decision log

| Date | Decision | Why |
|---|---|---|
| 2026-07-19 | Persist research before implementation | avoid unsupported novelty claims and retain traceable citations |
| 2026-07-19 | Separate timer and message scheduling | timer expiry does not preserve protocol transition order |
| 2026-07-19 | Local keyed design precedes distributed cache/provider wrapper | current stack has local dialog state/callback semantics; distributed ownership needs fencing/handoff/outbox |
| 2026-07-19 | Do not use periodic full-cache timer scan as default | O(N) load/duplicate risk; benchmark a native deadline structure instead |
