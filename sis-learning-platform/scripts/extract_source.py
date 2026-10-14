from pathlib import Path
import re,csv,json,hashlib
root=Path(__file__).resolve().parents[1]; lines=(root/'Ne.md').read_text().splitlines()
def write(p,s):
 f=root/p;f.parent.mkdir(parents=True,exist_ok=True);f.write_text(s)
write('docs/source-assessment.md','''# Source assessment

The supplied Ne.md is documentary evidence, not inspected legacy source. Its SHA-256 is `'''+hashlib.sha256((root/'Ne.md').read_bytes()).hexdigest()+'''`.

| Evidence | Classification | Source |
|---|---|---|
| Java 8, Boot 2.3.12, JSF, Oracle | DOCUMENTED_CURRENT | Ne.md:9–21, 1321–1380 |
| RabbitMQ inbound and Oracle AQ/JMS outbound | DOCUMENTED_CURRENT | Ne.md:15, 1621–1657 |
| React, Spring AI, OpenAI, pgvector | DOCUMENTED_PROPOSAL | Ne.md:592–735, 1773–2145 |
| Kafka | DOCUMENTED_PROPOSAL; explicitly absent in current-state analysis | Ne.md:1623–1632, 2710 onward |
| Entity counts | Documentary counts, not complete field specifications | Ne.md:3194–3239 |
| Schema ownership | Inferred in the supplied index | Ne.md:4762–4774 |
| Synthetic providers, students, eligibility and finance rules | DEMO_ASSUMPTION | This project's implementation |
| Production PL/SQL, Mule contracts, shared-library internals | UNKNOWN | Ne.md:3177–3187 |

Part 1 is empty (3220–3226). Part 2 supplies notes but no complete definitions (3227–3240). The introduction is repeated at 4803. TuitionFeeForecast is an inactive JPA mapping (3233), though a legacy projection of the same name is described at 3960. AwardAuthority and AcademicYear have alternative JLBHEI/JAVHEP mappings; these must not be collapsed into a single verified class definition. Assertions within the source that all entities were read refer to the document author's work, not to evidence available here.

The complete supplied text is retained at the project root for line references. It is not automatically sent to an external model. Source files and transactional data are separate trust boundaries.
''')
write('docs/assumptions-and-gaps.md','''# Assumptions and ambiguity register

| ID | Gap or conflict | Resolution |
|---|---|---|
| G01 | No production source checkout | Educational reconstruction; never claim production fidelity |
| G02 | Empty LLE entity detail section | Inventory named tables; synthetic models have separate names |
| G03 | Shared library 2.0.23/24 unavailable | Do not infer missing fields from repository names |
| G04 | Unavailable stored procedures and Mule RAML | Named local integration simulators; no Oracle verification claim |
| G05 | Overlapping Azure/OpenAI and Kafka target proposals | OpenAI backend adapter; broker-free local outbox simulator; Kafka remains optional proposal |
| G06 | Corporate identity unknown | Executable local synthetic accounts; external OIDC is a separate deployment design |
| G07 | Monetary policy missing | DEMO: GBP three instalments 25%,25%,remaining; HALF_UP cents; never official policy |
| G08 | State transition rules incomplete | DEMO: registration before attendance; active attendance before suspension; resumption from suspended only |
| G09 | Owner inferences and duplicate mappings | Keep source occurrences and mapping alternatives in inventory |
| G10 | AI credentials unavailable | Offline mode visibly simulated; live OpenAI needs backend-only configuration |
| G11 | Documentary proposed vector DDL has extra NOT NULL/FK fields | Use explicit JDBC writes in one transaction; do not use incompatible automatic vector-store insert SQL |
| G12 | PUBLIC revoke does not constrain table owner | Separate migration and runtime roles required for integration audit immutability |
| G13 | Regex discovery cannot prove complete symbol coverage | Preserve candidate ledger with occurrences and review status; do not claim complete semantic extraction |

Application source values UG, PGL, PGD, PGNI, APPLY_ONCE and LLE are synthetic transport categories. No production eligibility, liability, BCF or bursary decisions are reproduced.
''')
# Preserve all sections with exact lines so no field table is discarded by extraction.
sections=[]; start=0; heading='Preamble'
for i,line in enumerate(lines):
 if re.match(r'^#{1,6} ',line):
  if i>start:sections.append(dict(heading=heading,start_line=start+1,end_line=i,text='\n'.join(lines[start:i])))
  start=i;heading=line.lstrip('# ')
sections.append(dict(heading=heading,start_line=start+1,end_line=len(lines),text='\n'.join(lines[start:])))
write('docs/source-sections.json',json.dumps(sections,indent=2,ensure_ascii=False))
# Extract table names, class identifiers and explicitly listed DTOs; conservatively retain candidates.
items={}
def add(name,line,kind,fields=''):
 name=name.strip('` *');
 if not name:return
 x=items.setdefault(name,dict(source_name=name,aliases='',source_heading='',source_lines=[],classification='',module='',table_schema='',fields_known='',fields_unknown='Unspecified source fields and constraints',primary_key='UNKNOWN',relationships='See referenced source section',implementation_name='',mapping_type=kind,completeness='PENDING_REVIEW',test_coverage='UNRUN',limitation='Documentary mapping; no legacy source available'))
 x['source_lines'].append(line)
 x['classification']='DOCUMENTED_PROPOSAL' if 592<=line<1297 or 1773<=line<3189 else 'DOCUMENTED_CURRENT'
 if fields:x['fields_known']=fields
 if kind=='table':x['table_schema']=name+'; owner inferred unless explicit'
 if 'TuitionFeeForecast' in name or name=='TUITION_FEE_FORECASTS':x['mapping_type']='inactive mapping / legacy projection alternative'
