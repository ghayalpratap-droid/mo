from pathlib import Path
r=Path(__file__).resolve().parents[1];d=r/'docs/diagrams';d.mkdir(exist_ok=True)
def diagram(name,text):(d/(name+'.mmd')).write_text(text+'\n')
diagram('01-current-context','''flowchart LR
 Portal[Legacy JSF portal] --> Legacy[Legacy Spring and Hibernate]
 EDS[EDS and Mule] --> Boot[Java 8 Boot 2.3 services]
 CLASS[CLASS] --> Rabbit[RabbitMQ]
 Rabbit --> Boot
 Legacy --> Oracle[(Oracle schemas and PL SQL)]
 Boot --> Oracle
 Boot --> AQ[Oracle AQ JMS]
 AQ --> Downstream[CEM DSFS LAP and LLE]
 Boot --> BAS[BAS API]
 Boot --> BCF[BCF API]
''')
diagram('02-target-trust-boundaries','''flowchart LR
 subgraph Browser[Browser boundary]
  UI[React TypeScript portal]
 end
 subgraph Trusted[Authenticated application boundary]
  B[Domain service]
  K[Knowledge service]
  R[Typed read tools]
 end
 subgraph DB[Database ownership boundary]
  Business[(business schema)]
  Knowledge[(knowledge schema and pgvector)]
 end
 UI -->|Bearer identity| B
 UI -->|Bearer identity| K
 B --> Business
 K --> Knowledge
 K --> R
 R -->|Reauthorized API read| B
 K -->|Masked context only| Model[OpenAI or visible offline adapter]
 B --> Sim[Named local integration simulators]
''')
diagram('03-business-erd','''erDiagram
 providers ||--o{ applications : owns
 students ||--o{ applications : identifies
 applications ||--|| tuition_periods : covers
 tuition_periods ||--|| tuition_finance_details : finances
 tuition_periods ||--o{ tuition_period_histories : snapshots
 applications ||--o{ histories : records
 applications ||--o{ cocs : changes
 applications ||--o{ instalments : schedules
 applications ||--o{ outbox_events : emits
 applications {
  uuid id PK
  string provider FK
  decimal fee
  string currency
  long version
 }
 cocs {
  uuid id PK
  uuid application_id FK
  string type
  string status
 }
''')
diagram('04-catalogue-erd','''erDiagram
 providers ||--o{ courses : offers
 providers ||--o{ campuses : locates
 providers ||--o{ modules : owns
 courses ||--o{ course_modules : contains
 modules ||--o{ course_modules : included
 courses ||--o{ course_variants : varies
 campuses ||--o{ course_variants : hosts
 academic_years ||--o{ course_variants : dates
 qualifications ||--o{ course_variants : awards
 providers ||--o{ subscriptions : subscribes
 academic_years ||--o{ subscriptions : bounds
''')
diagram('05-knowledge-erd','''erDiagram
 source_files ||--o{ documents : versions
 source_files {
  uuid id PK
  uuid active_version
  string provider
  string audience
  boolean deleted
 }
 documents ||--o{ document_chunks : splits
 document_chunks ||--|| embeddings : encodes
 documents ||--o{ code_relations : describes
 document_chunks ||--o{ message_sources : cites
 chat_messages ||--o{ message_sources : records
 embeddings {
  uuid id PK
  vector embedding
  string model
 }
''')
diagram('06-conversation-operations-erd','''erDiagram
 chat_sessions ||--o{ chat_messages : owns
 chat_sessions ||--o{ chat_turns : processes
 chat_messages ||--o{ feedback : receives
 chat_sessions ||--o{ agent_runs : tracks
 audit_logs {
  uuid id PK
  string actor
  string event_type
 }
 retrieval_logs {
  uuid id PK
  json chunk_ids
  long latency_ms
 }
 tool_invocations {
  uuid id PK
  string outcome
 }
 ingestion_jobs {
  uuid id PK
  string status
 }
''')
diagram('07-class-packages','''classDiagram
 class BusinessController
 class BusinessService
 class Applications
 class StudentApplication
 class CatalogueController
 class OutboxSimulator
 class ExternalReceiver
 class LocalIntegrationSimulator
 class KnowledgeController
 class ChatTurns
 class KnowledgeStore
 class ModelGateway
 class OwnedChatMemory
 class BusinessTools
 BusinessController --> BusinessService
 BusinessService --> Applications
 Applications --> StudentApplication
 BusinessService --> OutboxSimulator : durable event
 OutboxSimulator --> ExternalReceiver
 ExternalReceiver <|.. LocalIntegrationSimulator
 KnowledgeController --> ChatTurns
 ChatTurns --> KnowledgeStore
 ChatTurns --> ModelGateway
 ChatTurns --> OwnedChatMemory
 ModelGateway --> BusinessTools
''')
diagram('08-application-state','''stateDiagram-v2
 [*] --> ACTIVE : synthetic intake
 ACTIVE --> SUSPENDED : valid suspension
 SUSPENDED --> ACTIVE : valid resumption
 ACTIVE --> WITHDRAWN : withdrawal
 SUSPENDED --> WITHDRAWN : withdrawal
 ACTIVE --> CANCELLED : cancellation
 SUSPENDED --> CANCELLED : cancellation
 WITHDRAWN --> [*]
 CANCELLED --> [*]
''')
diagram('09-coc-state','''stateDiagram-v2
 [*] --> Validating : schema and identity checked
 Validating --> REJECTED : synthetic rule failure
 Validating --> ACCEPTED : synthetic rules pass
 ACCEPTED --> Recorded : command history and outbox transaction
 REJECTED --> Recorded : rejection history and outbox transaction
 Recorded --> [*]
''')
diagram('10-import-state','''stateDiagram-v2
 [*] --> RUNNING : bounded CSV accepted
 RUNNING --> COMPLETED : every row has result
 COMPLETED --> [*]
 note right of RUNNING
   Row outcomes CREATED or INVALID
   Crash recovery is not implemented
 end note
''')
diagram('11-ingestion-state','''stateDiagram-v2
 [*] --> RUNNING : source validated
 RUNNING --> SUCCEEDED : atomic active version switch
 RUNNING --> FAILED : parse embedding or write failure
 FAILED --> PreviousVersion : retain last usable evidence
 SUCCEEDED --> [*]
 PreviousVersion --> [*]
''')
diagram('12-local-deployment','''flowchart TB
 Host[Developer host with Java 21 Node Docker]
 Host --> Vite[Vite 5174]
 Host --> Domain[Domain 8091]
 Host --> Knowledge[Knowledge 8092]
 Domain --> H2[(H2 persistent file local or offline)]
 Domain -.->|integration profile| PG[(PostgreSQL 5544)]
 Knowledge --> PG
 PG --> Vector[pgvector 0.8.1]
 Knowledge -.->|local real-model profile| OpenAI[OpenAI API]
''')
diagram('13-cloud-proposal','''flowchart TB
 Browser[Portal browser] --> Edge[TLS ingress and OIDC]
 Edge --> Domain[Domain containers]
 Edge --> Knowledge[Knowledge containers]
 Domain --> DB[(Managed PostgreSQL business)]
 Knowledge --> KDB[(Managed PostgreSQL pgvector)]
 Knowledge --> Egress[Controlled model egress]
 Egress --> Model[OpenAI configured endpoint]
 Domain --> Broker[Optional RabbitMQ target adapter]
''')
diagram('14-ingestion-data-flow','''flowchart LR
 Upload[Authorized source upload] --> Parse[Bounded safe syntax validation]
 Parse --> Scrub[Secret and PII baseline scrub]
 Scrub --> Split[Heading and token chunks]
 Split --> Hash[Content ACL and model hash]
 Hash --> Emb[Batch embeddings]
 Emb --> TX[Transaction and per-source advisory lock]
 TX --> Chunks[(New chunks and vectors)]
 Chunks --> Active[Atomic active version pointer]
 TX --> Audit[Job outcome and audit]
''')
diagram('15-query-data-flow','''flowchart LR
 Question[Question and owned session] --> Mask[Mask input]
 Mask --> Memory[Reauthorized bounded memory]
 Memory --> ACL[Provider audience and active version filters]
 ACL --> Semantic[Vector candidates]
 ACL --> Keyword[Keyword and exact identifier candidates]
 Semantic --> RRF[Rank fusion and deduplication]
 Keyword --> RRF
 RRF --> Model[Context and read-only tools]
 Model --> Citations[Validate citation identifiers and recheck ACL]
 Citations --> Stream[Buffered validated SSE]
 Stream --> Persist[Completed messages and provenance]
''')
flows={
 'application-intake':('Create application','Intake DTO and provider check','Source reference course and fee validation','Application tuition history and outbox'),
 'registration':('Confirm registration','Confirmation DTO provider and version','Active application and confirmation state','Registration status history and outbox to CEM simulator'),
 'attendance':('Confirm attendance','Confirmation DTO provider and version','Registration prerequisite','Attendance status history and outbox to BAS simulator'),
 'coc':('Submit CoC','Change DTO provider and version','Type date state and money rules','Accepted mutation or rejected decision plus history outbox'),
 'payment':('Schedule payments','Object provider version and write role','Active registered application','Exact decimal instalments and history'),
 'csv-import':('Upload CSV','Provider write role and bounded rows','Per row validation and duplicate reference','Job row results applications histories outbox'),
 'publishing-retry':('Scheduled outbox dispatch','Locked pending event rows','Receiver budget and idempotent processed event','Delivered or bounded backoff or DEAD'),
}
for name,(action,auth,rule,persist) in flows.items():
 diagram('sequence-'+name,f'''sequenceDiagram
 participant UI as React portal or local scheduler
 participant API as Controller
 participant Service as Service and deterministic rules
 participant DB as Relational database
 participant Sim as Named local receiver
 UI->>API: {action}
 API->>API: {auth}
 alt Unauthorized or stale version
  API-->>UI: 403 or 404 or 409
 else Authorized request
  API->>Service: Validated request DTO
  Service->>Service: {rule}
  alt Validation failure
   Service-->>UI: 400 or 422 or explicit REJECTED decision
  else Valid or recorded rejection
   Service->>DB: {persist}
   DB-->>Service: Transaction committed
   Service-->>UI: Response DTO and refreshed history
   DB-->>Sim: Durable outbox event on dispatch
   Sim-->>DB: Processed event or bounded retry
  end
 end
''')
diagram('sequence-rag-chat','''sequenceDiagram
 participant UI as React portal
 participant API as ChatTurns
 participant DB as Knowledge schema
 participant Model as Model adapter
 UI->>API: Authenticated POST streaming question
 API->>DB: Check session owner and claim active turn
 API->>DB: Retrieve active authorized vector and keyword candidates
 API->>Model: Masked question memory and untrusted evidence
 Model-->>API: Answer with citation IDs
 API->>DB: Recheck citation access
 API-->>UI: Validated buffered deltas
 API->>DB: Completed messages and citation provenance
 API-->>UI: Completion envelope
 alt Cancellation or failure
  API->>DB: CANCELLED FAILED or INTERRUPTED
  API-->>UI: Failure or partial state
 end
''')
diagram('sequence-live-tool','''sequenceDiagram
 participant UI as Portal question
 participant K as Knowledge service
 participant Tool as BusinessTools
 participant B as Domain API
 participant DB as Business schema
 UI->>K: Latest attendance status UUID
 K->>Tool: Typed applicationSummary with server identity
 Tool->>B: Bearer authenticated GET summary
 B->>B: Verify role and object provider
 alt Wrong provider
  B-->>Tool: 404 hidden object
 else Allowed
  B->>DB: Read authoritative current state
  DB-->>B: Confirmation status
  B-->>Tool: Masked status source and timestamp
  Tool-->>K: Live facts
  K-->>UI: Live fact distinct from document citations
 end
''')
# Source-specific flows, documentary only, preserve service names without claiming implementation fidelity.
sourceflows=[('UG EDS intake','sis-application-service','TuitionPeriodRepository','sis-publisher and AQ'),('PG NI notification','sis-notification-service','Configured AppRulesEngine','LAP AQ and publisher'),('Apply Once','sis-applyonce-service','PGNewAppRulesEngine','CEM and DSFS'),('LLE application catalogue','lle-application-service and lle-course-service','XXLE and XXLC repositories','Mule and CMS'),('Attendance to bursary','sis-attendance-service','Staged attendance JDBC','BAS'),('Bulk CoC','sis-coc-service','CSV processor and validation','RabbitMQ and AQ'),('PG CoC to BCF','pg-coc-service','KIFPK032 cursor and callback','BCF'),('Registration CSV','JMBSRI','SpringSisCsvService','Oracle AQ'),('Course closure','sis-application-service','kifpk053','CMS RabbitMQ'),('Disposition','sis-application-service','KIFPK052 and KIFPK059','GDPR RabbitMQ'),('Validation search','sis-validation-service and sis-data-service','Rules metadata and SQL','Legacy portal'),('Negative balance','sis-reports-service','Report DAO','CLASS ORDS')]
text='# Documentary end-to-end mappings\n\nThese are DOCUMENTED_CURRENT statements from Ne.md:207–427. Referenced Java/PL SQL is unavailable. The demo uses source categories and named simulators; it does not reproduce production procedures.\n\n| Flow | Documented service | Data/rules | Integration | Demo limit |\n|---|---|---|---|---|\n'
for flow,svc,data,ext in sourceflows:text+=f'| {flow} | {svc} | {data} | {ext} | Synthetic aggregate or named receiver; production contract unavailable |\n'
text+='\nFor implemented commands, React → typed request DTO → bearer validation → role/provider/version checks → controller → deterministic service → JPA/JDBC transaction → application/history/outbox → response DTO → Query invalidation. Duplicate keys return the prior result or conflict on changed payload. Stale versions return 409. Role denial returns 403, hidden object returns 404, invalid input returns 400/422 or explicit rejected CoC. Database failures must not produce fake success. Outbox delivery happens after commit and is retried independently. CSV crash recovery and production dead-letter replay remain incomplete.\n'
(r/'docs/end-to-end-flows.md').write_text(text)
index='# Diagram index\n\nCurrent context is documentary; cloud is proposal; all other diagrams describe the synthetic implementation or explicitly noted logical stages. ERDs omit some columns for readability; migrations are authoritative.\n\n'
for p in sorted(d.glob('*.mmd')):index+=f'## {p.stem}\n\n```mermaid\n{p.read_text()}```\n\n'
(r/'docs/diagrams.md').write_text(index)
print(len(list(d.glob('*.mmd'))),'diagrams generated')
