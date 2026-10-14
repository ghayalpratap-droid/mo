"""Idempotent synthetic sources and two demo applications; no external production calls."""
import json,urllib.request,urllib.error
from pathlib import Path
root=Path(__file__).resolve().parents[1]
cfg=dict(x.split('=',1) for x in (root/'.env').read_text().splitlines() if x and not x.startswith('#'))
client=urllib.request.build_opener(urllib.request.ProxyHandler({}))
def call(port,path,data,token=None,key=None):
 headers={'Content-Type':'application/json'}
 if token:headers['Authorization']='Bearer '+token
 if key:headers['Idempotency-Key']=key
 req=urllib.request.Request(f'http://127.0.0.1:{port}/api/v1'+path,data=json.dumps(data).encode(),headers=headers)
 with client.open(req,timeout=45) as r:return json.load(r)
admin=call(8091,'/auth/login',{'username':'admin','password':cfg['SIS_DEMO_PASSWORD']})['token']
for provider,course in [('A','A-CS'),('B','B-DS')]:
 call(8091,'/applications',{'provider':provider,'courseId':course,'sourceRef':'DEMO-SEED-'+provider,'studentName':'Fictitious Learner '+provider,'source':'LLE','academicYear':2026,'fee':9250.01},admin,'demo-seed-'+provider)
for file in (root/'samples').glob('*.md'):
 call(8092,'/ai/sources',{'name':file.name,'text':file.read_text(),'provider':'ALL','audience':'PUBLIC'},admin)
print('Synthetic applications and curated documentary source are available.')
