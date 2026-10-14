# Demo API contracts

Routes below are new `/api/v1` contracts, not claims about legacy endpoint availability. Login is the only unauthenticated business API. Use Authorization: Bearer TOKEN; no credential query strings. APIs return 400 for validation, 401 unauthenticated, 403 role denial, 404 absent or hidden object, 409 idempotency/version conflict, 422 deterministic business rejection where applicable, 429 concurrency limit and 503 AI/dependency failure. Validation errors contain message, fieldErrors and correlationId; authentication and malformed-request handlers also return the correlation envelope.

Application create example:
```json
{"provider":"A","sourceRef":"DEMO-001","studentName":"Fictional Learner","source":"LLE","courseId":"A-CS","academicYear":2026,"fee":9250.01}
```
Supply a unique `Idempotency-Key`. A retry with identical payload returns the same ID; changed payload with that key returns 409. Response is a bounded application DTO with UUID, provider, statuses, fee, currency, version and timestamps. `GET applications?q=&page=0&size=20` permits size 1..50, page 0..10000 and query up to 100 characters. Sorting is fixed by created time and ID.

Confirmation: `{"version":0,"status":"CONFIRMED"}`. CoC: `{"version":2,"type":"SUSPENSION","effectiveDate":"2026-01-01"}`. Preview performs no writes. Invalid submitted CoCs retain a REJECTED decision and return 200 with that business status; invalid HTTP/schema input returns 400. Clients must inspect business status. Monetary requests use decimal values and server-side BigDecimal.

Chat stream uses authenticated POST `/api/v1/ai/sessions/{id}/messages/stream` with `{"question":"Explain PGNI processing"}`. Events: start (turnId), delta, complete (answer/citations/liveFacts/mode), failure. `POST ai/turns/{id}/cancel` authorizes session ownership. `scripts/verify.py` is an executable request/response walkthrough without printing credentials.

| Method | Path | Implementation |
|---|---|---|
| DELETE | `/api/v1/ai/sessions/{id}` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| DELETE | `/api/v1/ai/sources/{id}` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| GET | `/api/v1/academic-years` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/ai/citations/{id}` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| GET | `/api/v1/ai/ingestion-jobs` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| GET | `/api/v1/ai/sessions` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| GET | `/api/v1/ai/sessions/{id}/messages` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| GET | `/api/v1/ai/sources` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| GET | `/api/v1/ai/status` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| GET | `/api/v1/ai/tools/applications/{id}/summary` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| GET | `/api/v1/applications` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/applications/{id}` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/applications/{id}/cocs` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/applications/{id}/history` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/applications/{id}/payment-schedules` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/applications/{id}/summary` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/applications/{id}/tuition-periods` | `backend/sis-domain-service/src/main/java/edu/sis/domain/CatalogueController.java` |
| GET | `/api/v1/auth/me` | `backend/shared/src/main/java/edu/sis/shared/AuthController.java` |
| GET | `/api/v1/catalogue` | `backend/sis-domain-service/src/main/java/edu/sis/domain/CatalogueController.java` |
| GET | `/api/v1/courses` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/imports/{id}` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/imports/{id}/results` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/modules` | `backend/sis-domain-service/src/main/java/edu/sis/domain/CatalogueController.java` |
| GET | `/api/v1/operations` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/operations/exceptions` | `backend/sis-domain-service/src/main/java/edu/sis/domain/CatalogueController.java` |
| GET | `/api/v1/operations/toggles` | `backend/sis-domain-service/src/main/java/edu/sis/domain/CatalogueController.java` |
| GET | `/api/v1/providers` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/reference-data` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/reports` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| GET | `/api/v1/rules` | `backend/sis-domain-service/src/main/java/edu/sis/domain/CatalogueController.java` |
| GET | `/api/v1/students` | `backend/sis-domain-service/src/main/java/edu/sis/domain/CatalogueController.java` |
| POST | `/api/v1/ai/feedback` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| POST | `/api/v1/ai/search` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| POST | `/api/v1/ai/sessions` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| POST | `/api/v1/ai/sessions/{id}/messages` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| POST | `/api/v1/ai/sessions/{id}/messages/stream` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| POST | `/api/v1/ai/sources` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| POST | `/api/v1/ai/turns/{id}/cancel` | `backend/sis-knowledge-service/src/main/java/edu/sis/knowledge/KnowledgeController.java` |
| POST | `/api/v1/applications` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| POST | `/api/v1/applications/{id}/attendance` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| POST | `/api/v1/applications/{id}/cocs` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| POST | `/api/v1/applications/{id}/cocs/preview` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| POST | `/api/v1/applications/{id}/payment-schedules` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| POST | `/api/v1/applications/{id}/registration` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |
| POST | `/api/v1/auth/login` | `backend/shared/src/main/java/edu/sis/shared/AuthController.java` |
| POST | `/api/v1/imports` | `backend/sis-domain-service/src/main/java/edu/sis/domain/BusinessController.java` |

DTO definitions: backend/sis-domain-service/.../Contracts.java and knowledge KnowledgeController records. Generated OpenAPI exports are openapi-domain.json and openapi-knowledge.json. Complete examples for all endpoints and cursor/sorting extensions remain pending.
