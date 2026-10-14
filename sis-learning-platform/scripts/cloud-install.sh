#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mkdir -p /workspace/tools
if [ ! -x /workspace/tools/jdk-21/bin/javac ]; then
 image=eclipse-temurin:21-jdk@sha256:f9bf7e9fe2f9bc4657537c1a7e20c768f0c3bafca41598263be4714783dd62eb
 docker pull "$image"
 toolchain_id=$(docker create "$image")
 docker cp "$toolchain_id":/opt/java/openjdk /workspace/tools/jdk-21
 docker rm "$toolchain_id" >/dev/null
fi
export JAVA_HOME=/workspace/tools/jdk-21
export GRADLE_USER_HOME=/workspace/tools/sis-gradle
python3 - <<'PY'
import urllib.parse,os,pathlib
u=urllib.parse.urlsplit(os.environ.get('HTTPS_PROXY',os.environ.get('https_proxy','')))
lines=['org.gradle.jvmargs=-Xmx1g','org.gradle.workers.max=2','systemProp.javax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts']
if u.hostname:
 for protocol in ['http','https']:
  lines.extend([f'systemProp.{protocol}.proxyHost={u.hostname}',f'systemProp.{protocol}.proxyPort={u.port or 80}'])
p=pathlib.Path(os.environ['GRADLE_USER_HOME'])/'gradle.properties';p.parent.mkdir(parents=True,exist_ok=True);p.write_text('\n'.join(lines)+'\n')
PY
./gradlew --no-daemon --max-workers=2 test :backend:sis-domain-service:bootJar :backend:sis-knowledge-service:bootJar
npm --prefix frontend ci --cache /workspace/tools/npm-cache
npm --prefix frontend test
npm --prefix frontend run build
bash scripts/configure.sh
