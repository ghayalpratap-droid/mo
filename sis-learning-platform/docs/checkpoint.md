# Continuation checkpoint

The complete master request is retained in PROJECT_REQUEST.md. This increment is runnable and substantially beyond a skeleton, but the full request is not complete. Do not regenerate the project or describe documentary candidates as implemented entities.

## Completed files and decisions

Source assessment, ambiguity register, 909-name candidate inventory, preserved source sections/dictionary, legacy mappings, Java 21 Gradle multi-project build/wrapper/locks, shared local JWT security, business and knowledge services, PostgreSQL/H2 migrations, synthetic references, React/TypeScript portal, model adapters, token chunking, parser validators, hybrid pgvector retrieval, owned memory/provenance, buffered SSE, typed read tools, tests, generated OpenAPI/schema dictionaries, evaluation fixtures, 24 parsed Mermaid diagrams and run/demo/interview docs. See traceability.csv for precise status per module. No changes were made to the original /workspace/mo source.

## Pending inventory reconciliation

entity-inventory.csv is a candidate superset, including uppercase column/config tokens marked UNKNOWN. It is not a certified complete semantic entity inventory. All DOCUMENTARY_ONLY rows lack an implementation; all SYNTHETIC_MAPPING rows need field-level fidelity review. Explicitly reconcile missing Part 1/2 field details, alternate AwardAuthority/AcademicYear mappings, all XXLE/XXLC families, queue ranges, DTOs and enum occurrences. Do not fabricate missing library fields. The 17 proposed AI tables exist, but repositories/code_relations/prompt_templates/agent_runs and AI event tables are mostly dormant schema support rather than complete features.

## Remaining implementation scope

- Separate registration/attendance worklists; richer course/variant/study-period authoring; complete synthetic reference/subscription management.
- Full CoC subtype handling, especially authorized provider transfer, and distinct immutable subtype/finance histories; detailed fee-waiver/funding-run lifecycle.
- Configured rule activation backed by an allowlisted deterministic rule engine, plus page/component mappings.
- Durable resumable import workers, progress polling, richer row failure taxonomy and recovery after process failure.
- Detailed financial/negative-balance/remittance reports, sync exception workflow, operational replay and real optional RabbitMQ delivery.
- Named scenario facades for unavailable UG/PGNI/Apply Once/BCF/course-closure/disposition contracts; current source categories and local receivers do not reproduce those procedures.
- Remaining read tools (masked search, course detail, side-effect-free CoC callback), complete tool schemas/limits/audit and per-turn tool provenance in responses.
- Structural method/table chunking and richer parser metadata; source ACL-only updates, physical deletion/retention, stale-turn recovery and transactional cancellation edge cases.
- Stronger PII/DLP, rate limits, external OIDC execution, complete observability/cost accounting and every security/operational control requested in PROJECT_REQUEST.md.
- Opt-in real OpenAI smoke/evaluation after secure key configuration. Live model latency, token usage, groundedness and citation entailment are not measured by offline tests.
- Complete all acceptance scenarios, concurrency/dependency-failure suites, parser/tenant permutations and remaining browser flows. Current tests do not establish the requested exhaustive coverage or 80% code coverage.

## Exact continuation commands

`bash scripts/cloud-install.sh` installs/validates; `SIS_PROFILE=integration bash scripts/start.sh` starts; `python3 scripts/seed.py`, `python3 scripts/verify.py`, `python3 scripts/e2e.py`, `python3 scripts/evaluate.py`, and `python3 scripts/export-contracts.py` generate measured evidence. Stop before using a rebuilt JAR or changing profiles. Logs are in data/. Generated local secrets are ignored; never print them. Fresh snapshot restoration/publication has not been independently tested.

Next task: reconcile the inventory semantically, then implement the missing worklist/catalogue/CoC/finance slices with tests. Preserve the current authentication, provider checks, exact money math, immutable history, active-version retrieval and honest simulation labels.
