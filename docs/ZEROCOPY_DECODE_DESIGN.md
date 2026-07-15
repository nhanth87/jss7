# Zero-Copy Lazy Decode / Single-Pass Encode — Design (jSS7 j25)

Status: **partially implemented.** Resolves the retention-scope, encode-asymmetry,
and storage questions. See `AGENTS.md` and the BerCursor work
(`asn/asn-api/.../BerCursor.java`, `BerWriter.java`).

**Implemented (2026-07-12):**
- **BerCursor decode wired for the SMSC/USSD hot path** (14 MAP messages) with a safe
  fallback to the classic `AsnInputStream` object-tree. `MapBerSupport` = the wiring
  contract (`useBer`/`absOffset`/`logFallback`, heap-backed + toggle gate, snapshot read,
  fallback on any failure or optional-field present). Gated by
  `-Djss7.asn.berCursorEnabled` (default true).
- `OctetStringBase.setData(backing,offset,length)` / `SmsSignalInfoImpl.setData(...)` —
  the decode path copies the octet value out of the BerCursor snapshot immediately
  (`Arrays.copyOfRange`). `getData()` is a plain getter.
- `DialogImpl` inbound-payload retention scaffolding was **removed** (reverted to HEAD):
  never wired, and pointless once views are gone (nothing aliases the buffer).

**View mechanism removed (2026-07-12, decided).** The earlier lazy `(backing,offset,length)`
view + lazy-materialize `getData()` was dropped. Analysis showed it was **deferred-copy, not
true zero-copy**: every read/encode path (`encodeData`, `equals`, `hashCode`, `getString`,
`decodeTpdu`) calls `getData()`, and the common relay path re-encodes → copy anyway. True
zero-copy relay would need relay-splice encode (step 5, `BerSlice.asByteBuf()`) which was
never wired. So the view only added the §4 lifetime hazard (a view escaping to
Infinispan/persist = read-after-free) with no real-traffic copy savings. Copying at decode is
simpler, has no lifetime rule, and performs the same. **The decode speedup lives in BerCursor
(no object-tree alloc), not in the view** — that is kept.

Verified byte-for-byte after removal: simple USSD APDU + 4 real MoForwardSM vectors
(simple/noDaOa hot path + complex/full fallback) decode identically on BerCursor-on and
legacy-off paths and re-encode to the original bytes.

**Remaining (only if a true zero-copy relay is later justified by profiling):** relay-splice
encode via `BerSlice.asByteBuf()` (step 5) — and only then reconsider a ByteBuf-slice octet
holder (the corsac-jss7 / Mobius approach, see [[jss7-mobius-corsac-asn]]). `DialogImpl`
retention wiring is moot without views.

## 0. Measured motivation (JDK25, real Impl + micro, best-of-5)

| Direction | Speedup vs old stack | Where | Alloc |
|-----------|----------------------|-------|-------|
| **Decode** (BerCursor) | **10–15.6x** | shallow + large octet (USSD/SMS TPDU ≥~1.5KB), real `ProcessUnstructuredSSRequestImpl` | **0** vs 110–4184 B/msg |
| Decode | 1.3–3x | small / deep-nested messages | 0 vs 108–265 B |
| **Encode** (BerWriter, backward one-pass) | **12–112x** | deeply nested (subscriber-data, routing) | ~0 vs 272 B/msg |
| Encode | ~1x | shallow messages (AsnOutputStream already fast) | ~0 |

The two ≥10x drivers are **mutually exclusive by message shape** (decode wins on
shallow-big-octet; encode wins on deep-nesting), so no single message is ≥10x both.
The universal wins are: **zero allocation both directions** (throughput/GC), and the
fixed 100x decode regression. Prior state: [[jss7-j25-bercursor-optimization]].

## 1. Retention scope — retain at TCAP (the SCCP `data` byte[])

**Fact:** `SccpDataMessage.getData()` returns **`byte[]`** (not a Netty ByteBuf);
`TCAPProviderImpl.onMessage` reads `byte[] data = sccpDataMessage.getData()`. SCCP
already materialized the TCAP payload into a standalone array.

**Decision:**
- **Decode SCCP addresses (GT/PC/SSN) eagerly** — SCCP must parse them for GTT
  routing before handoff; they are small, copy cost negligible.
- **Retain a reference to the SCCP `data` byte[]** = the TCAP APDU. `dialogId`,
  dialog portion, and MAP operation params (IMSI, USSD-String, SMS-TPDU, …) are
  **offset/length views** into this array — no further copy.
