# Architecture and decisions

Status: educational reconstruction in progress. See verification and coverage ledgers for measured capabilities and remaining work.

The React/TypeScript portal uses React Router and TanStack Query. It authenticates to the domain service and keeps a short-lived bearer token in memory. Both Java services validate HS256 issuer/expiry and derive provider and roles from server-created claims. The local identity setup is a simulator for an external identity provider, not an OIDC deployment.

The domain service contains application, confirmation, CoC, finance, import, catalogue and outbox components. JPA owns the mutable application aggregate; bounded JDBC projections and history/outbox writes share its transaction. Catalogue, students and finance have separate synthetic tables. Extraction to independent services should follow ownership boundaries after contracts and delivery semantics are stable; splitting every legacy service now would multiply distributed failure cases without restoring missing contracts.

The knowledge service has no access to business tables or Oracle. It owns the knowledge schema and calls bounded business APIs using the authenticated identity. OpenAI is called only from this backend. In offline/integration mode deterministic hash vectors and excerpt responses replace external model calls visibly. Real PostgreSQL/pgvector is still required; H2 is not a vector database.

ADR-01: Java 21, Spring Boot 3.5.16 and Spring AI 1.1.8; pinned Gradle wrapper, BOMs, dependency locks and npm lockfile. See version-matrix.md.

ADR-02: local/offline domain uses persistent H2; integration uses PostgreSQL business schema. Knowledge always uses PostgreSQL with public.vector. No Oracle behavior is verified by H2.

ADR-03: explicit JDBC vector writes maintain chunk foreign keys and model dimension constraints. Automatic PgVectorStore insert SQL is not used against the custom schema. Flyway owns DDL through sis_migrator. Runtime roles have explicit grants; audit/history UPDATE/DELETE are denied independently of PUBLIC privileges.

ADR-04: one chat persistence table. OwnedChatMemory reads bounded, currently authorized turns from chat_messages. ChatTurns atomically records messages and citation provenance. A revoked/deleted/replaced source hides its old assistant content from history and future model memory. No duplicate advisor memory store requires synchronization.

ADR-05: at-least-once outbox delivery, bounded retry, deterministic named local receivers, processed-event deduplication. There is no universal exactly-once promise. RabbitMQ and Oracle AQ are DOCUMENTED_CURRENT. Kafka is only DOCUMENTED_PROPOSAL and is not a dependency of this demo.

ADR-06: source content is untrusted data. It never supplies authorization or a tool host. Only typed read tools are registered; no SQL execution, file reader, arbitrary URL fetch, mutation, replay, disposition or funding tools exist.

ADR-07: streaming transport buffers generated text until citation-ID validation, then delivers deltas and a completion envelope. This improves citation handling but does not optimize provider time-to-first-token. Partial output is labeled and interruptions are not recorded as completed answers. Live model quality and production load targets are unmeasured.

Diagrams are under diagrams/. Current-state diagrams describe the attachment; target diagrams describe this demo unless explicitly marked cloud proposal.