for sec in sections:
 for off,line in enumerate(sec['text'].splitlines()):
  n=sec['start_line']+off
  for name in re.findall(r'\b(?:XX[A-Z]{2}_[A-Z0-9_]+|[A-Z][A-Z0-9]*(?:_[A-Z0-9]+){1,})\b',line):
   if any(k in name for k in ('_SEQ','_IND','_TYPE')):continue
   add(name,n,'queue/non-entity' if '_JMS_' in name else 'table')
  for name in re.findall(r'\b[A-Z][a-z][A-Za-z0-9]*(?:POJO|Pojo|DTO|Dto|Enum|EnumType|Request|Response|History|Hist|PK|Id)\b',line):add(name,n,'DTO/enum/entity candidate')
  if n>=3189 and line.startswith('|'):
   cols=[x.strip() for x in line.strip('|').split('|')]
   if len(cols)>1 and re.match(r'(?:[a-z][\w]*\.)*[A-Z][a-zA-Z0-9]+',cols[0]) and not any(w in cols[0] for w in ['Repository','DAO']):
    for name in re.findall(r'\b[A-Z][a-z][A-Za-z0-9]+\b',cols[0]):add(name,n,'DTO/entity candidate',cols[1])
  if line.startswith('#') and '→' in line:
   name=re.search(r'#+\s+(\w+)',line).group(1);add(name,n,'reference entity',sec['text'])
  for name in re.findall(r'CREATE TABLE ([a-z_]+)',line):add(name,n,'proposed AI table',sec['text'])
for name,x in items.items():
 ns=sorted(set(x['source_lines']));x['source_lines']=';'.join(map(str,ns));sec=next(s for s in sections if s['start_line']<=ns[0]<=s['end_line']);x['source_heading']=sec['heading']
 lower=name.lower()
 x['module']=next((m for keys,m in [('coc','CoC'),('import bulk stage','Imports'),('payment instalment funding fee','Finance'),('course module study','Catalogue'),('regist attendance confirmation','Confirmations'),('customer student address','Students'),('rule validation rca page component','Validation'),('notification publisher outbox queue','Integration'),('chat document embedding source ingestion feedback agent prompt retrieval tool','Knowledge'),('user group','Identity'),('sync exception report','Operations')] if any(k in lower for k in keys.split())),'Reference/application')
with (root/'docs/entity-inventory.csv').open('w') as f:
 w=csv.DictWriter(f,fieldnames=list(next(iter(items.values())).keys()));w.writeheader();w.writerows(items.values())
write('docs/data-dictionary.md','# Documentary data dictionary\n\nExtracted sections preserve all supplied fields, types, keys and relations. These are not implementation claims. Candidate inventory requires reconciliation; UNKNOWN means absent evidence.\n\n'+'\n\n'.join(f"## {s['heading']} (Ne.md:{s['start_line']}–{s['end_line']})\n\n{s['text']}" for s in sections if s['start_line']>=3241 and s['start_line']<4803))
mods=['Identity and access','Provider and reference data','Students','Applications','Course catalogue','Tuition and finance','Registration and attendance','CoC','Configured validation','Payments','CSV imports','Publisher and integrations','Reports and operations','Knowledge and AI']
write('docs/coverage-plan.md','# Complete capability coverage plan\n\nEach row remains in scope. Implementation and tests must be recorded in traceability.csv; a plan is not completed functionality.\n\n| Module | Required implementation | Acceptance evidence |\n|---|---|---|\n'+'\n'.join('| '+x+' | Typed API, server authorization, persistence, UI, audit, documented source mapping | Unit/API/integration/UI tests; failure and denial paths |' for x in mods)+'\n\nSource-specific flows: UG/EDS intake, PG/NI notification, Apply Once, LLE intake/catalogue, attendance-to-BAS, bulk CoC, PG CoC-to-BCF, registration CSV, course closure, disposition, validation/search, negative-balance reporting. External adapters are simulators with explicit contract gaps. Every source candidate has a row in entity-inventory.csv; unsupported mappings stay pending rather than being claimed as six generic entities.\n')
with (root/'docs/traceability.csv').open('w') as f:
 w=csv.writer(f);w.writerow(['capability','implementation','API_UI','test','status']);w.writerows([x,'','','','PLANNED'] for x in mods)
write('docs/version-matrix.md','''# Version matrix

Verification date: 2026-10-14.

| Component | Pin | Evidence |
|---|---|---|
| Java | 21 | Installed OpenJDK 21.0.12.1 |
| Spring Boot | 3.5.16 | Maven Central; same supported minor as Spring AI build |
| Spring AI | 1.1.8 BOM | Official tagged getting-started.adoc supports Boot 3.4.x/3.5.x; tagged pom uses Boot 3.5.15 |
| Gradle | 8.14.3 | Official distribution with SHA-256 verification |
| React | 19.1.1 | Exact package and lockfile |
| TypeScript | 5.9.3 | Exact package and lockfile |
| Vite | 7.3.7 | Exact package and lockfile |
| Node | 24.19.0 | Installed runtime; .node-version |
| PostgreSQL / pgvector | 17 / 0.8.1 | pgvector/pgvector:0.8.1-pg17, resolve image digest during setup |

Official references: https://raw.githubusercontent.com/spring-projects/spring-ai/v1.1.8/spring-ai-docs/src/main/antora/modules/ROOT/pages/getting-started.adoc and https://raw.githubusercontent.com/spring-projects/spring-ai/v1.1.8/pom.xml . docs.spring.io returned proxy 403; tagged official GitHub sources were fetched successfully. Compilation remains the compatibility gate. No live OpenAI verification is implied.
''')
print(len(items),'inventory candidates;',len(sections),'source sections')
