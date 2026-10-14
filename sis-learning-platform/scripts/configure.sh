#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if [ ! -e .env ]; then
 umask 077
 printf 'SIS_JWT_SECRET=%s\nSIS_DEMO_PASSWORD=%s\nSIS_DB_PASSWORD=%s\n' "$(openssl rand -hex 32)" "$(openssl rand -hex 12)" "$(openssl rand -hex 24)" > .env
fi
printf 'Local configuration exists in .env; values are not printed.\n'
