# MAP USSD Load Test

End-to-end SS7 stack load test: SCTP → M3UA → SCCP → TCAP → MAP (USSD).
Client sends `ProcessUnstructuredSSRequest`, server responds, client closes dialog.
Each dialog = 1 transaction (TPS measured as new dialogs/second).

## Quick Start

### Prerequisites

- **Java 25** (`java -version` must say `25`)
- **SCTP kernel module**: `lsmod | grep sctp` — if empty, run `sudo modprobe sctp`
- **Maven 3.9+** and **Ant** installed

### 1. Build (first time only)

```bash
cd jSS7/j25/map/load
ant -f ussd-server.xml compile
```

### 2. Run

**Terminal 1 — Server:**
```bash
cd jSS7/j25/map/load
ant -f ussd-server.xml server
```

Wait ~10 seconds for the SCTP→M3UA→SCCP→TCAP→MAP stack to boot.
You'll see a live ANSI TUI showing processed dialogs.

**Terminal 2 — Client:**
```bash
cd jSS7/j25/map/load
ant -f ussd-client.xml client
```

The default client profile runs for ~60 seconds (300,000 dialogs at 5,000 TPS).
A live TUI shows real-time TPS, success rate, memory, and peak TPS.
When complete, a final summary is printed.

### 3. Stop

```bash
ant -f ussd-server.xml clean
ant -f ussd-client.xml clean
```

Or just Ctrl+C in each terminal.

## Configuration

Edit properties at the top of `ussd-client.xml` (or override in `client.properties`):

| Property | Default | Description |
|----------|---------|-------------|
| `test.client.numOfDialogs` | `300000` | Total dialogs (60s × 5000 TPS) |
| `test.client.concurrentDialog` | `5000` | Guava RateLimiter permits/sec = target TPS |
| `test.client.rampUpPeriod` | `0` | 0 = instant start. >0 = ramp over N seconds |
| `test.client.deliveryThreads` | `16` | M3UA delivery thread pool |
| `jvm.xmx` | `512m` | Max heap |

**More aggressive test (5,000 TPS for 2 minutes):**
```xml
<property name="test.client.numOfDialogs" value="600000"/>
<property name="test.client.concurrentDialog" value="5000"/>
<property name="jvm.xmx" value="2g"/>
```

## W2 TCAP scheduler A/B test

The TCAP ingress scheduler is enabled by default. It has a bounded queue and a worker pool that processes **different SCCP flows in parallel** while preserving FIFO within a flow. It does not classify MAP operation codes yet, so this test measures isolation/parallelism rather than ATI-vs-MT-FSM priority.

Run W2 (default; choose workers no higher than available CPU initially):

```bash
cd /home/meodien/Desktop/ethiopia-working-dir/worktrees/jSS7/coral-valley/jSS7/map/load
ant -f ussd-server.xml server -Dtest.server.w2Workers=16 -Dtest.server.w2Capacity=100000
# or: ant -f mt-sms-server.xml server -Dtest.server.w2Workers=16 -Dtest.server.w2Capacity=100000
```

Run the Argona/FIFO baseline only when explicitly disabled:

```bash
ant -f ussd-server.xml server -Dtest.server.w2Scheduler=false
# or: ant -f mt-sms-server.xml server -Dtest.server.w2Scheduler=false
```

Keep client TPS, dialog count, delivery threads, JVM heap, peer and run duration identical between A/B runs. Record client successful/error dialogs and TPS, server CPU/heap/GC, TCAP timeout/abort logs, and `W2 TCAP ingress queue is full` warnings. A capacity warning means the run used inline fail-open processing and is not a pure queued-W2 comparison; increase capacity or reduce offered load. Confirm startup log `W2 TCAP ingress scheduler enabled` before treating a run as W2.

## TUI Display

The ConsoleTui writes to **stderr** (keeps stdout clean for CSV output):

```
╔══════════════════════════════════════════════════════════════╗
║  MAP USSD Load Test — CLIENT                               ║
╚══════════════════════════════════════════════════════════════╝

 ⏱ 00:37    │    TPS:  1,042    │    Peak:  1,150    │    Target: 1,000 (104%)
 
 📊 Dialogs
    Created:    38,520    Success:    38,450    Error:        70
    Rate:          99.8%    Pending:     1,978

 💻 System
    Heap: 234M/512M    Threads: 42    Uptime: 00:37
```

