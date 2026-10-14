# Interview explanation

## Two-minute overview

This is an educational SIS/LLE reconstruction built from supplied architecture notes, not work on inaccessible production source. The notes describe a Java 8/Spring Boot 2.3 and legacy JSF/Hibernate estate using Oracle, RabbitMQ and Oracle AQ. They also contain multiple AI target proposals and an incomplete entity catalogue. I preserved those distinctions instead of claiming the proposals were production features.

The demo uses Java 21, a modular business service, a separate Spring AI knowledge service and a React/TypeScript portal. Business rules are deterministic. The assistant retrieves authorized source passages and calls bounded read-only APIs for current facts. Provider isolation is enforced by the backend and again on tool targets. PostgreSQL/pgvector stores knowledge; a persistent H2 profile supports the business workflow without Oracle. Offline models are marked simulated. I verified builds, API isolation, source lifecycle behavior and a real browser workflow, while recording missing production contracts and unmeasured live model quality.

## Technical walkthrough

Application commands validate a DTO, derive provider scope from signed server identity, check version/idempotency, update the aggregate, and append immutable history plus a durable outbox event in one transaction. Payments use BigDecimal and an explicitly synthetic split policy. Integration receivers are deterministic named local substitutes; delivery is at least once with processed-event deduplication, bounded retry and dead state.

Knowledge ingestion validates safe source syntax, masks baseline secrets/PII patterns, creates heading/token chunks, hashes content/model/ACL, batches embeddings and atomically switches active versions. PostgreSQL advisory locks serialize publication. SQL filters provider, audience, model and active version before candidate limits in vector and keyword branches. Exact identifiers and rank fusion complement semantic retrieval. Citations are checked against retrieved IDs; their presence is not semantic proof.

Chat sessions belong to one user. Memory and history reauthorize source provenance after source changes. Typed tools keep provider/identity out of model arguments and forward the authenticated user to the domain API. No arbitrary SQL/URL/filesystem or write tool is registered. Streaming buffers output through citation validation and labels partial states. Local generated credentials and a separate migration/runtime database role demonstrate boundaries; corporate OIDC and full operational hardening remain future deployment work.

Tests exposed a PostgreSQL Instant binding issue that H2 did not catch, and browser testing exposed implicit form-label ambiguity. Those corrections illustrate why compilation, real database checks and browser tests answer different questions. I would discuss unimplemented scope explicitly rather than presenting this sample as production experience.
