"""Measures this fixture's offline retrieval only; does not claim live-model groundedness."""
import json,time,urllib.request,statistics
from pathlib import Path
root=Path(__file__).resolve().parents[1];config=dict(x.split('=',1) for x in (root/'.env').read_text().splitlines() if x and not x.startswith('#'))
client=urllib.request.build_opener(urllib.request.ProxyHandler({}))
def call(path,data,token=None):
 req=urllib.request.Request('http://127.0.0.1:8092'+path,data=json.dumps(data).encode(),headers={'Content-Type':'application/json',**({'Authorization':'Bearer '+token} if token else {})})
 with client.open(req,timeout=45) as res:return json.load(res)
token=call('/api/v1/auth/login',{'username':'advisor-a','password':config['SIS_DEMO_PASSWORD']})['token'];records=[]
for fixture in json.loads((root/'evaluation/questions.json').read_text()):
 start=time.perf_counter();result=call('/api/v1/ai/search',{'question':fixture['question']},token);elapsed=(time.perf_counter()-start)*1000
 docs=list(dict.fromkeys(x['source'] for x in result));expected=fixture['expectedDocuments'];recall=sum(d in docs for d in expected)/len(expected) if expected else None
 records.append({'id':fixture['id'],'latency_ms':round(elapsed,2),'retrieved_documents':docs,'recall_at_6':recall,'empty_retrieval':not result,'citation_ids_valid':all('id' in x and x['startLine']<=x['endLine'] for x in result),'expected_behavior':fixture['expectedBehavior'],'semantic_support_review':'UNMEASURED'})
positive=[x['recall_at_6'] for x in records if x['recall_at_6'] is not None]
report={'mode':'offline-demo','scope':'small curated fixture; not production benchmark','mean_recall_at_6':statistics.mean(positive),'records':records,'live_model_groundedness':'UNMEASURED','citation_entailment':'UNMEASURED; identifier validity is not logical support','provider_token_usage':None,'provider_cost':None,'p95_claim':'Not statistically meaningful at ten queries'}
(root/'evaluation/latest-retrieval.json').write_text(json.dumps(report,indent=2));print('Measured offline retrieval recall@6:',report['mean_recall_at_6']);print('Live model groundedness, tokens and cost remain unmeasured.')
