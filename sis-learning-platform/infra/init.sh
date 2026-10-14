#!/usr/bin/env bash
set -euo pipefail
psql -v ON_ERROR_STOP=1 --username postgres --dbname sis --set=pwd="$SIS_DB_PASSWORD" <<'SQL'
CREATE EXTENSION vector;
SELECT format('CREATE ROLE sis_migrator LOGIN PASSWORD %L', :'pwd') \gexec
SELECT format('CREATE ROLE sis_business LOGIN PASSWORD %L', :'pwd') \gexec
SELECT format('CREATE ROLE sis_knowledge LOGIN PASSWORD %L', :'pwd') \gexec
CREATE SCHEMA business AUTHORIZATION sis_migrator;
CREATE SCHEMA knowledge AUTHORIZATION sis_migrator;
GRANT USAGE ON SCHEMA public TO sis_migrator,sis_business,sis_knowledge;
SQL
