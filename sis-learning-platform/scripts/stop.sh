#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
for service in sis-domain-service sis-knowledge-service frontend; do
 if [ -f "data/$service.pid" ]; then
  pid=$(cat "data/$service.pid")
  if [[ "$pid" =~ ^[0-9]+$ ]] && kill -0 "$pid" 2>/dev/null; then
   command_line=$(ps -p "$pid" -o args=)
   if [[ "$command_line" == *"$service-0.1.0.jar"* || ( "$service" = frontend && "$command_line" == *"npm run dev"* ) ]]; then kill "$pid"; fi
  fi
  rm "data/$service.pid"
 fi
done
docker compose stop
