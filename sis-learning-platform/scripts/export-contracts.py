"""Exports generated OpenAPI and actual demo schema metadata without credential output."""
import json,subprocess,urllib.request
from pathlib import Path
root=Path(__file__).resolve().parents[1];config=dict(x.split('=',1) for x in (root/'.env').read_text().splitlines() if x and not x.startswith('#'));client=urllib.request.build_opener(urllib.request.ProxyHandler({}))
req=urllib.request.Request('http://127.0.0.1:8091/api/v1/auth/login',data=json.dumps({'username':'admin','password':config['SIS_DEMO_PASSWORD']}).encode(),headers={'Content-Type':'application/json'})
with client.open(req) as res:token=json.load(res)['token']
for name,port in [('domain',8091),('knowledge',8092)]:
 req=urllib.request.Request(f'http://127.0.0.1:{port}/v3/api-docs',headers={'Authorization':'Bearer '+token})
 with client.open(req) as res:document=json.load(res)
 (root/f'docs/openapi-{name}.json').write_text(json.dumps(document,indent=2))
query="SELECT jsonb_pretty(jsonb_agg(to_jsonb(c))) FROM (SELECT table_schema,table_name,column_name,data_type,is_nullable,column_default FROM information_schema.columns WHERE table_schema IN ('business','knowledge') ORDER BY table_schema,table_name,ordinal_position)c"
result=subprocess.run(['docker','compose','exec','-T','postgres','psql','-U','postgres','-d','sis','-At','-c',query],cwd=root,capture_output=True,text=True,check=True)
columns=json.loads(result.stdout);(root/'docs/implemented-schema.json').write_text(json.dumps(columns,indent=2));tables={}
for c in columns:tables.setdefault(c['table_schema']+'.'+c['table_name'],[]).append(c)
text='# Implemented PostgreSQL data dictionary\n\nThis is the synthetic implementation, extracted from the running integration database. Documentary legacy fields are in data-dictionary.md. Migrations define keys, indexes, foreign keys and runtime grants; no legacy fidelity is implied.\n\n'
for table,cols in tables.items():
 text+='## '+table+'\n\n| Column | PostgreSQL type | Nullable | Default |\n|---|---|---|---|\n'
 for c in cols:text+=f"| {c['column_name']} | {c['data_type']} | {c['is_nullable']} | {c['column_default'] or ''} |\n"
 text+='\n'
text+='\nOwnership: business tables belong to the domain service, knowledge tables to the knowledge service. Provider-scoped records carry provider directly or inherit it through their authorized parent. Application version is optimistic concurrency state; immutable histories are append-only to runtime roles. UUIDs identify synthetic records. Money is decimal(12,2) GBP. Timestamps are time-zone-aware except date-only business dates. Source/document/chunk relations preserve active versions; chat session deletion cascades messages/provenance/feedback/turns. No automatic application deletion cascade exists. Source deletion is currently logical; physical retention cleanup remains pending.\n'
(root/'docs/implemented-data-dictionary.md').write_text(text)
print('Exported two OpenAPI documents and',len(tables),'schema tables.')
