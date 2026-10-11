#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

if [[ -n "${BASE_REF:-}" && "$BASE_REF" != 0000000000000000000000000000000000000000 ]]; then
    : "${HEAD_REF:?HEAD_REF is required when BASE_REF is set}"
    # A Kotlin formatting policy change must be checked against all tracked Kotlin files.
    if ! git diff --quiet "$BASE_REF" "$HEAD_REF" -- .editorconfig; then
        pre-commit run ktlint-check --hook-stage manual --all-files --show-diff-on-failure
    fi
    exec pre-commit run --hook-stage manual --from-ref "$BASE_REF" --to-ref "$HEAD_REF" --show-diff-on-failure
else
    exec pre-commit run --hook-stage manual --all-files --show-diff-on-failure
fi
