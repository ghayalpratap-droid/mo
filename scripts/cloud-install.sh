#!/usr/bin/env bash
set -euo pipefail
cd /workspace/mo
mkdir -p /workspace/tools
if [ ! -x /workspace/tools/apache-maven-3.9.11/bin/mvn ]; then
  curl -fsSL https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.11/apache-maven-3.9.11-bin.tar.gz -o /tmp/employee-maven.tar.gz
  curl -fsSL https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.11/apache-maven-3.9.11-bin.tar.gz.sha512 -o /tmp/employee-maven.sha512
  python3 - <<'PYCODE'
import hashlib
assert hashlib.sha512(open('/tmp/employee-maven.tar.gz','rb').read()).hexdigest() == open('/tmp/employee-maven.sha512').read().strip()
PYCODE
  tar xzf /tmp/employee-maven.tar.gz -C /workspace/tools
fi
if [ ! -x /workspace/tools/jdk-17/bin/javac ]; then
  docker pull maven:3.9.11-eclipse-temurin-17
  toolchain_id=$(docker create maven:3.9.11-eclipse-temurin-17)
  docker cp "$toolchain_id":/opt/java/openjdk /workspace/tools/jdk-17
  docker rm "$toolchain_id" > /dev/null
fi
python3 - <<'PYCODE'
import os, urllib.parse, pathlib
u=urllib.parse.urlsplit(os.environ.get('HTTPS_PROXY',os.environ.get('https_proxy','')))
proxies=f'<proxies><proxy><id>cloud</id><active>true</active><protocol>http</protocol><host>{u.hostname}</host><port>{u.port or 80}</port></proxy></proxies>' if u.hostname else ''
pathlib.Path('/workspace/tools/maven-settings.xml').write_text('<settings>'+proxies+'</settings>')
PYCODE
export JAVA_HOME=/workspace/tools/jdk-17
export MAVEN_OPTS=-Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts
/workspace/tools/apache-maven-3.9.11/bin/mvn -s /workspace/tools/maven-settings.xml -B -Dmaven.repo.local=/workspace/tools/m2 -f backend/pom.xml verify
cd frontend
npm ci --cache /workspace/tools/npm-cache
npm test
npm run build
