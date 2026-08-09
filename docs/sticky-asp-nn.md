# Sticky ASP (N–N) + NI/GTT peer-route LB

**Topology:** 1 M3UA AS with **N ASPs** (N–N). Digicom example: AS-BP + L1-1404 + L2-1403, but **N is not limited to 2**. Not active-standby, not OVERRIDE single-primary, not A-A pair-only.

## A) Mid-dialog sticky ASP (jSS7)

| Step | Behaviour |
|------|-----------|
| Ingress | `TransferMessageHandler` tags `Mtp3TransferPrimitive.preferredAspName` with the receiving ASP |
| SCCP/TCAP | Carried on `SccpMessage` → `Dialog.preferredAspName` on TC-BEGIN (alongside sticky `remotePc`) |
| Egress | `AsImpl.write` prefers bound ASP when ACTIVE (bypasses SLS loadshare) |

**Failover (N–N):** default = preferred down → SLS among **remaining ACTIVE of N**. Opt-in fail-closed:

```text
-Dorg.restcomm.protocols.ss7.m3ua.preferredAsp.failClosed=true
```

**Enable:** no flag for sticky itself — set when ingress ASP is captured (automatic) or when NI/GTT pins after LB (`Dialog.setPreferredAspName` / `setRemotePc`).

## B) New-session NI/GTT LB (jainslee-cluster)

`Ss7PeerRouteAffinity` on cache `ss7-peer-route-lb` (same `ClusterManager` / ADR 0001 fabric):

1. Pick one of **N** `PeerRoute(aspName, peerPc)` via ISPN round-robin (+ optional affinity pin by corrId/MSISDN).
2. Caller pins onto the new TCAP dialog → subsequent messages sticky via (A).

```java
var affinity = new Ss7PeerRouteAffinity(clusterManager);
var route = affinity.pickAndPin("ni:networkId=0", corrId, candidates); // N≥1
dialog.setPreferredAspName(route.aspName());
dialog.setRemotePc(route.peerPc());
```

## USSDGW wire (follow-up)

USSDGW NI push / GTT path should call `Ss7PeerRouteAffinity` when `ClusterManager` is bound, then pin on the MAP/TCAP dialog before first outbound. Until wired: stack sticky-on-ingress works for MO; new NI without pin still uses classic SLS among ACTIVE ASPs.

## Tests

- jSS7 `SgFSMTest.testTwoAspInAsLoadshare` — sticky preferred ASP sends all SLS on one ASP
- `Ss7PeerRouteAffinityTest` — N=3 RR + affinity pin
