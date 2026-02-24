# jSS7

jSS7 is an open-source Java implementation of an **SS7 signaling stack**, enabling Java applications to communicate with legacy SS7 network elements and SIGTRAN-based infrastructures.

The project provides protocol-level support for both traditional SS7 deployments and IP-based signaling, and is designed to be embedded as a core signaling component within telecom platforms.

This repository is maintained and published as open source by **PAiCore Technology**.  
For more information, visit **https://paicore.tech**.

---

## Supported Protocols

jSS7 is the only Open Source Java based implementation of the SS7 protocol stack. It provides implementations for the protocols of the SS7/SIGTRAN stack:

- MTP2
- MTP3
- ISUP
- M3UA (SIGTRAN over IP)
- SCCP
- TCAP
- MAP
- CAP
- INAP

The stack supports integration with IP-based SS7 signaling environments (SIGTRAN), and can be used with Intel family signaling boards (Dialogic SS7 cards) or Zaptel/Dahdi compatible TDM devices (Digium, Sangoma).

---

## Build Prerequisites

- Java 11 (OpenJDK recommended)
- Maven 3.6.x or later
- Ant 1.10.x or later

---

## Build from Source

```bash
git clone https://github.com/paicoretech/jss7.git
cd jss7
mvn clean install
```

---

## Usage

jSS7 can be used as a **protocol stack or library** within telecom systems, or operate as a standalone application running over WildFly application server.

---

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**.  
See the [LICENSE](LICENSE) file for details.
