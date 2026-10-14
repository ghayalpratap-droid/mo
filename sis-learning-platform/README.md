# SIS / LLE Student Finance and Knowledge Platform

A runnable educational reconstruction based on supplied documentary evidence. This is an implementation checkpoint, not a claim of complete coverage or production source fidelity. Read [verification](docs/verification.md), [remaining scope](docs/checkpoint.md), [source assessment](docs/source-assessment.md) and [assumptions](docs/assumptions-and-gaps.md).

## Run in this cloud workspace

Prerequisites: Docker, Node 24.19.0, Python 3, Java 21 JDK. The cloud installer prepares the pinned JDK under /workspace/tools and configures Gradle to use the platform proxy/system trust without disabling verification.

```sh
cd /workspace/mo/sis-learning-platform
bash scripts/cloud-install.sh
SIS_PROFILE=integration bash scripts/start.sh
python3 scripts/seed.py
python3 scripts/verify.py
```

On a laptop with Java 21 and Node installed, run `./gradlew test :backend:sis-domain-service:bootJar :backend:sis-knowledge-service:bootJar`, `npm --prefix frontend ci`, `npm --prefix frontend test`, `npm --prefix frontend run build`, and `bash scripts/configure.sh`. Start with `JAVA_HOME=/path/to/jdk21 GRADLE_USER_HOME=/path/to/gradle-cache SIS_PROFILE=integration bash scripts/start.sh`.

Ports: portal 5174, domain 8091, knowledge 8092, PostgreSQL 5544 (loopback only). The onboarding UI does not provide browser preview links. Validate with local requests or the supplied browser test.

The ignored .env contains generated SIS_DEMO_PASSWORD, SIS_JWT_SECRET and SIS_DB_PASSWORD. Read the local file securely to sign in; do not paste values into chat or commit it. Accounts: admin, advisor-a, advisor-b, developer, support, reporter. Each uses the generated local password; roles and provider scope are server-controlled. All accounts and records are synthetic. See [security model](docs/security-model.md).

## Profiles

| Profile | Business | Knowledge | Models |
|---|---|---|---|
| offline-demo (default) | Persistent H2 file | PostgreSQL/pgvector | Visible deterministic simulation |
| local | Persistent H2 file | PostgreSQL/pgvector | Real OpenAI, requires configured key |
| integration | PostgreSQL business schema | PostgreSQL knowledge schema | Visible deterministic simulation |

H2 does not verify Oracle, PostgreSQL vector/JSONB, PL/SQL or AQ behavior. For PostgreSQL business with real OpenAI, set SIS_DOMAIN_PROFILE=integration and SIS_KNOWLEDGE_PROFILE=local. Stop existing services before changing profiles or rebuilt JARs; startup reuses responding processes.

## Activate real OpenAI

Supply SIS_OPENAI_API_KEY securely to the backend environment and allow api.openai.com. Optional model variables: SIS_CHAT_MODEL (default gpt-4o-mini), SIS_EMBEDDING_MODEL (default text-embedding-3-small, dimension 1536). Never use VITE_ variables for keys. The Java HTTP factory uses the HTTPS proxy and normal certificate verification. The live adapter compiles, but no key was present during this checkpoint, so real model calls and quality are unverified. After setup, `python3 scripts/live-model-smoke.py --allow-external-model` is an opt-in external-model check.

```sh
bash scripts/stop.sh
SIS_DOMAIN_PROFILE=integration SIS_KNOWLEDGE_PROFILE=local bash scripts/start.sh
python3 scripts/seed.py
```

Reingest every source after changing embedding model or dimension. The store filters model IDs, preventing old offline vectors from being used as real-model embeddings. A dimension change additionally requires a separately reviewed migration/index rebuild. External model failure should leave ordinary business APIs usable.

## Verify

```sh
./gradlew test
npm --prefix frontend test
npm --prefix frontend run build
python3 scripts/verify.py
python3 scripts/e2e.py
python3 scripts/evaluate.py
python3 scripts/export-contracts.py
```

For browser checks install Python Playwright and use system Chromium; this cloud machine already provides them. The browser script creates its own fictitious record and verifies portal intake, confirmations, CoC, refresh, streamed answer, citation, mobile width and denied provider access. Screenshots are /tmp/sis-desktop.png and /tmp/sis-mobile.png. `verify.py` requires the integration profile and the dedicated demo database; it temporarily adds a checksum-specific test constraint to verify failed-index rollback and removes it in finally. It does not target production.

Generated evidence is under evaluation/. Offline recall is a small fixture measurement, not live model quality. OpenAPI exports are docs/openapi-domain.json and docs/openapi-knowledge.json; authenticated runtime specifications are at path /v3/api-docs on each service.

## Persistence, shutdown and reset

`bash scripts/stop.sh` stops recorded application processes and Compose while retaining data. `SIS_PROFILE=integration bash scripts/start.sh` restarts them. PostgreSQL uses the named Compose volume; local H2 uses backend/sis-domain-service/data/business. Processes do not survive an environment snapshot. Cloud tasks are isolated: use existing checkouts and do not create Git worktrees unless explicitly requested.

To intentionally reset fictitious demo data, stop services, remove the project's Compose volume with `docker compose down -v`, and remove only the local H2 data directory if you used it. This permanently deletes those demo records; keep .env unless intentionally rotating all local secrets. Rerun start and seed afterward. No production deployment is included.

## Troubleshooting

Inspect data/sis-domain-service.log, data/sis-knowledge-service.log and data/frontend.log. A missing Java compiler needs the JDK, not only a JRE. A Gradle download failure may require the platform HTTPS proxy/system trust; cloud-install.sh configures these while preserving TLS verification. A stale version returns 409: refresh before a new command. Invalid CoCs return explicit REJECTED decisions; preview does not write. Missing AI sources cause abstention; source replacement/deletion invalidates old citations. A busy chat session permits only one active turn. If a process died during generation, stuck-turn recovery is still pending; use a new session and consult the checkpoint.

## Documentation

- [AI concepts](docs/ai-explained.md), [architecture](docs/architecture.md), [RAG](docs/rag-design.md), [tools](docs/tool-catalogue.md).
- [Source inventory](docs/entity-inventory.csv), [legacy mappings](docs/legacy-to-demo-mapping.md), [documentary dictionary](docs/data-dictionary.md), [implemented dictionary](docs/implemented-data-dictionary.md).
- [API contracts](docs/api-contracts.md), [flows](docs/end-to-end-flows.md), [24 diagrams](docs/diagrams.md), [version matrix](docs/version-matrix.md).
- [Demo walkthrough](docs/demo-script.md), [interview explanation](docs/interview-explanation.md), [traceability](docs/traceability.csv), [continuation checkpoint](docs/checkpoint.md).
