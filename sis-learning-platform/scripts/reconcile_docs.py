from pathlib import Path
import csv,json,re
r=Path(__file__).resolve().parents[1]
def w(p,s):p=r/p;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s)
source=(r/'Ne.md').read_text().splitlines();old=list(csv.DictReader((r/'docs/entity-inventory.csv').open()));columns=list(old[0]);items={x['source_name']:x for x in old}
with (r/'docs/extraction-candidates.csv').open('w') as f:
 out=csv.DictWriter(f,fieldnames=columns);out.writeheader();out.writerows(old)
sections=json.loads((r/'docs/source-sections.json').read_text())
def add(name,n,kind,table=''):
 if name not in items:items[name]={k:'' for k in columns};items[name].update(source_name=name,source_lines=str(n),source_heading=next(s['heading'] for s in sections if s['start_line']<=n<=s['end_line']),classification='DOCUMENTED_PROPOSAL' if 592<=n<1297 or 1773<=n<3189 else 'DOCUMENTED_CURRENT',mapping_type=kind,table_schema=table,fields_unknown='Unspecified fields and constraints',primary_key='UNKNOWN',relationships='See source reference',test_coverage='UNRUN',limitation='Documentary evidence, not inspected legacy source')
 else:
  x=items[name];x['source_lines']=';'.join(map(str,sorted(set([int(x) for x in x['source_lines'].split(';') if x]+[n]))));
  if table:x['table_schema']=table
for n,line in enumerate(source,1):
 for name,table in re.findall(r'\b([A-Z][a-z][A-Za-z0-9]+)\s*→\s*([A-Z][A-Z0-9_.]+)',line):add(name,n,'reference entity',table);add(table,n,'table',table)
 for name in re.findall(r'\b(?:Xxle|Xxlc|Xxss|Xxsl|Default)[A-Za-z0-9]+\b',line):add(name,n,'documentary entity candidate')
 if 4775<=n<=4800 and line.startswith('|'):
  for name in re.findall(r'\b[A-Z][A-Z0-9_]{2,}\b',line.split('|')[1]):add(name,n,'queue/non-entity' if '_JMS_' in name else 'table',name)
for i in range(1,7):add('XXAQ_HEI_REG_JMS_TXT_Q'+str(i),4724,'queue/non-entity')
# Classify schema objects only against explicit evidence, retain uncertain tokens as unknown instead of fake entities.
known_tables=set()
for n,line in enumerate(source,1):
 if line.startswith('#') and '→' in line:
  tail=line.split('→',1)[1];known_tables.update(re.findall(r'\b[A-Z][A-Z0-9_.]{2,}',tail))
 if 4775<=n<=4800 and line.startswith('|'):known_tables.update(re.findall(r'\b[A-Z][A-Z0-9_]{2,}',line.split('|')[1]))
