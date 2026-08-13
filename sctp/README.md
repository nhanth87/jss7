# jSS7 SCTP transport

The SCTP project lives in the sibling checkout `ethiopia-working-dir/sctp`
(same Maven coordinates `org.mobicents.protocols.sctp:*:2.27.32`).

| Artifact | Role |
|---|---|
| `sctp-api` | Mobicents `Management` / `Association` contract |
| `sctp-core` | Backend SPI, generation fence, bounded rings |
| `sctp-backend-fstack` | **Default.** Sidecar + GraalVM 25 FFM. No `com.sun.nio.sctp` |
| `sctp-native-fstack` | C ABI, sidecar binary, F-Stack enablement patch |
| `sctp-impl` | JVM-only Netty/JDK SCTP oracle |

GMLC native images must depend on `sctp-backend-fstack` only.
M3UA stays on `sctp-api`; it does not move into C++.

`Ss7StackBuilder` and the jSS7 simulator (`M3uaMan`) construct SCTP via
`SctpProvider` (default `FSTACK_DPDK`). Override with
`-Dsctp.backend=NETTY_KERNEL` or `-Dss7.sctp.impl=...NettySctpManagementImpl`
on a JVM-only oracle path.

Two JVMs (GMLC + simulator) share one `sctp-fstack` sidecar. LOOPBACK with a
live UDS socket pairs reversed 5-tuples and forwards M3UA PPID 3. Without a
socket, LOOPBACK stays in-process for unit tests.
