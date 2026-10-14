from pathlib import Path
import csv,json,re
root=Path(__file__).resolve().parents[1];sections=json.loads((root/'docs/source-sections.json').read_text());alternatives=[];context=''
for section in sections:
 heading=section['heading']
 if re.match(r'\d+\.\d+ ',heading):context=heading
 match=re.search(r'^([A-Za-z0-9]+).*?→\s*([A-Z][A-Z0-9_.]+)',heading)
 if match:
  alternatives.append({'source_class':match[1],'table':match[2].split('.')[-1],'documentary_context':context,'heading':heading,'start_line':section['start_line'],'end_line':section['end_line'],'definition':section['text'],'classification':'DOCUMENTED_CURRENT','limitation':'Alternative documentary mapping; legacy source unavailable'})
with (root/'docs/mapping-alternatives.csv').open('w') as f:
 writer=csv.DictWriter(f,fieldnames=list(alternatives[0]));writer.writeheader();writer.writerows(alternatives)
rows=list(csv.DictReader((root/'docs/entity-inventory.csv').open()));groups={}
for alt in alternatives:groups.setdefault(alt['table'],set()).add(alt['source_class'])
for row in rows:
 matches=[a for a in alternatives if a['source_class']==row['source_name']]
 if matches:
  table=matches[0]['table'];row['aliases']=';'.join(sorted(groups[table]-{row['source_name']}));row['table_schema']=table+'; source owner inference preserved'
  definitions='\n'.join(a['definition'] for a in matches)
  keys=re.findall(r'(?:\*\*Id:\*\*|Id:|\*\*Composite id:\*\*)[^\n]*',definitions)
  if keys:row['primary_key']='; '.join(dict.fromkeys(keys))
  relationships=[line for line in definitions.splitlines() if any(word in line for word in ['many-to-one','one-to-one','bag →','set →'])]
  if relationships:row['relationships']='\n'.join(dict.fromkeys(relationships))
  if len(matches)>1:row['fields_known']='\n\n'.join(f"Ne.md:{a['start_line']} {a['documentary_context']}\n{a['definition']}" for a in matches);row['limitation']+='; alternative definitions preserved in mapping-alternatives.csv'
 elif row['source_name'] in groups:row['aliases']=';'.join(sorted(groups[row['source_name']]))
with (root/'docs/entity-inventory.csv').open('w') as f:
 writer=csv.DictWriter(f,fieldnames=list(rows[0]));writer.writeheader();writer.writerows(rows)
print('Preserved',len(alternatives),'documentary class/table mapping occurrences')
