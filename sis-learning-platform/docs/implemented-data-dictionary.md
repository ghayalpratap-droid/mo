# Implemented PostgreSQL data dictionary

This is the synthetic implementation, extracted from the running integration database. Documentary legacy fields are in data-dictionary.md. Migrations define keys, indexes, foreign keys and runtime grants; no legacy fidelity is implied.

## business.academic_years

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | integer | NO |  |
| start_date | date | NO |  |
| end_date | date | NO |  |
| active | boolean | NO |  |

## business.applications

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| provider | character varying | NO |  |
| source_ref | character varying | NO |  |
| student_name | character varying | NO |  |
| source | character varying | NO |  |
| course_id | character varying | NO |  |
| academic_year | integer | NO |  |
| fee | numeric | NO |  |
| currency | character varying | NO |  |
| status | character varying | NO |  |
| registration | character varying | NO |  |
| attendance | character varying | NO |  |
| version | bigint | NO |  |
| updated_at | timestamp with time zone | NO |  |
| created_at | timestamp with time zone | NO |  |
| credits | integer | NO | 120 |
| student_id | uuid | YES |  |

## business.campuses

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | character varying | NO |  |
| provider | character varying | NO |  |
| name | character varying | NO |  |

## business.cocs

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| application_id | uuid | NO |  |
| provider | character varying | NO |  |
| type | character varying | NO |  |
| status | character varying | NO |  |
| reason | character varying | NO |  |
| effective_date | date | NO |  |
| created_at | timestamp with time zone | NO |  |

## business.course_modules

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| course_id | character varying | NO |  |
| module_id | character varying | NO |  |

## business.course_variants

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| course_id | character varying | NO |  |
| campus_id | character varying | NO |  |
| academic_year | integer | NO |  |
| qualification_id | character varying | NO |  |

## business.courses

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | character varying | NO |  |
| provider | character varying | NO |  |
| title | character varying | NO |  |
| credits | integer | NO |  |

## business.feature_toggles

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| name | character varying | NO |  |
| toggle_value | character varying | NO |  |

## business.flyway_schema_history

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| installed_rank | integer | NO |  |
| version | character varying | YES |  |
| description | character varying | NO |  |
| type | character varying | NO |  |
| script | character varying | NO |  |
| checksum | integer | YES |  |
| installed_by | character varying | NO |  |
| installed_on | timestamp without time zone | NO | now() |
| execution_time | integer | NO |  |
| success | boolean | NO |  |

## business.histories

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| application_id | uuid | NO |  |
| provider | character varying | NO |  |
| event | character varying | NO |  |
| detail | character varying | NO |  |
| actor | character varying | NO |  |
| occurred_at | timestamp with time zone | NO |  |

## business.import_jobs

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| provider | character varying | NO |  |
| status | character varying | NO |  |
| created_at | timestamp with time zone | NO |  |

## business.import_rows

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| job_id | uuid | NO |  |
| row_number | integer | NO |  |
| status | character varying | NO |  |
| detail | character varying | NO |  |

## business.instalments

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| application_id | uuid | NO |  |
| provider | character varying | NO |  |
| number | integer | NO |  |
| amount | numeric | NO |  |
| currency | character varying | NO |  |
| due_date | date | NO |  |

## business.modules

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | character varying | NO |  |
| provider | character varying | NO |  |
| title | character varying | NO |  |
| credits | integer | NO |  |

## business.outbox_events

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| provider | character varying | NO |  |
| aggregate_id | uuid | NO |  |
| event_type | character varying | NO |  |
| status | character varying | NO |  |
| attempts | integer | NO |  |
| created_at | timestamp with time zone | NO |  |
| target | character varying | NO | 'MULE'::character varying |
| next_attempt | timestamp with time zone | NO | CURRENT_TIMESTAMP |
| last_error | character varying | YES |  |

## business.processed_events

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| event_id | uuid | NO |  |
| receiver | character varying | NO |  |
| processed_at | timestamp with time zone | NO |  |

## business.providers

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | character varying | NO |  |
| name | character varying | NO |  |

## business.qualifications

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | character varying | NO |  |
| title | character varying | NO |  |

## business.request_receipts

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| provider | character varying | NO |  |
| request_key | character varying | NO |  |
| fingerprint | character varying | NO |  |
| result_id | uuid | NO |  |

## business.rule_configs

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | character varying | NO |  |
| description | character varying | NO |  |
| enabled | boolean | NO |  |
| policy | character varying | NO |  |

## business.students

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| provider | character varying | NO |  |
| demo_label | character varying | NO |  |
| created_at | timestamp with time zone | NO |  |

## business.subscriptions

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| provider | character varying | NO |  |
| academic_year | integer | NO |  |
| service | character varying | NO |  |
| active | boolean | NO |  |

## business.sync_exceptions

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| provider | character varying | NO |  |
| service | character varying | NO |  |
| error_code | character varying | NO |  |
| message_masked | character varying | NO |  |
| created_at | timestamp with time zone | NO |  |

