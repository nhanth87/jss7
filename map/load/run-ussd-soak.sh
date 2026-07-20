#!/usr/bin/env bash
# Runs a local USSD FIFO soak and preserves evidence. Only PIDs started here are terminated.
set -Eeuo pipefail

BASE_DIR=$(cd "$(dirname "$0")" && pwd)
cd "$BASE_DIR"
RUN_ID="ussd-fifo-5ktps-10m-$(date +%Y%m%d-%H%M%S)"
RUN_DIR="$BASE_DIR/soak-artifacts/$RUN_ID"
mkdir -p "$RUN_DIR"
printf '%s\n' "$RUN_ID" > "$RUN_DIR/run-id"
printf 'RUNNING %s\n' "$(date -Is)" > "$RUN_DIR/outcome"
cp ussd-client.xml ussd-server.xml ss7-ussd-client.json ss7-ussd-server.json \
    log4j2-client.xml log4j2-server.xml "$RUN_DIR/"
# Never share the developer's default SCTP ports with a local Restcomm container.
# These files are run-local snapshots; source JSON remains untouched.
CLIENT_CONFIG="$RUN_DIR/ss7-ussd-client-soak.json"
SERVER_CONFIG="$RUN_DIR/ss7-ussd-server-soak.json"
sed -e 's/127.0.0.1:8012/127.0.0.1:18112/g' -e 's/127.0.0.1:8011/127.0.0.1:18111/g' \
    ss7-ussd-client.json > "$CLIENT_CONFIG"
sed -e 's/127.0.0.1:8011/127.0.0.1:18111/g' -e 's/127.0.0.1:8012/127.0.0.1:18112/g' \
    ss7-ussd-server.json > "$SERVER_CONFIG"

cleanup() {
    local signal=${1:-TERM}
    for pid_file in "$RUN_DIR/client-java.pid" "$RUN_DIR/server-java.pid"; do
        if [[ -f "$pid_file" ]]; then
            kill -"$signal" "$(cat "$pid_file")" 2>/dev/null || true
        fi
    done
}
trap 'cleanup TERM' EXIT

rm -f client.log server.log client.jfr server.jfr gc-client.log gc-server.log
# Override the launcher default only for this constrained local soak environment.
ant -f ussd-server.xml -Dtest.server.config="$SERVER_CONFIG" -Dtest.server.w2Scheduler=false \
    -Dtest.server.monitor=true -Djvm.xms=2g -Djvm.xmx=2g server \
    >"$RUN_DIR/server.console.log" 2>&1 &
SERVER_ANT_PID=$!
printf '%s\n' "$SERVER_ANT_PID" > "$RUN_DIR/server-ant.pid"

SERVER_JAVA_PID=''
for _ in $(seq 1 60); do
    SERVER_JAVA_PID=$(pgrep -n -f 'org\.restcomm\.protocols\.ss7\.map\.load\.ussd\.Server' || true)
    if [[ -n "$SERVER_JAVA_PID" ]] && grep -q 'Server ready' server.log 2>/dev/null; then
        break
    fi
    sleep 1
done
if [[ -z "$SERVER_JAVA_PID" ]] || ! kill -0 "$SERVER_JAVA_PID" 2>/dev/null; then
    printf 'SERVER_START_FAILED %s\n' "$(date -Is)" > "$RUN_DIR/outcome"
    exit 1
fi
printf '%s\n' "$SERVER_JAVA_PID" > "$RUN_DIR/server-java.pid"

ant -f ussd-client.xml -Dtest.client.config="$CLIENT_CONFIG" -Dtest.client.numOfDialogs=3000000 \
    -Dtest.client.concurrentDialog=5000 -Dtest.client.rampUpPeriod=0 -Dtest.client.monitor=true \
    -Djvm.xms=2g -Djvm.xmx=2g client >"$RUN_DIR/client.console.log" 2>&1 &
CLIENT_ANT_PID=$!
printf '%s\n' "$CLIENT_ANT_PID" > "$RUN_DIR/client-ant.pid"

CLIENT_JAVA_PID=''
for _ in $(seq 1 60); do
    CLIENT_JAVA_PID=$(pgrep -n -f 'org\.restcomm\.protocols\.ss7\.map\.load\.ussd\.Client' || true)
    if [[ -n "$CLIENT_JAVA_PID" ]]; then
        break
    fi
    sleep 1
done
if [[ -z "$CLIENT_JAVA_PID" ]] || ! kill -0 "$CLIENT_JAVA_PID" 2>/dev/null; then
    printf 'CLIENT_START_FAILED %s\n' "$(date -Is)" > "$RUN_DIR/outcome"
    exit 1
fi
printf '%s\n' "$CLIENT_JAVA_PID" > "$RUN_DIR/client-java.pid"
printf 'STARTED %s server=%s client=%s\n' "$(date -Is)" "$SERVER_JAVA_PID" "$CLIENT_JAVA_PID" > "$RUN_DIR/outcome"

TIMEOUT_PATTERN='onDialogTimeout|onInvokeTimeout|TCAP.*[Tt]imeout|[Tt]imeout.*TCAP'
END_AT=$((SECONDS + 660))
while ((SECONDS < END_AT)); do
    cp client.log "$RUN_DIR/client.log" 2>/dev/null || true
    cp server.log "$RUN_DIR/server.log" 2>/dev/null || true
    if grep -E -m1 "$TIMEOUT_PATTERN" "$RUN_DIR/client.log" "$RUN_DIR/server.log" > "$RUN_DIR/timeout-match.txt" 2>/dev/null; then
        printf 'TIMEOUT_DETECTED %s\n' "$(date -Is)" > "$RUN_DIR/outcome"
        cleanup TERM
        sleep 10
        break
    fi
    if ! kill -0 "$CLIENT_JAVA_PID" 2>/dev/null; then
        printf 'CLIENT_EXITED %s\n' "$(date -Is)" > "$RUN_DIR/outcome"
        break
    fi
    sleep 1
done
if [[ $(cat "$RUN_DIR/outcome") == STARTED* ]]; then
    printf 'SOAK_WINDOW_COMPLETE %s\n' "$(date -Is)" > "$RUN_DIR/outcome"
    cleanup TERM
    sleep 10
fi
for f in client.log server.log client.jfr server.jfr gc-client.log gc-server.log; do
    cp "$f" "$RUN_DIR/$f" 2>/dev/null || true
done
for pid_file in "$RUN_DIR/client-java.pid" "$RUN_DIR/server-java.pid"; do
    if [[ -f "$pid_file" ]]; then
        jcmd "$(cat "$pid_file")" VM.native_memory summary >"$RUN_DIR/$(basename "$pid_file" .pid)-nmt.txt" 2>&1 || true
    fi
done
trap - EXIT
cleanup TERM
printf 'DONE %s\n' "$(date -Is)" >> "$RUN_DIR/outcome"
