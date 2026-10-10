#!/usr/bin/env bash
set -euo pipefail
cd /workspace/mo
if [ ! -f .env ]; then
  (umask 077; printf 'POSTGRES_PASSWORD=%s\n' "$(openssl rand -hex 24)" > .env)
fi
# .env is local trusted configuration; never commit it or print its values.
set -a
source .env
set +a
docker compose up -d db
export DATABASE_URL=jdbc:postgresql://localhost:5432/employees
export DATABASE_USERNAME=employees
export DATABASE_PASSWORD="$POSTGRES_PASSWORD"
export CORS_ORIGINS=http://localhost:5173
if ! curl --fail --silent http://localhost:8080/actuator/health > /dev/null; then
  nohup /workspace/tools/jdk-17/bin/java -jar backend/target/employees-1.0.0.jar > /tmp/backend-server.log 2>&1 &
  echo $! > /tmp/employee-backend.pid
fi
if ! curl --fail --silent http://localhost:5173 > /dev/null; then
  (cd frontend; nohup npm run dev > /tmp/frontend-server.log 2>&1 & echo $! > /tmp/employee-frontend.pid)
fi
python3 scripts/smoke.py
curl --fail --silent http://localhost:5173 > /dev/null
printf 'Backend and frontend are responding. Logs: /tmp/backend-server.log and /tmp/frontend-server.log\n'
