#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
project_root=$PWD
bash scripts/configure.sh
set -a
source .env
set +a
export JAVA_HOME=${JAVA_HOME:-/workspace/tools/jdk-21}
export GRADLE_USER_HOME=${GRADLE_USER_HOME:-/workspace/tools/sis-gradle}
java_bin="$JAVA_HOME/bin/java"
java_trust=()
if [ -f /etc/ssl/certs/java/cacerts ]; then java_trust=(-Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts); fi
test -x "$java_bin"
docker compose up -d --wait
mkdir -p data
for service in sis-domain-service sis-knowledge-service; do
 if [ "$service" = sis-domain-service ]; then port=8091; profile=${SIS_DOMAIN_PROFILE:-${SIS_PROFILE:-offline-demo}}; else port=8092; profile=${SIS_KNOWLEDGE_PROFILE:-${SIS_PROFILE:-offline-demo}}; fi
 if ! curl -fsS "http://127.0.0.1:$port/actuator/health" >/dev/null 2>&1; then
  test -f "backend/$service/build/libs/$service-0.1.0.jar"
  (cd "backend/$service"; nohup "$java_bin" "${java_trust[@]}" -jar "build/libs/$service-0.1.0.jar" --spring.profiles.active="$profile" > "$project_root/data/$service.log" 2>&1 & echo $! > "$project_root/data/$service.pid")
 fi
 for attempt in $(seq 1 60); do
  if curl -fsS "http://127.0.0.1:$port/actuator/health" >/dev/null 2>&1; then break; fi
  if [ "$attempt" = 60 ]; then echo "Service $service failed. Inspect data/$service.log"; exit 1; fi
  sleep 1
 done
done
if ! curl -fsS http://127.0.0.1:5174 >/dev/null 2>&1; then
 (cd frontend; nohup npm run dev > "$project_root/data/frontend.log" 2>&1 & echo $! > "$project_root/data/frontend.pid")
fi
for attempt in $(seq 1 20); do
 if curl -fsS http://127.0.0.1:5174 2>/dev/null | python3 -c 'import sys; assert "id=\"root\"" in sys.stdin.read()'; then break; fi
 if [ "$attempt" = 20 ]; then echo 'Frontend readiness failed; inspect data/frontend.log'; exit 1; fi
 sleep 1
done
printf 'Domain, knowledge and frontend readiness checks passed.\n' 
