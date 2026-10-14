"""Opt-in paid external-model smoke; never reads or prints the provider key."""
import argparse,json,urllib.request
from pathlib import Path
parser=argparse.ArgumentParser();parser.add_argument('--allow-external-model',action='store_true');args=parser.parse_args()
if not args.allow_external_model:parser.error('Pass --allow-external-model only after secure key configuration and approval of external model usage.')
root=Path(__file__).resolve().parents[1];cfg=dict(x.split('=',1) for x in (root/'.env').read_text().splitlines() if x and not x.startswith('#'));client=urllib.request.build_opener(urllib.request.ProxyHandler({}))
def request(path,data=None,token=None):
 req=urllib.request.Request('http://127.0.0.1:8092'+path,data=None if data is None else json.dumps(data).encode(),headers={'Content-Type':'application/json',**({'Authorization':'Bearer '+token} if token else {})})
 with client.open(req,timeout=45) as res:return json.load(res)
token=request('/api/v1/auth/login',{'username':'advisor-a','password':cfg['SIS_DEMO_PASSWORD']})['token']
status=request('/api/v1/ai/status',token=token)
assert status['mode']=='openai','Start knowledge service with local profile; offline output cannot pass this smoke'
session=request('/api/v1/ai/sessions',{},token)['id'];answer=request('/api/v1/ai/sessions/'+session+'/messages',{'question':'Explain documented PGNI notification processing and identify unknown production details.'},token)
assert answer['mode']=='openai' and answer['citations'] and not answer['abstained']
assert not answer['answer'].startswith('SIMULATED')
(root/'evaluation/latest-live-model.json').write_text(json.dumps({'status':'PASSED','sessionId':session,'citationCount':len(answer['citations']),'semanticSupport':'Requires human review; smoke is not a quality benchmark'},indent=2))
print('PASS: configured live adapter answered with validated citation IDs; review semantic support separately.')
