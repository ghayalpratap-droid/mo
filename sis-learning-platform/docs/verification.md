# Verification report

Checkpoint date: 2026-10-15 (Asia/Calcutta). This is a runnable educational increment, not completion of the entire master request.

| Check | Observed result |
|---|---|
| Java 21 Gradle tests | 14 passed: 7 domain/API and 7 knowledge/parser/masking tests; zero failed/skipped |
| Both executable backend builds | Passed against Boot 3.5.16 / Spring AI 1.1.8 |
| Frozen npm install, frontend component/client tests, TypeScript and Vite build | Passed; four frontend tests |
| Real PostgreSQL/pgvector integration | 11 functional groups passed |
| Browser workflow | Passed intake, confirmations, payments, CoC, refresh persistence, streamed simulated answer, citation viewer, mobile width and denied provider access |
| Mermaid | All 24 diagrams parsed successfully using Mermaid with JSDOM |
| OpenAPI / schema exports | Two authenticated OpenAPI specifications; 46 tables including two Flyway metadata tables |
| Reusable installer | Executed successfully; pinned JDK, wrapper checksum, Gradle locks and npm ci retained |
| Stop/restart startup | Executed repeatedly with persistent PostgreSQL volume; health and portal HTML verified |
| Source mapping alternatives | 73 separate documentary class/table mapping occurrences retained |
| Live OpenAI smoke, provider semantic quality and token/cost results | UNRUN / UNMEASURED because no key was present |
| Publication / fresh-task restoration | Not performed or verified |

Integration groups exercised:

- application intake, idempotency, read-only role and cross-provider denial
- registration, attendance, stale-version conflict and exact payment amounts
- side-effect-free preview, rejected and accepted CoCs, immutable timeline
- CSV valid/invalid/duplicate rows and authorized result download
- real pgvector, exact identifiers, bidirectional source ACL and owned sessions/feedback
- live-data tool reads actual authorized business status
- failed indexing preserves good version; replacement/deletion revoke stale citations and memory
- runtime roles cannot update audit or history records
- documentation role checks and safe XML ingestion rejection
- authenticated buffered SSE and no mutation from retrieved injection text in offline mode
- generated credentials are absent from built frontend assets

The dedicated integration test temporarily rejects one checksum-specific source document through a PostgreSQL constraint, proves that the previous active version stays usable, and removes the test constraint in finally. Runtime role checks prove that audit and history UPDATE are denied. Both provider directions and same-provider conversation ownership are checked. The browser runs against actual services, not mocked API responses.

The evaluation fixture measured offline retrieval recall@6 = 1.0 on four positive questions over one curated document. This is a small deterministic fixture, not evidence of live semantic retrieval quality. Unknown API, irrelevant weather and instruction queries produced empty retrieval in this run; isolation/missing-procedure questions can retrieve public context without proving an answer is supported. Citation-ID validation is distinct from human semantic support. Groundedness, relevance, calibrated thresholds, load targets, live tokens and cost remain unmeasured.

The 909 inventory names form a conservative candidate superset with explicit UNKNOWN/DOCUMENTARY_ONLY rows; they are not 909 implemented entities. See checkpoint.md and traceability.csv for remaining business, model, integration, UI and operational scope. Source aliases and conflicting definitions are in mapping-alternatives.csv. Source/schema field definitions are not invented to fill unavailable catalogue sections.

Current-instance checks do not prove that a new task has restored the workspace. Install/start instructions and the optional SIS_OPENAI_API_KEY requirement were saved to the environment draft; api.openai.com was added for that binding. The draft must be reviewed/saved in environment settings and the environment published by the user. Values were not supplied in chat or included in source.