for x in items.values():
 name=x['source_name'];x['completeness']='DOCUMENTARY_ONLY';x['implementation_name']='';lower=name.lower();x['module']=x['module'] or 'Reference/application'
 if '_JMS_' in name:x['mapping_type']='queue/non-entity'
 elif name in known_tables:x['mapping_type']='immutable history entity' if any(t in name for t in ['HIST','EVENT','LOG']) else 'reference entity'
 elif name.isupper():x['mapping_type']='unknown token (column/config/table candidate)'
 elif name.endswith(('Enum','EnumType')):x['mapping_type']='enum'
 elif name.endswith(('PK','Id')):x['mapping_type']='embeddable/key candidate'
 elif x['mapping_type'].startswith('DTO'):x['mapping_type']='DTO/integration contract candidate'
 # Explicit synthetic mappings; the source field layout remains documentary.
 mapping=None
 if name in ['Customer','XXSL_CUSTOMERS']:mapping='students (synthetic masked projection)'
 elif name in ['Course','XXSL_COURSES']:mapping='courses (synthetic reference)'
 elif name in ['HEIPOJO','XXSL_COLLEGES']:mapping='providers (synthetic reference)'
 elif name in ['COCS','CocPOJO','Coc']:mapping='cocs (synthetic discriminator command model)'
 elif name in ['TUITION_PERIODS','TuitionPeriod','DefaultTuitionPeriod']:mapping='tuition_periods'
 elif name in ['TUITION_PERIOD_HISTORIES','DefaultTuitionPeriodHistory']:mapping='tuition_period_histories'
 elif name in ['TUITION_FINANCE_DETAILS','DefaultTuitionFinanceDetail']:mapping='tuition_finance_details'
 elif name in ['XXSS_APPLICATIONS','XXLE_APPLICATIONS','XxleApplication','XxssApplication']:mapping='applications (synthetic aggregate; legacy fields not reproduced)'
 elif name in ['PAYMENT_INSTALMENTS','PaymentInstalmentPOJO']:mapping='instalments'
 elif name in ['IMPORT_RESULTS','IMPORT_FAILURE_DETAILS','STAGE_FILE_UPLOADS']:mapping='import_rows (synthetic combined result projection)'
 elif name in ['BULK_FILE_SUBMISSIONS','BulkFileSubmissionPOJO']:mapping='import_jobs'
 elif name in ['XXSL_ACADEMIC_YEARS','AcademicYear','AcademicYearPOJO']:mapping='academic_years'
 elif name=='QUALIFICATIONS':mapping='qualifications'
 elif name=='XXSL_HEI_LOCNS':mapping='campuses'
 elif name=='XXLC_CORE_MODULES':mapping='modules'
 elif name=='XXLC_COURSE_VARIANTS':mapping='course_variants'
 elif name=='RULE_CONFIG_ATTRIBUTES':mapping='rule_configs (synthetic explanatory catalogue)'
 elif name=='XXSL_SYNC_EXCEPTIONS':mapping='sync_exceptions (masked synthetic record)'
 elif name in ['repositories','source_files','documents','document_chunks','embeddings','code_relations','chat_sessions','chat_messages','audit_logs','retrieval_logs','tool_invocations','ingestion_jobs','feedback','prompt_templates','agent_runs','outbox_events','processed_events']:mapping='knowledge.'+name
 if mapping:x['implementation_name']=mapping;x['completeness']='SYNTHETIC_MAPPING';x['limitation']='DEMO_ASSUMPTION fields; see implementation migrations. Documentary source shape is not reproduced.';x['test_coverage']='See scripts/verify.py and verification report; not every dormant table is exercised'
 if name in ['TuitionFeeForecast','TUITION_FEE_FORECASTS']:x['mapping_type']='inactive JPA mapping / alternative legacy report DTO';x['limitation']='Do not count as verified active JPA entity; Ne.md:3233 and legacy projection at 3960'
 if not mapping:x['limitation']=(x['limitation']+'; not implemented in this increment').strip('; ')
with (r/'docs/entity-inventory.csv').open('w') as f:
 out=csv.DictWriter(f,fieldnames=columns);out.writeheader();out.writerows(sorted(items.values(),key=lambda x:x['source_name'].lower()))
