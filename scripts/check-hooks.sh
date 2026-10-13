#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

# CI checks syntax, conflicts, file size, and secrets without modifying files.
# Formatting remains in local hooks and explicit manual audits.
exec python3 scripts/check-hooks.py
