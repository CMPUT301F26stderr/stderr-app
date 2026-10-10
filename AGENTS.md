# Repository instructions

## Scope and documentation

- Respect the requested scope. Do not change application code to resolve repository-tooling work without authorization for that change.
- Detailed team documentation belongs in the GitHub Wiki. Keep repository instructions brief and executable configuration in Git.
- Preserve course reference documents under `doc/` unless the task explicitly calls for updating them.
- Do not invent team assignments, credentials, or enabled integrations. Label scaffolding and pending setup explicitly.
- Use `bash scripts/check.sh` for Android Lint, local unit tests, and the debug build. Report unavailable checks honestly.
- Use the pinned hooks in `.pre-commit-config.yaml` for file checks and ktfmt Kotlin style. Whitespace hooks can edit files: run them only within the authorized scope. A full audit uses `pre-commit run --hook-stage manual --all-files` and scans the checkout for secrets.

## Shared workspace

Avoid `git restore` when rolling back changes in a shared workspace; it may overwrite another contributor's progress. Revert only edits you own, and preserve unrelated changes.

## Subagents

Delegate bounded, simple work when independent tasks can proceed concurrently, noisy tests or logs benefit from isolation, or an independent review materially improves confidence. Do not delegate trivial edits, broad exploration of an unfamiliar codebase, large implementations, or merging commits.

- Use `explorer` for a specific read-only search or execution-path question.
- Use `reviewer` after meaningful changes, with a specific review goal.
- Use `tester` for tests, reproduction, and failure diagnosis.
- Use `executor` for simple commands with potentially substantial output.

Give each agent a bounded task and explicit deliverable. Assign file ownership for write work, avoid overlapping edits, and tell agents they share the workspace and must preserve others' changes. Run independent read-only work concurrently when appropriate. Request concise summaries and wait for required agents before concluding. The main agent owns integration, final validation, and communication, and reviews agent findings as evidence.
