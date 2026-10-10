"""Non-destructive to existing employees; creates and removes one test record."""
import json, os, time, urllib.request, urllib.error
base=os.environ.get('BACKEND_URL','http://localhost:8080').rstrip('/')
def request(path,method='GET',data=None):
    req=urllib.request.Request(base+path,method=method,data=json.dumps(data).encode() if data is not None else None,headers={'Content-Type':'application/json'})
    try:
        with urllib.request.urlopen(req,timeout=5) as r:
            body=r.read(); return r.status,json.loads(body) if body else None
    except urllib.error.HTTPError as e:
        return e.code,json.load(e)
for attempt in range(90):
    try:
        status,health=request('/actuator/health')
        if status==200 and health['status']=='UP': break
    except (OSError,ValueError): pass
    time.sleep(1)
else: raise RuntimeError('Backend health failed')
record={'firstName':'Rahul','lastName':'Sharma','age':28,'address':'Pune, Maharashtra'}
status,created=request('/api/employees','POST',record); assert status==201,(status,created)
path='/api/employees/'+str(created['id'])
try:
    assert request(path)[1]['age']==28
    assert any(e['id']==created['id'] for e in request('/api/employees')[1])
    record['age']=29
    assert request(path,'PUT',record)[1]['age']==29
    assert request(path)[1]['age']==29
    for invalid in [{**record,'age':-1},{**record,'firstName':''},{**record,'age':'invalid'}]:
        assert request('/api/employees','POST',invalid)[0]==400
finally:
    assert request(path,'DELETE')[0]==204
assert request(path)[0]==404
print('PASS: API create, list, read, update, validation, delete and health')
