# Đánh Giá JCTools vs Javolution vs java.util trong jSS7

> **Updated:** 2026-08-04 (Phase 3 main complete; test XML → Jackson shim)

## Thống Kê Tổng Quan

### 1. JAVOLUTION

| Scope | Status |
|-------|--------|
| Parent BOM / main deps | **REMOVED** — do not re-add |
| `import javolution` under `src/main` | **0** |
| `import javolution` under `src/test` | ~186 files (XMLObjectReader/Writer) |
| Mitigation | Module `ss7-test-xml-support` — Jackson-backed classes in package `javolution.xml` (transitional). Prefer `SCCPJacksonXMLHelper` / module Jackson helpers (see `GT0001Test`). |

### 2. JCTOOLS — ĐANG DÙNG (hot path)

| Class | Mục đích |
|-------|----------|
| NonBlockingHashMap | Dialog / congestion maps |
| MpscArrayQueue | Payload pools |

Pinned in BOM: JCTools **4.0.5**, Agrona **1.23.1**.

### 3. JACKSON XML

Persist / config path uses Jackson XML (e.g. `SCCPJacksonXMLHelper`). Integer map keys stored as `<kN>` (never illegal `<N>`).

## Phase status

| Phase | Status |
|-------|--------|
| 1–2 Java 25 + test modernization | Done |
| 3 Main javolution purge | Done |
| 3 Test XML migration | Shim landed; migrate callers to Jackson helpers over time |
| 4 VT + ZGC | Checklist: [`docs/vt-zgc-audit.md`](docs/vt-zgc-audit.md) |
| 5 RA | micro-jainslee `vendor-ras/ra-jss7` |

## Ghi chú hiệu năng

- Prefer JCTools / Agrona over `ConcurrentHashMap` only where contention profiles justify it.
- Off-heap dialog leases: versioned bytes (not Java serialization).
- Do not claim production multi-ASP TCAP HA until lab soak passes (micro-jainslee ADR 0001).