At the end, a final summary is printed.


### ASN.1 Telemetry

By default, ASN.1 observatory counters (`activeCount`, `totalDecodedCount`) are **disabled**
to avoid ~3ns/op overhead on the hot decode path. This improves decode throughput by ~42%
(from 3.27M → 4.65M ops/s).

**To enable telemetry for monitoring:**
```bash
ant -f ussd-server.xml server -Dasn.telemetry.enabled=true
ant -f ussd-client.xml client -Dasn.telemetry.enabled=true
```

The TUI ASN.1 section shows `🔒 Telemetry off` when disabled,
or real-time active cursors + total decoded when enabled.

## Understanding Stack Overhead

The ASN.1 codec alone processes ~3.86M messages/sec (single-thread).
The full SS7 stack adds layers that consume CPU:

| Layer | What it does | Cost factor |
|-------|-------------|-------------|
| **ASN.1** (BerCursor) | TLV decode/encode | **1×** (baseline) |
| **TCAP** | Dialog state machine, invoke/result tracking, timeout scheduling | +0.5–1× |
| **SCCP** | GT routing, SSN dispatch, segmentation | +0.3–0.5× |
| **M3UA** | ASP/AS state, sequence numbering, traffic mode | +0.5–1× |
| **SCTP** | Association management, congestion control, kernel I/O | +0.5–1× |
| **MAP** | Application context, opcode dispatch, field getters | +0.2–0.5× |

**Total stack overhead = ~2–4× the codec cost.**

This means:
- **2× overhead** (optimistic): If codec = 3.86M ops/s, stack = ~1.93M TPS/thread  
- **4× overhead** (conservative): If codec = 3.86M ops/s, stack = ~965K TPS/thread

**To achieve 1M TPS:**
- At 2× overhead: ~1 thread → 1.93M TPS ✅
- At 4× overhead: ~3 threads → 2.9M TPS ✅

The actual overhead depends on your hardware (CPU speed, SCTP kernel efficiency),
payload size, and thread configuration. Run this load test to measure your real numbers.

### Where the overhead comes from

```text
On the wire:  SCTP packet [M3UA DATA [SCCP UDT [TCAP BEGIN [MAP invoke(ussdString)]]]]
                ↑~~~~~~~~~~↑~~~~~~~~↑~~~~~~~~~↑~~~~~~~~~~~↑~~~~~~~~~~~~~~~~~~~~~~~↑
                SCTP        M3UA     SCCP      TCAP       MAP                       ASN.1
                
Each layer parses its header, dispatches to the next, and the inner payload
(ASN.1 MAP message) is what BerCursor decodes. The outer layers are thin
but add context switches (kernel I/O, thread pools, timer wheels).
```

## Files

```
map/load/
├── ussd-server.xml          # Ant build: server (run me first)
├── ussd-client.xml          # Ant build: client (editable TPS config)
├── server.properties        # Optional overrides for server
├── client.properties        # Optional overrides for client
├── ant-classpath.txt        # Generated by Maven (auto-refreshed on compile)
├── log4j2-server.xml        # Server logging config
├── log4j2-client.xml        # Client logging config
├── src/main/java/.../
│   ├── ConsoleTui.java      # ANSI escape live TUI (zero-dependency)
│   ├── Counter.java         # Thread-safe counter
│   ├── CsvWriter.java       # CSV metrics output
│   └── ussd/
│       ├── TestHarnessUssd.java  # Shared base class
│       ├── Server.java           # SS7 stack server
│       └── Client.java           # Load generator client
└── server/ client/          # SCTP/M3UA XML configs (legacy)
```

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `SCTP not supported` | `sudo modprobe sctp` |
| `Address already in use` | `ant -f ussd-server.xml clean` then wait 5s |
| `UnsupportedClassVersionError` | Wrong Java version — need Java 25 |
| Server doesn't start | Check `server.log`, verify SCTP: `ss -lntp \| grep 8011` |
| Client can't connect | Server must be fully booted (~10s). Check `ss -lntp \| grep 8011` |
| Low TPS | Increase `deliveryThreads`, `jvm.xmx`, or reduce `concurrentDialog` |
| TUI not showing | Must run in a real terminal (not IDE console, not piped) |
