#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
python3 scripts/extract_source.py
python3 scripts/reconcile_docs.py
python3 scripts/reconcile-aliases.py
python3 scripts/generate_diagrams.py
