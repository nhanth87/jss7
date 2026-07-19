# W2 Scheduler — Research & Design Log

Thư mục này là hồ sơ bền vững cho nghiên cứu, thiết kế và implementation từng bước của **winwin scheduler (w2-scheduler)** cho jSS7. Module Maven hiện triển khai primitive dispatcher local (case 1), nhưng chưa được wire vào TCP/TCAP/MAP/CAP runtime nên chưa thay đổi hành vi protocol hiện tại.

## Mục tiêu

Một SS7 operation có chi phí xử lý không đồng nhất: ví dụ MAP ATI có thể phụ thuộc HLR/MSC, trong khi MT-Forward có thể chỉ cần tra SRI rồi forward. Vì vậy FIFO dùng chung có thể tạo head-of-line blocking và làm tăng timeout/tail latency. W2 nghiên cứu scheduler có nhận thức về cost, deadline, fairness và tính đúng đắn protocol.

## Nội dung

| File | Nội dung |
|---|---|
| [research-landscape.md](research-landscape.md) | Paper/spec/link đã kiểm tra và mức liên quan |
| [current-state-audit.md](current-state-audit.md) | Bằng chứng code hiện tại tại SCCP/TCAP/MAP/CAP |
| [design-notes.md](design-notes.md) | Các quyết định và kiến trúc đề xuất theo giai đoạn |
| [research-backlog.md](research-backlog.md) | Câu hỏi còn mở, benchmark và cách cập nhật |

## Implementation status

### Case 1 — bounded local dispatcher (implemented)

`org.restcomm.protocols.ss7.scheduler.w2.W2Dispatcher` consumes immutable `W2Work<Runnable>` from the foundation queue with strict **priority → monotonic deadline → FIFO** selection. It has one explicit daemon worker, non-blocking bounded admission, graceful drain on `stop()` (including work admitted before `start()`), exception isolation and a metrics snapshot.

This is an infrastructure primitive only. It is deliberately not wired to TCAP/MAP/CAP.

### Case 2 — keyed mailbox and feature-flagged TCAP ingress (implemented)

`W2KeyedMailboxDispatcher` provides a globally bounded, FIFO mailbox for each `W2Work.dialogKey`. It permits at most one active mailbox drainer per key, while a configurable worker pool drains different keys in parallel. W2 priority/deadline selection applies only among eligible mailbox heads; a later high-priority event can never overtake an earlier event from the same key.

`TCAPProviderImpl` now has an opt-in ingress adapter (`-Dss7.tcap.w2Scheduler.enabled=true`). It keys pre-decode work by inbound SCCP flow (`networkId/opc/SLS/calling/called address`) and runs the existing TCAP decode/FSM unchanged inside the mailbox. This establishes an executable USSD/SMSC load-test path, but is **not yet MAP/CAP operation-aware**: MAP/CAP opcode and application context are unavailable until after TCAP parsing. Queue exhaustion logs a warning and processes the item inline rather than silently dropping SS7 traffic.

## Kết luận hiện tại

1. jSS7 hiện không có một FIFO message scheduler dùng chung cho TCAP/MAP/CAP. Inbound được dispatch trực tiếp từ SCCP vào TCAP rồi callback synchronous đến MAP/CAP/app; SCCP có lane executor theo SLS cho local-originated traffic.
2. Timer là cơ chế riêng: invoke timeout chủ yếu dùng Netty `HashedWheelTimer`; dialog idle timeout dùng JDK `ScheduledExecutorService` với sticky absolute deadline.
3. Không được thay thế timer bằng message scheduler hoặc ngược lại. Trước hết cần local, bounded, keyed per-dialog mailbox để bảo toàn causality; deadline/cost policy chỉ chọn dialog/job kế tiếp giữa các key.
4. Đã có research trực tiếp về SS7 performance/congestion. Chưa xác minh được một công trình công khai đề xuất chính xác tổ hợp: per-dialog ordering + deadline-aware + fair sharing + unknown-size-aware dispatch + timer plane tách riêng cho TCAP/MAP/CAP runtime. Đây là nhận định theo phạm vi nguồn, **không phải** chứng minh tuyệt đối không tồn tại.

## Quy ước cập nhật

- Mỗi paper mới: thêm citation/link, ngày kiểm tra, phạm vi và giới hạn áp dụng ở `research-landscape.md`.
- Mỗi quyết định thiết kế: bổ sung vào `design-notes.md` với lý do và impact.
- Benchmark/result: ghi hypothesis, cấu hình, commit SHA, workload, raw data location và kết luận vào `research-backlog.md`.
- Không lấy operation name làm "size" tuyệt đối: đo actual service time và lưu confidence/uncertainty trước khi ưu tiên theo estimated cost.

## Boundary

W2 trước mắt là local scheduler research cho jSS7. Distributed ownership/Infinispan/RA bootstrap là chương trình kiến trúc riêng, chỉ xem xét sau khi local ordering, backpressure, fault semantics và observability được chứng minh.
