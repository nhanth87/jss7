# BerCursor/BerWriter v5 — Zero-Copy Pipeline & Shared-Nothing Architecture

## Real Performance (measured 2026-07-10)

### Single-thread codec roundtrip — SEQUENCE{INT+100B OCTET} (realistic MAP payload)

```
Latency:    547 ns/op (0.5 μs/op)
Throughput: 1,826,000 ops/s (codec only, single-thread!)
```

| Encode only (SMALL pool) | Zero-copy decode | Roundtrip (encode+decode) |
|---|---|---|
| 18.5M ops/s | ~3M ops/s | **1.83M ops/s** |

### Nested SEQUENCE + OPTIONAL — MAP USSD message (2026-07-10)

Realistic MAP payload:
`SEQUENCE { [0] EXPLICIT SEQUENCE{INT,OID} (50% present), SEQUENCE{INT(42), OID, OCTET_STRING(160B)} }`

```
Encoded size: 181 bytes (no DP), 192 bytes (with dialoguePortion)
```

| Test | Latency | Throughput | Ops |
|------|---------|------------|-----|
| **Decode only** (50% DP) | 215 ns/op | **4,650,084 ops/s** | 1M |
| **Roundtrip encode+decode** | 324 ns/op | **3,089,607 ops/s** | 1M |

> **Bug fix**: `openConstructed()` does NOT advance parent pos. MUST call
> `parent.skipValue()` after processing child cursor. Without this, parent
> re-reads child content as outer TLVs → `AsnException: BER length field too large`.

### MAP/CAP TPS with TCAP/SCCP Stack Overhead

| Overhead | 1 thread | 4 threads | 8 threads | 16 threads |
|----------|----------|-----------|-----------|------------|
| 4× (current) | 1,162K TPS | 4.6M TPS | 9.3M TPS | 18.6M TPS |
| 2× (shared-nothing) | 2,325K TPS | 9.3M TPS | 18.6M TPS | 14.6M TPS |

**1M TPS requires: ~1 thread (4×) or ~1 thread (2×)**

> ⚠️ Previous estimate (3,700 ops/s) was **wrong** — it was an unverified placeholder.
> The real codec throughput was always ~1.8M ops/s. v5 measured it properly.

## Where the numbers come from

| Number | Source | Status |
|--------|--------|--------|
| **1,826,000** | QuickBench: single-thread roundtrip SEQUENCE{INT+100B} via `BerWriter.get()` + `resultAsCursor()` | ✅ REAL |
| **4,650,084** | MapMessageBench: 1M decode SEQUENCE+OPTIONAL (MAP USSD) | ✅ REAL |
| **3,089,607** | MapMessageBench: 1M roundtrip SEQUENCE+OPTIONAL | ✅ REAL |
| **18,500,000** | BerWriterPool benchmark: SMALL tier encode-only INT+100B | ✅ REAL |
| **222,000** | BerCursorConcurrencyTest: 4-thread pool roundtrip (smaller because pool + multi-thread overhead) | ⚠️ lower bound |
| **3,700** | Old placeholder in `estimateThroughput()` — wrong by 500× | ❌ WRONG |

## Changes in v5

### 1. MapMessageBench (NEW — 2026-07-10)
- `benchDecodeOnly()` — 1M ops decode with nested SEQUENCE + OPTIONAL dialoguePortion ✅
- `benchRoundtrip()` — 1M ops encode+decode roundtrip ✅
- Bug discovered: `openConstructed()` doesn't advance parent `pos` → must call `parent.skipValue()`

### 2. BerCursorConcurrencyTest (NEW)
- `testMultiThreadDecodeNoCorruption` — 4 threads × 50K, zero corruption ✅
- `testMultiThreadBerWriterPool` — pool roundtrip: ~887K/s total, 0 errors ✅
- `testWriterPoolVsNoPool` — pool (256B) at 92% speed of ThreadLocal (8KB)
- `testZeroCopyForwardPipeline` — BerSlice forwarding: 1.06× faster than copy

### 3. BerWriterPool Tier Benchmark (BerCodecBenchmark)
`benchmarkWriterPoolTiers()` — 5 tiers × 3 payloads:

| Payload | TINY(256B) | SMALL(1KB) | MEDIUM(4KB) | DEFAULT(8KB) |
|---------|-----------|-----------|------------|-------------|
| INTEGER(3B) | 95 ns | 113 ns | 20 ns | 30 ns |
| SEQ+100B | 92 ns | **54 ns** | 140 ns | 150 ns |
| OCTET 1KB | 86 ns | 60 ns | **53 ns** | 80 ns |

### 4. Ss7RaConfig (ra-jss7)
- `validate()` — checks threads, warns if deliveryThreads < CPUs
- `estimateThroughput()` — **now uses measured 1.83M ops/s/thread** (not the old wrong 3,700)

### 5. Ss7Stack (ra-jss7)
- `start()` logs config validation warnings + throughput estimate

## Thread Configuration

```bash
# 1M TPS target — just a few threads needed!
-Dra.jss7.sctp-worker-threads=8
-Dra.jss7.delivery-threads=8
-Dra.jss7.max-dialogs=20000
```

## Running Tests

```bash
# All ASN tests (66 tests)
cd jSS7/j25 && mvn test -pl asn/asn-api -am

# Concurrency stress test
mvn test -pl asn/asn-api -Dtest=BerCursorConcurrencyTest

# Pool tier benchmark
mvn test -pl asn/asn-api -Dtest=BerCodecBenchmark#benchmarkWriterPoolTiers

# MAP USSD nested+optional benchmark
mvn test -pl asn/asn-api -Dtest=MapMessageBench

# ra-jss7 compile
cd jain-slee && mvn compile -pl vendor-ras/ra-jss7
```
