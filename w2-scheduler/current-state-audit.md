# Current-State Audit: SCCP / TCAP / MAP / CAP

**Audit mode:** read-only. Các line references bên dưới phản ánh worktree khảo sát ngày 2026-07-19.

## Luồng thực tế

```text
SCCP delivery thread → TCAPProviderImpl.onMessage → decode/process TC package
                     → TCListener callbacks → MAPProviderImpl/CAPProviderImpl → application listeners
```

`TCAPProviderImpl.onMessage(SccpDataMessage)` decode ASN.1 và xử lý TC-CONTINUE/BEGIN/END/ABORT/UNI trực tiếp, không enqueue sang W2 hay scheduler module:

- Entry và dispatch: `tcap/tcap-impl/.../TCAPProviderImpl.java:712-735`.
- TC-CONTINUE lookup/process: `:767-792`.
- TC-BEGIN: `:867-875`; END: `:879-914`; ABORT: `:917-953`; UNI: `:956-974`.
- TC listener list là `CopyOnWriteArrayList`; fan-out callback synchronous (`TCAPProviderImpl.java:98`, `tcap-api/.../TCListener.java:20-90`).
- MAP/CAP implement `TCListener` và xử lý component/dialog callbacks trực tiếp. CAP example: `cap/cap-impl/.../CAPProviderImpl.java:360-387`, `414-470`.

## "FIFO" hiện hữu nằm ở đâu?

Không tìm thấy shared FIFO worker queue cho TCAP/MAP/CAP dispatch. Ở SCCP, local-user-originated SCCP delivery được submit tới một executor lane được chọn theo masked SLS:

- `sccp/sccp-impl/.../SccpRoutingControl.java:871-880`.

Đó là affinity/order theo SLS, không phải operation-aware TCAP/MAP/CAP scheduler. Inbound MTP-originated SCCP delivery tại đây gọi listener trực tiếp (`:872-874`).

Module `scheduler` có các lane/task queue lịch sử, nhưng audit không tìm thấy TCAP/MAP/CAP code path dùng nó để dispatch message hay timeout. Root build có module scheduler (`pom.xml:59-78`); `scheduler/pom.xml` chỉ phụ thuộc Log4j API và Agrona.

## Timer plane hiện tại

| Loại | Cơ chế | Evidence |
|---|---|---|
| Invoke operation timeout | `provider.createOperationTimer(...)`; thường Netty wheel, fallback JDK scheduler | `tcap/tcap-impl/.../asn/InvokeImpl.java:356-369` |
| Dialog idle timeout | JDK `ScheduledExecutorService`; sticky absolute deadline, không dùng wheel vì churn restart | `tcap/tcap-impl/.../DialogImpl.java:2060-2150` |
| Per-dialog integrity | `ReentrantLock` bảo vệ transition | xem `DialogImpl` lock quanh timer/process paths |

`InvokeImpl` start timer ở state `Sent`, stop timer khi `Idle` hoặc `Reject_W` (`InvokeImpl.java:320-369`). `DialogImpl` ghi rõ comment: idle timer phải ở JDK pool; repeated restart làm quá tải single wheel worker. Vì vậy không được thay toàn bộ timer bằng wheel/urgent queue mà không đo đạc.

## Hệ quả cho W2

- Một deadline timer chỉ quyết định thời điểm callback expiry được tạo; nó **không** bảo đảm thứ tự state transition với RESULT/END/ABORT.
- `ReentrantLock` tránh data race nhưng không tạo keyed mailbox, không admission control và không chống head-of-line blocking giữa dialog khác nhau.
- Message scheduler phải giữ tất cả events của một dialog (inbound component, outbound command, timer expiry) vào cùng một serial owner/mailbox; policy chỉ chọn key/job nào chạy tiếp theo.
- MAP/CAP semantics và callback reentrancy hiện là local/synchronous. Một scheduler bất đồng bộ thay đổi thread, latency và error timing, nên cần compatibility test.

## Existing benchmark base

Repository có `test/tcap-benchmark`, dùng JMH 1.17.3 (`test/tcap-benchmark/pom.xml:18-35`), nhưng source/target Java 7 trong POM (`:20`, `:68-71`) trong khi root hiện compile Java 25 (`pom.xml:42-46`). W2 benchmark cần hoặc hiện đại hóa isolated benchmark module, hoặc tạo harness/TestNG phù hợp root build, trước khi dùng làm performance gate.
