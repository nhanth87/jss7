# W2 Scheduler Design Notes

## Problem statement

FIFO toàn cục không phù hợp khi SS7 operation có service time khác nhau. Một job nặng/chờ downstream (ATI/HLR/MSC) có thể chặn job ngắn (SRI lookup/MT forwarding). Nhưng scheduler không được phá TCAP dialog causality hoặc làm timeout/END thắng RESULT do reorder.

## Tách hai plane bắt buộc

| Plane | Trách nhiệm | Không chịu trách nhiệm |
|---|---|---|
| Message/work scheduler | admission, chọn work kế tiếp, fairness, per-dialog serial order, backpressure | độ chính xác timer expiry |
| Timer scheduler | arm/cancel expiry, deadline lateness, phát `TimerExpired` event | chọn thứ tự protocol work giữa dialog |

Timer expiry phải enqueue vào mailbox của dialog; chỉ mailbox owner mới làm timeout transition. Đây là điều kiện tránh race `RESULT vs timeout`, `END vs idle expiry`, cancel-vs-fire.

## Local target architecture (không distributed trước)

```text
SCCP/TCAP ingress
  → classify (protocol, operation, source/tenant, deadline, estimated cost)
  → bounded keyed mailbox[dialogId]
  → eligible-dialog scheduler (fairness + urgency)
  → one serial drainer per dialog
  → current TCAP/MAP/CAP processing

Timer facility → TimerExpired(dialogId, epoch, timerId) → same mailbox[dialogId]
```

### Invariants

1. Mỗi dialog có tối đa một active drainer.
2. Events của một dialog xử lý FIFO/causal order; terminal transition idempotent.
3. Không chạy application callback khi giữ global scheduler lock.
4. Queue bị bounded; overload phải admit, shed hoặc fast-fail rõ ràng — không unbounded latency.
5. Timer cancellation/fire có generation/epoch để stale expiry không thắng completion.
6. Scheduler policy chỉ reorders giữa **eligible dialogs**, không reorders trong dialog.

## Policy đề xuất: hybrid, không EDF thuần

1. **Admission/congestion gate:** queue capacity, queue-age threshold, per source/tenant quota và hysteresis.
2. **Per-dialog FIFO mailbox:** correctness baseline.
3. **Urgent tier:** chỉ cho work có positive/slim slack; có budget/quota để không starve normal traffic.
4. **Fair scheduling:** DRR/WFQ-style virtual service cho tenant/source hoặc route class để cô lập noisy neighbor.
5. **Cost-aware within fair lane:** initial estimate theo protocol/operation + observed history; estimated SJF/SRPT-style chỉ khi confidence tốt. Estimate xấu chuyển qua LAS/MLFQ/aging; overrun demotion.
6. **Deadline:** deadline là soft priority trừ khi standards/product policy định nghĩa hard deadline. `slack = deadline - now - estimated_remaining_cost`.

Không scan tất cả outstanding timer mỗi 30 ms như mẫu Grok: đó là O(N) periodic work, tốn allocation/GC và duplicate urgent enqueue. Timer queue nên natively ordered by deadline (wheel + near-term heap/queue nếu benchmark chứng minh cần thiết).

## Đánh giá mẫu DeadlineAwareTimerScheduler/DistributedTcapProvider

Không coi mẫu đó là drop-in vì:

- `TimerCallback` local object/class name không serializable/routable/durable; node khác không thể gọi callback đúng.
- Random ID không đảm bảo uniqueness/fencing lifecycle; `containsKey` + `remove` không atomic ownership claim.
- cache expiry và local wheel/urgent queue có thể cùng fire; thiếu atomic state/generation sẽ duplicate callback.
- `urgentQueue.contains/removeIf` là O(N), scanner toàn cache O(N); poller có thể fire trước `fireAt` nếu queue không chờ đúng deadline.
- `super(delegate)` cho `TCAPProviderImpl` không được xác minh API/cấu trúc; composition/adapter tại boundary thực tế an toàn hơn.
- overriding "sendComponent" chưa chắc là interception point của actual jSS7 API; `InvokeImpl` hiện tự lifecycle timer theo state.
- operation code hardcode không đủ: operation semantics/version/context, route, tenant và observed cost đều ảnh hưởng.

## Distributed phase (defer)

Chỉ cân nhắc sau local rollout. Bắt buộc có `dialogId → ownerNode, epoch, state`, ingress routing theo owner, fencing token trên message/timer, durable handoff, idempotent outbound/outbox, transaction-ID policy đa node và fault tests (duplicate/reorder/rebalance/crash). Infinispan cache không tự cung cấp per-dialog ordering hay exactly-once TCAP send.

## Implementation decision — Case 2 (2026-07-19)

`W2KeyedMailboxDispatcher` is the first local ordering primitive. It has a global bounded event capacity and an `ArrayDeque` FIFO per `dialogKey`; only a mailbox head is placed in the priority queue. Completion removes that head and makes the next head eligible. Therefore an event's priority/deadline is considered only after all preceding events from the same dialog complete. It is wired as the default feature-configurable TCAP ingress dispatcher; pass `-Dss7.tcap.w2Scheduler.enabled=false` for the Argona/FIFO path. MAP/CAP decoded service callbacks are also feature-flagged (`ss7.map.w2Scheduler.enabled`, `ss7.cap.w2Scheduler.enabled`) and classed by actual operation code: MAP Location/Auth = CRITICAL; USSD/SMSC routing and MT forward = HIGH; CAP charging = CRITICAL. Classification affects only cross-dialog selection.

## Migration sequence

1. Telemetry-only: queue age/depth, service cost, slack, timer lateness, cancel/fire races.
2. `TcapTimerScheduler` abstraction sau `createOperationTimer`; migrate invoke timer trước, giữ idle JDK sticky timer.
3. Local keyed mailbox chạy shadow/feature flag nhưng chưa alter dispatch policy.
4. Bounded admission + per-dialog serial drainer; correctness/race tests.
5. Fairness layer, rồi urgency, sau đó cost-aware policy canary.
6. Chỉ sau benchmark + fault tests mới đánh giá distributed ownership.
