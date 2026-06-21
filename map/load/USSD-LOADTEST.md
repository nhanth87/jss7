# MAP USSD Load Test — Multi-Menu + Adaptive Timeout

Aligns jSS7 `map/load` with `ussdgateway` demo config and the gRPC Python tester menu tree.

## Demo alignment (must match ussdgw)

| Parameter | Value |
|-----------|-------|
| SCTP | client `127.0.0.1:8011` → GW `127.0.0.1:8012` |
| M3UA RC / NA | 101 / 102 |
| OPC / DPC | 1 / 2 |
| USSD SSN | **8** (not 147) |
| MSC / HLR SSN | 8 / 6 |
| Default short code | `*100#` (gRPC) or `*519#` (HTTP demo) |
| Menu tree | `src/main/resources/menu_config.json` (same as `ussdgateway/tools/grpc-as-tester/menu_config.json`) |

## Build

```bash
cd jSS7/map/load
ant -f ussd_build.xml clean assemble
# or
cd jSS7/map && mvn install -N -DskipTests && cd load && mvn clean package -Passemble -DskipTests
```

Output: `target/load/map-load.jar` + dependencies.

## Run against USSD Gateway

1. Start ussdgw (Docker or WildFly) with SCTP 8012.
2. Add scrule for gRPC: `*100#` → `127.0.0.1:8443`.
3. Start gRPC AS: `python3 ussd_as_server.py --port 8443 --menu-config menu_config.json`
4. Enable bridge (optional): `sessionbridgeenabled=true`, `asyncgatetimeoutms=7000`.
5. Run MAP load client:

```bash
cd jSS7/map/load
ant -f ussd_build.xml client
# or direct java with profile + think delay:
java -cp "target/load/*" org.restcomm.protocols.ss7.map.load.ussd.Client \
  1000 50 sctp 127.0.0.1 8011 -1 127.0.0.1 8012 IPSP 101 102 1 2 3 2 8 6 8 \
  1111112 9960639999 1 16 -100 0 "*100#" RANDOM 50 200
```

### CLI args (extended)

| Pos | Name | Default |
|-----|------|---------|
| 24 | duration minutes | 0 (count mode) |
| 25 | USSD string | `*100#` |
| 26 | menu profile | `RANDOM` (`BALANCE`, `DATA`, `SUBSCRIBE`, `ADAPTIVE`) |
| 27 | think min ms | 0 |
| 28 | think max ms | 0 |

**Think delay** (args 27–28): random sleep before each user digit — exercises gateway EWMA/adaptive gate with multi-turn dialogs without overrunning TCAP timeout.

**ADAPTIVE profile**: same menu paths as RANDOM; pair with think delay 50–500 ms and gRPC AS `--min-delay 1 --max-delay 100`.

## jSS7 MAP Simulator

```bash
cd jSS7
mvn clean install -pl tools/simulator -am -Dmaven.test.skip=true
cd tools/simulator/bootstrap/target/simulator-ss7/bin
chmod +x run.sh
./run.sh gui --name=main
```

Load config from `ussdgateway/core/bootstrap/src/main/config/ss7-simulator/main_simulator2.xml` into `data/main_simulator2.xml`.

## gRPC Python multi-menu load

```bash
cd ussdgateway/tools/grpc-as-tester
python3 loadtest_client.py --target localhost:8443 --tps 500 --duration 30 --multi-menu --profile BALANCE
```