w('docs/legacy-to-demo-mapping.md','# Legacy to demo mapping\n\nThis inventory has '+str(len(items))+' names, including uncertain extraction tokens. It is an auditable candidate superset, not a certified entity count. `extraction-candidates.csv` retains the first pass. `source-sections.json` and `data-dictionary.md` retain the evidence. Rows marked DOCUMENTARY_ONLY are not implemented; SYNTHETIC_MAPPING does not imply matching legacy fields. Alternative class mappings remain separate rows. Schema owners are inferred unless the source gives an explicit schema.\n\n| Legacy family | Demo disposition |\n|---|---|\n| Customer, course, HEI | Synthetic students/courses/providers; no actual identifiers or bank details |\n| UG/PG/Apply Once/LLE | One synthetic application aggregate with source category |\n| Registration/attendance | Aggregate status plus immutable event history; not legacy confirmation tables |\n| CoC subtypes | Explicit type discriminator and deterministic rules; unsupported types remain rejected |\n| Tuition/finance | Separate synthetic period, detail and snapshot tables |\n| Payments | Exact-decimal instalments; funding-run and bank processing not implemented |\n| Bulk imports | Synthetic jobs/results; legacy header/footer/AQ contracts unavailable |\n| Catalogue | Synthetic courses/modules/campuses/qualifications; detailed XXLC variants remain documentary |\n| Rule metadata | Explanatory rule catalogue; no arbitrary database-driven bean execution |\n| Integrations | Named deterministic local receivers; no production contact |\n| AI proposed DDL | All 17 named tables migrated; dormant metadata tables are not complete features |\n')
w('docs/ai-explained.md','''# AI explained through this project

| Concept | Meaning | Purpose / component | Input | Output | Example |
|---|---|---|---|---|---|
| LLM | Generates language from context | ChatModel answer generation | Question and evidence | Explanation | Explain a documented CoC rule |
| OpenAI | Model API provider | Backend adapter only | Masked text | Answer or embeddings | Configured chat and embedding model IDs |
| Spring AI | Java integration framework | ChatClient, ChatModel, EmbeddingModel, ChatMemory, @Tool | Typed Java request | Model/retrieval/tool interactions | Register bounded read-only BusinessTools |
| Embedding | Numeric representation | Semantic candidate matching | Source passage | 1536 floats | Match PGNI process terminology |
| Vector database | Vector storage and distance search | PostgreSQL vector extension | Query vector | Neighbor passages | Cosine distance search with scope filters |
| RAG | Evidence retrieval before generation | KnowledgeStore + ChatTurns | Question | Cited answer or abstention | Explain PG/NI notification processing |
| Tool calling | Bounded live API execution | BusinessTools | UUID / fixed read operation | Authorized current fact | Latest attendance status |
| Chat memory | Bounded conversation context | OwnedChatMemory | Prior authorized turns | At most 20 messages / 8000 characters | Follow-up context; never identity |
| Relational database | Authoritative transactional storage | Domain schema | Validated command | Records and immutable history | Registration and instalments |

For **Explain PG/NI notification processing**, the supplied document describes notification validation, configured rule order, persistence, LAP publication using `XXAQ_SIS_COC_JMS_TXT_Q`, and Reg/ATN publication through sis-publisher (Ne.md:235–260). Retrieval supplies passages; the LLM explains them with identifiers and line references. These are documentary claims.

For **Latest attendance status UUID**, a bounded read-only tool calls the authenticated domain summary endpoint, propagating the user's validated bearer identity. The domain checks provider ownership. A successful live fact can answer without retrieved text and carries its source and as-of timestamp.

For **Why was this CoC rejected?**, the CoC history tool supplies the actual synthetic decision and the retrieved rule supplies explanatory context. A configured LLM can combine them, but cannot change the decision. Offline mode currently displays retrieved excerpts and supports explicit live status routing; it does not simulate real reasoning quality or autonomous tool choice.

Missing/unauthorized sources cause abstention. Unsupported citations cause an error. Valid citation IDs establish that a passage was retrieved, not that every sentence is logically supported; human groundedness review is still required. Indexing is not model training. Hashed offline embeddings are a deterministic test adapter, not OpenAI embeddings or evidence of semantic quality.
''')
w('docs/architecture.md','''# Architecture and decisions

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
''')
w('docs/rag-design.md','''# RAG and ingestion design

Ingestion validates filename, size, format and administrator privilege, parses safely, masks common secret/PII patterns, splits by heading and a CL100K token budget, hashes normalized content plus ACL and model, embeds batches of 32, then writes documents/chunks/vectors in a transaction. A per-name PostgreSQL advisory lock serializes publication. The source's active-version pointer changes only after all writes succeed. Failed writes leave the last usable version. Repeat content is idempotent, though embedding computation is currently performed before unchanged-version detection. Soft deletion and ACL checks immediately remove sources from search/citations.

Markdown is heading-aware. JavaParser validates Java syntax, JSqlParser validates supported SQL, SnakeYAML SafeConstructor limits aliases/depth, JDK StAX disables DTD/external entities, Properties uses the standard parser, and OpenAPI JSON must carry a version marker. These validators do not create a resolved call graph or faithfully parse Oracle PL/SQL. Raw source text retains original line references; parser-specific method/table chunking remains incomplete. Source fetching is absent, so no repository crawler or arbitrary URL input is enabled.

KnowledgeStore combines cosine candidates and PostgreSQL simple full-text/exact-substring candidates using reciprocal rank fusion (k=60), deduplicates IDs and returns at most six. Both branches filter active document version, provider, audience and embedding model before LIMIT. Exact identifiers preserve underscores. The 0.12 cosine cutoff is a provisional demo setting, not an empirically calibrated production threshold. Token chunk size is 600 with up to 80 tokens of whole-line overlap; oversized single lines split on Unicode code-point boundaries without truncation. Query/evidence budget and measured quality still need tuning.

Changing the embedding model requires re-ingesting every source. Different model IDs are never mixed in candidate selection. Dimensions are checked at 1536 and PostgreSQL enforces vector(1536). pgvector 0.8.1 HNSW supports this dimension with cosine distance. Old versions remain stored for review; soft deletion revokes access but does not erase bytes. Physical retention/purge jobs remain outstanding.

Before generation, the service authenticates, checks owned session, masks input, reads bounded authorized memory and retrieves evidence. Prompts distinguish instructions from untrusted evidence. Tools contain server-held identity, fixed routes, a five-call / 15-second budget and five-second read timeouts. Citation UUIDs must belong to retrieved passages and source access is rechecked before persistence. Citation checks do not establish semantic entailment. Logs record IDs, outcomes and latency rather than prompts/completions. Token usage is reported unavailable when not captured; no invented cost metrics are emitted.

Regex masking is a baseline, not a comprehensive PII classifier. Only curated sanitized source files belong in this demo; never upload transaction exports, real students or credentials. An external-model privacy review and stronger DLP controls remain necessary for real data.
''')
w('docs/security-model.md','''# Executable security and limitations

Local synthetic users: admin (ADMIN), advisor-a (ADVISOR/provider A), advisor-b (ADVISOR/provider B), developer (DEVELOPER/provider A), support (SUPPORT/provider A), reporter (REPORTER/provider A). The generated local password is supplied through SIS_DEMO_PASSWORD and hashed with BCrypt in the authentication service. JWT signing uses a generated 256-bit secret and a one-hour lifetime. Values stay in ignored .env. Browser tokens are in-memory; no credentials enter query strings or Vite variables.

Bearer-only APIs are stateless and disable CSRF because browsers do not automatically attach these Authorization headers. The Vite proxy gives one local origin; no permissive cross-origin policy is installed. Actuator health is public; other exposed metrics require authentication. Business writes require ADVISOR or ADMIN. Reports require REPORTER or ADMIN; operational reads require support/developer/admin. Scope checks precede object history, imports, exports, citations, tools and feedback. Conversation ownership is stricter than provider membership: another user in the same provider cannot read it.

PostgreSQL migration and runtime roles are separate. Knowledge runtime cannot update/delete audit rows; business runtime cannot update/delete immutable history. All data is fictitious. JPA's version field rejects stale commands, and idempotency receipts detect reused keys with changed payloads. Concurrent unique conflicts return HTTP 409. Transactions include command, history and outbox.

External OIDC proposal: replace the local issuer with a resource-server issuer-uri and validate audience; map only allowlisted IdP roles/providers server-side. Use Authorization Code + PKCE for the portal or a BFF with appropriate CSRF protection. This is a proposal, not an active corporate connection. No corporate IdP information is available.

Outstanding production controls include per-user rate limits, credential rotation/revocation, comprehensive PII classification, TLS ingress deployment, structured distributed traces, policy retention/purge, shared-secret replacement and model quality review. The demo is not presented as production-ready.
''')
w('docs/tool-catalogue.md','''# Typed read-only tools

All are new demo facades. Documentary legacy API mappings are in Ne.md:526–565 and 2658–2709. Provider is never a model argument. Each call forwards authenticated identity, the domain reauthorizes the object, and knowledge records outcome. Timeout: connect 2s/read 5s. Turn budget: at most five calls / 15s. No replay, write, stored procedure or funding tool exists.

| Tool | Input | Output / backing API | Permission / bound |
|---|---|---|---|
| applicationSummary | UUID | status, registration, attendance, source/asOf; GET applications/{id}/summary | Object provider; one record |
| academicYears | none | GET academic-years | Authenticated; seeded reference list |
| courseOptions | none | GET courses | Provider scope; seeded reference list |
| referenceData | none | GET reference-data | Authenticated; fixed categories |
| confirmationHistory | UUID | GET applications/{id}/history | Object provider; history projection |
| cocHistory | UUID | GET applications/{id}/cocs | Object provider; synthetic decisions |
| tuitionPeriods | UUID | GET applications/{id}/tuition-periods | Object provider; synthetic finance projection |
| featureToggles | none | GET operations/toggles | Support/developer/admin; fixed allowlist |
| syncExceptions | none | GET operations/exceptions | Support/developer/admin; maximum 50 masked rows |

Application search as a masked model tool, course detail tool, separate CoC dry-run callback and full reference-domain catalogue remain incomplete. Normal portal APIs provide application search and side-effect-free CoC preview. Offline mode does not run an autonomous LLM tool loop; explicit current-status routing demonstrates real API reads.
''')
# Generate deployment-independent API contract directly from executable mappings and DTO source.
contracts=[]
for p in (r/'backend').glob('*/src/main/java/edu/sis/**/*.java'):
 text=p.read_text();base=re.search(r'@RequestMapping\("([^"]+)"\)',text)
 for m in re.finditer(r'@(Get|Post|Put|Delete)Mapping\((?:value=)?"([^"]+)"',text):contracts.append((m.group(1).upper(),(base.group(1) if base else '')+m.group(2),str(p.relative_to(r))))
