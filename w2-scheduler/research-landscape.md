# Research Landscape and Reference Register

**Last reviewed:** 2026-07-19. Link availability/citation metadata cần được kiểm tra lại khi dùng cho paper/publication.

## Research conclusion (carefully scoped)

Đã có nghiên cứu trực tiếp về **SS7 performance, queueing và congestion**, gồm TCAP trong mô hình mạng. Vì vậy không tuyên bố "SS7 chưa ai nghiên cứu". Trong tập nguồn đã kiểm tra, chưa thấy một paper public/peer-reviewed đề xuất đúng thiết kế runtime: **TCAP/MAP/CAP per-dialog serial ordering + deadline-aware priority + fair sharing + unknown-job-size/cost-aware scheduling + timer plane riêng**. Đây là negative-search result có giới hạn, không phải proof of nonexistence.

## Direct SS7 / standards

| ID | Citation and link | Relevance |
|---|---|---|
| SS7-1 | G. Willmann, P. J. Kühn (1990), *Performance Modeling of Signaling System No. 7*, IEEE Communications Magazine 28(7), 44–56. https://doi.org/10.1109/35.56241 | Direct SS7 model: MTP/SCCP/ISUP/**TCAP**, multiple message classes, priority processors, queueing-network and end-to-end delay. Baseline for evaluating W2 beyond local executor latency. |
| SS7-2 | D. R. Manfield, M. Zukerman (1992), *Analysis of Congestion Onset Thresholds for CCITT SS7 Networks*, GLOBECOM. https://doi.org/10.1109/GLOCOM.1992.276681 | Direct SS7 congestion threshold/queueing under overload. Supports bounded queues, hysteresis and admission controls. |
| SPEC-TCAP | ITU-T Q.771, Q.774, Q.775. https://www.itu.int/rec/T-REC-Q.771/en ; https://www.itu.int/rec/T-REC-Q.774/en ; https://www.itu.int/rec/T-REC-Q.775/en | TCAP semantics/timers/state behavior; normative review needed before policy changes. |
| SPEC-MAP | 3GPP TS 29.002 archive. https://www.3gpp.org/ftp/Specs/archive/29_series/29.002/ | MAP operation/dialog semantics. |
| SPEC-CAP | 3GPP TS 29.078. https://www.3gpp.org/ftp/Specs/archive/29_series/29.078/ | CAP application protocol semantics. |

## Scheduling foundations

| ID | Citation and link | W2 takeaway |
|---|---|---|
| EDF-1 | C. L. Liu, J. W. Layland (1973), *Scheduling Algorithms for Multiprogramming in a Hard-Real-Time Environment*. https://doi.org/10.1145/321738.321743 | EDF foundation; use slack/deadline but do not assume hard-real-time feasibility theorem applies to non-preemptive, downstream-dependent SS7 work. |
| EDF-2 | R. Atar, E. Shadmi (2020), *Fluid Limits for Earliest-Deadline-First Networks*. https://arxiv.org/abs/2009.07169 | Deadline networks under load; supports measuring feasible vs infeasible jobs. |
| EDF-3 | S. Das, D. Jenkins, S. Sengupta (2011), *Comparison of Loss Ratios of Different Scheduling Algorithms*. https://arxiv.org/abs/1101.1466 | Compare deadline/loss behavior rather than average latency only. |
| SJF-1 | H. Gromoll, J. Keutel (2010), *Invariance of Fluid Limits for SRPT and SJF*. https://arxiv.org/abs/1007.2469 | Short-job priority reduces response time when size is known/estimated; must protect long work. |
| SRPT-1 | H. C. Gromoll, L. Kruk, K. Puha (2010/11), *Diffusion Limits for SRPT Queues*. https://arxiv.org/abs/1005.1035 | SRPT theory; only analogy because SS7 cost is uncertain and non-preemptive. |
| UNKNOWN-1 | Z. Scully, I. Grosof, M. Harchol-Balter (2020), *Optimal Multiserver Scheduling with Unknown Job Sizes in Heavy Traffic*. https://arxiv.org/abs/2003.13232 | Unknown size needs attained-service/robust policy, not naïve operation labels. |
| UNKNOWN-2 | M. Akbari-Moghaddam, D. Down (2021), *SEH: Size Estimate Hedging for Single-Server Queues*. https://arxiv.org/abs/2101.00007 | Estimation error must be hedged/measured. |
| FQ-1 | A. Demers, S. Keshav, S. Shenker (1989), *Analysis and Simulation of a Fair Queueing Algorithm*. https://doi.org/10.1145/75246.75248 | Fair sharing/noisy-neighbor isolation for tenant/source/class lanes. |
| FQ-2 | S. Golestani (1994), *A Self-Clocked Fair Queueing Scheme for Broadband Applications*. https://doi.org/10.1109/INFCOM.1994.337677 | Practical virtual-time fair queueing reference. |
| FQ-3 | Y. You et al. (2022), *Hierarchical Multi-resource Fair Queueing for Packet Processing*. https://arxiv.org/abs/2208.06091 | Hierarchical fairness inspiration when CPU, downstream waits and tenant quotas interact. |
| TIMER-1 | G. Varghese, T. Lauck (1987), *Hashed and Hierarchical Timing Wheels*. https://doi.org/10.1145/37499.37504 | Foundation for scalable timer facility; separate from work dispatch. |

## Search ledger / next sources

Search terms to repeat quarterly: `TCAP queueing performance`, `MAP signaling overload scheduling`, `SS7 priority queue`, `telecom signaling deadline aware`, `TCAP timer wheel`, `per-key serial executor real time`.

For each discovered item, record: DOI/arXiv URL, access date, workload/model, whether it preserves flow/dialog order, whether service time is known, deadline type (hard/soft), fairness/admission mechanism, and reproducibility artifacts.