## business.tuition_finance_details

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| tuition_period_id | uuid | NO |  |
| fee | numeric | NO |  |
| currency | character varying | NO |  |
| credits | integer | NO |  |

## business.tuition_period_histories

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| tuition_period_id | uuid | NO |  |
| status | character varying | NO |  |
| fee | numeric | NO |  |
| credits | integer | NO |  |
| event | character varying | NO |  |
| occurred_at | timestamp with time zone | NO |  |

## business.tuition_periods

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| application_id | uuid | NO |  |
| provider | character varying | NO |  |
| start_date | date | NO |  |
| end_date | date | NO |  |
| status | character varying | NO |  |

## knowledge.agent_runs

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| session_id | uuid | YES |  |
| status | character varying | NO |  |
| created_at | timestamp with time zone | NO | now() |

## knowledge.audit_logs

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| actor | character varying | NO |  |
| event_type | character varying | NO |  |
| target_id | uuid | YES |  |
| created_at | timestamp with time zone | NO | now() |

## knowledge.chat_messages

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| session_id | uuid | NO |  |
| role | character varying | NO |  |
| content_masked | text | NO |  |
| status | character varying | NO |  |
| created_at | timestamp with time zone | NO | now() |
| message_order | bigint | NO | nextval('knowledge.chat_messages_message_order_seq'::regclass) |

## knowledge.chat_sessions

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| owner | character varying | NO |  |
| provider | character varying | NO |  |
| created_at | timestamp with time zone | NO | now() |

## knowledge.chat_turns

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| session_id | uuid | NO |  |
| status | character varying | NO |  |
| created_at | timestamp with time zone | NO | now() |

## knowledge.code_relations

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| from_document_id | uuid | YES |  |
| to_symbol | character varying | NO |  |
| relation_type | character varying | NO |  |

## knowledge.document_chunks

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| document_id | uuid | NO |  |
| heading | character varying | NO |  |
| start_line | integer | NO |  |
| end_line | integer | NO |  |
| content | text | NO |  |
| content_tsv | tsvector | YES |  |

## knowledge.documents

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| source_id | uuid | NO |  |
| content_hash | character varying | NO |  |
| model | character varying | NO |  |
| created_at | timestamp with time zone | NO | now() |

## knowledge.embeddings

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| embedding | USER-DEFINED | NO |  |
| model | character varying | NO |  |

## knowledge.feedback

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| message_id | uuid | NO |  |
| owner | character varying | NO |  |
| rating | integer | NO |  |

## knowledge.flyway_schema_history

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| installed_rank | integer | NO |  |
| version | character varying | YES |  |
| description | character varying | NO |  |
| type | character varying | NO |  |
| script | character varying | NO |  |
| checksum | integer | YES |  |
| installed_by | character varying | NO |  |
| installed_on | timestamp without time zone | NO | now() |
| execution_time | integer | NO |  |
| success | boolean | NO |  |

## knowledge.ingestion_jobs

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| source_name | character varying | NO |  |
| status | character varying | NO |  |
| detail | character varying | NO |  |
| created_at | timestamp with time zone | NO | now() |

## knowledge.message_sources

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| message_id | uuid | NO |  |
| chunk_id | uuid | NO |  |

## knowledge.outbox_events

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| event_type | character varying | NO |  |
| status | character varying | NO |  |
| created_at | timestamp with time zone | NO | now() |

## knowledge.processed_events

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| event_id | uuid | NO |  |
| processed_at | timestamp with time zone | NO | now() |

## knowledge.prompt_templates

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| name | character varying | NO |  |
| version | integer | NO |  |
| template | text | NO |  |

## knowledge.repositories

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| name | character varying | NO |  |
| active | boolean | NO | true |

## knowledge.retrieval_logs

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| actor | character varying | NO |  |
| chunk_ids | jsonb | NO |  |
| latency_ms | bigint | NO |  |
| created_at | timestamp with time zone | NO | now() |

## knowledge.source_files

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| name | character varying | NO |  |
| provider | character varying | NO |  |
| audience | character varying | NO |  |
| active_version | uuid | YES |  |
| deleted | boolean | NO | false |
| updated_at | timestamp with time zone | NO | now() |

## knowledge.tool_invocations

| Column | PostgreSQL type | Nullable | Default |
|---|---|---|---|
| id | uuid | NO |  |
| actor | character varying | NO |  |
| tool_name | character varying | NO |  |
| outcome | character varying | NO |  |
| created_at | timestamp with time zone | NO | now() |


Ownership: business tables belong to the domain service, knowledge tables to the knowledge service. Provider-scoped records carry provider directly or inherit it through their authorized parent. Application version is optimistic concurrency state; immutable histories are append-only to runtime roles. UUIDs identify synthetic records. Money is decimal(12,2) GBP. Timestamps are time-zone-aware except date-only business dates. Source/document/chunk relations preserve active versions; chat session deletion cascades messages/provenance/feedback/turns. No automatic application deletion cascade exists. Source deletion is currently logical; physical retention cleanup remains pending.