w('docs/api-contracts.md','''# Demo API contracts

Routes below are new `/api/v1` contracts, not claims about legacy endpoint availability. Login is the only unauthenticated business API. Use Authorization: Bearer TOKEN; no credential query strings. APIs return 400 for validation, 401 unauthenticated, 403 role denial, 404 absent or hidden object, 409 idempotency/version conflict, 422 deterministic business rejection where applicable, 429 concurrency limit and 503 AI/dependency failure. Validation errors contain message, fieldErrors and correlationId; authentication/transport errors are still framework responses and normalization is pending.

Application create example:
```json
{"provider":"A","sourceRef":"DEMO-001","studentName":"Fictional Learner","source":"LLE","courseId":"A-CS","academicYear":2026,"fee":9250.01}
```
Supply a unique `Idempotency-Key`. A retry with identical payload returns the same ID; changed payload with that key returns 409. Response is a bounded application DTO with UUID, provider, statuses, fee, currency, version and timestamps. `GET applications?q=&page=0&size=20` permits size 1..50, page 0..10000 and query up to 100 characters. Sorting is fixed by created time and ID.

Confirmation: `{"version":0,"status":"CONFIRMED"}`. CoC: `{"version":2,"type":"SUSPENSION","effectiveDate":"2026-01-01"}`. Preview performs no writes. Invalid submitted CoCs retain a REJECTED decision and return 200 with that business status; invalid HTTP/schema input returns 400. Clients must inspect business status. Monetary requests use decimal values and server-side BigDecimal.

Chat stream uses authenticated POST `/api/v1/ai/sessions/{id}/messages/stream` with `{"question":"Explain PGNI processing"}`. Events: start (turnId), delta, complete (answer/citations/liveFacts/mode), failure. `POST ai/turns/{id}/cancel` authorizes session ownership. `scripts/verify.py` is an executable request/response walkthrough without printing credentials.

| Method | Path | Implementation |
|---|---|---|
'''+''.join(f'| {m} | `{p}` | `{s}` |\n' for m,p,s in sorted(contracts))+'''
DTO definitions: backend/sis-domain-service/.../Contracts.java and knowledge KnowledgeController records. Generated OpenAPI is not yet installed. Complete examples for all endpoints and cursor/sorting extensions remain pending.
''')
print(len(items),'inventory names,',len(contracts),'API routes')