- **Do NOT** retain the raw M3UA/SCCP Netty ByteBuf.

**Consequence — the scary part disappears:** because the retained buffer is a plain
`byte[]` (not a pooled ByteBuf), there is **no refcount, no release, no
use-after-free**. GC reclaims it with the dialog. (Precondition to verify once: SCCP
allocates a *fresh* `data` array per message — standard jSS7 behaviour; if it ever
pools that array, retention must copy-on-store.)

**Deferred (bigger project):** true wire-to-app zero-copy (ByteBuf threaded through
the SCCP message API, eliminating the one ByteBuf→byte[] copy in SCCP). Only pursue
if profiling shows that single copy dominates.

## 2. Encode is NOT symmetric with decode

Decode views pre-existing bytes → zero-copy. Encode has **no pre-existing buffer**:
the app constructs values (IMSI digits, USSD string, TPDU, GT/number) and must
**write** them out. Zero-copy in the decode sense does not apply.

**Decisions:**
1. **Single-pass backward write (BerWriter) straight into the outbound ByteBuf**,
   with reserved header room for SCCP/M3UA/SCTP (see `NettyAsnOutputStream`,
   `TcapOutboundEncoder`). No intermediate nested `AsnOutputStream` + per-level
   copy — this is the 12–112x nested-encode win. App value objects are small and
   written once; that is inherent, not waste.
2. **Lazy view objects on decode:** `USSDStringImpl` / `SmsSignalInfoImpl` hold
   `(byte[] backing, int offset, int length)` instead of a copied array. Getters
   materialize only when called, copy only when the value must escape (§4). Pattern
   already exists on master (`decodeFromOctetView` / `setDataView` in
   `MapFlatAsnDecoder`) — port it.
3. **Relay = zero-copy for encode too:** SMSC / USSD-GW that forward a received
   field (TPDU relay, echo MSISDN into the response) should **splice the inbound
   `byte[]` slice directly into the outbound buffer**, skipping decode-to-object
   and re-encode entirely. This is the encode "10x" that is real for relay traffic.

## 3. Storage of the retained buffer

- **Primary: a `byte[]` field on `DialogImpl`** (the dialog owns its inbound
  payload). GC'd at dialog end. For multi-message dialogs (Begin→Continue→End) hold
  it until End/Abort (~200 B per open dialog — negligible). `DialogImpl` has no such
  field today; add one.
- **Global registry (only if needed — preview mode, executor lookup by dialogId):**
  **Agrona `Long2ObjectHashMap<byte[]>`** — primitive `long` key (zero boxing),
  open-addressing, cache-friendly. **Agrona is already a dependency** (`scheduler`
  module); add it to `tcap`.
- **NOT** `HashMap<Long,…>` (key boxing + needs synchronization).
- **NOT** LMAX Disruptor — that is an inter-thread **event ring** (correct for the
  *inbound pipeline* SCTP→decode→worker hand-off, a different concern), **not** a
  keyed store. Disruptor is not in the repo; jctools (present) covers pipeline
  queues if needed.

## 4. The rule implementation MUST enforce — view vs materialize

The lifetime risk with `byte[]` is gone, but **escape** is not:

- **Two-tier field API:** `getUssdStringView()` (zero-copy `BerSlice`/offset view,
  valid only within the processing scope) vs `getUssdString()` (materialized copy,
  safe to keep). Default getters materialize; explicit `*View()` for hot paths.
- **Clustering boundary (ties into the Infinispan HA work):** any dialog/app state
  that is **replicated (Infinispan) or persisted** MUST materialize field values —
  a view pointing into a node-local `byte[]` cannot cross nodes or outlive the
  request. Views are for **within-node, within-request** processing only.

## 5. Implementation order

1. `DialogImpl`: retain the inbound `byte[]` (field + set on Begin/Continue decode,
   clear on End/Abort).
2. View-backed primitives: `USSDStringImpl`, `SmsSignalInfoImpl` gain
   `(backing, offset, length)` + `setView(...)`; getters materialize lazily.
3. Wire `BerCursor` into the USSD (`ProcessUnstructuredSSRequestImpl`) and SMS
   (`MoForwardShortMessageRequestImpl`) decode paths, using views.
4. Two-tier getter API + mark the clustering materialization points.
5. Encode: relay-splice path for the gateway/relay case; ensure MAP/TCAP encode uses
   `BerWriter`→outbound ByteBuf (no nested `AsnOutputStream`).

Each step is independently testable against the existing MAP decode/encode unit
tests (byte-for-byte round-trip must still pass).
