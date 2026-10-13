# Contributing

Follow these contribution rules and the workflow and responsibilities recorded in the [team wiki](https://github.com/CMPUT301F26stderr/stderr-campsbridge/wiki).

## Install local hooks

Run `nix develop` to enter the development shell. It includes pre-commit, Python, Go, Java, Gitleaks, and ktlint 1.8.0. The formatter and hooks use the shared `.editorconfig`: four-space indentation, a 100-character column limit, and expression bodies on a separate line. Pre-commit manages its own hook environments, so the first installation still downloads their dependencies.

Without Nix, install the pinned runner with `uv tool install pre-commit==4.6.2` (or `pipx install pre-commit==4.6.2`). Make Java available on `PATH` for Kotlin checks; the project's JDK 25 works. For the format command, install [ktlint 1.8.0](https://github.com/ktlint/ktlint/releases/tag/1.8.0) on `PATH`; hooks download and verify the same release automatically.

After cloning, run:

```bash
pre-commit install --install-hooks
```

Hooks check staged files for whitespace, final newlines, valid YAML/JSON/TOML, conflict markers, oversized files (over 1 MiB), secrets, and Kotlin formatting. Markdown hard line breaks are preserved. Whitespace, newline, and Kotlin hooks may fix files; review and stage those changes before retrying the commit. Hooks never stage files automatically. Manual audits check Kotlin without modifying it. CI omits formatting, whitespace, and final-newline checks.

Format all tracked and untracked Kotlin sources and Gradle scripts, excluding ignored build outputs:

```bash
bash scripts/format-kotlin.sh
# Check the same files without changing them:
bash scripts/format-kotlin.sh --check
```

Use the ktlint integration in Android Studio with the pinned version and shared `.editorconfig`. Disable competing Kotlin formatters. Compose component names are allowed. Remove the comment-only file exceptions from `.editorconfig` when the corresponding integration files are implemented.

For a full checkout audit, including secrets in files that are not staged:

```bash
pre-commit run --hook-stage manual --all-files
```

CI checks YAML/JSON/TOML syntax, conflict markers, and file size in the PR/push diff, and scans the entire checkout for secrets. Manual workflow runs check all tracked files. Run the same checks with `nix develop .#hooks --command bash scripts/check-hooks.sh`; the Nix shell supplies the check implementations directly. Automatic PR formatting, commit-message validation, and pre-push checks are not enabled.

## Work on an issue

- Create or claim an issue before starting. Reference relevant user-story IDs and write observable acceptance criteria.
- Add the issue to the team project and keep its status current: Backlog → Ready → In progress → In review → Done.
- Assign one primary owner. Agree on shared-file changes and cross-feature interfaces with affected teammates.
- Use a short-lived branch such as `feat/42-lottery-waitlist`, `fix/57-booking-dates`, or `chore/18-ci`.

## Open a pull request

- Keep the change focused and explain the resulting behavior.
- Use a Conventional Commit PR title such as `feat(lottery): add waitlist joining`; working commits can remain flexible.
- Link its issue with `Closes #123` when merging will complete the issue.
- Run `bash scripts/check.sh`, or the equivalent Gradle command in the README. Report checks you could not run and why.
- For repository configuration changes, also run `nix develop .#repository --command bash scripts/check-repository.sh`. See the [maintenance guide](.github/SETUP.md) for workflow and runner behavior.
- Include screenshots for visible UI changes and update affected wiki guidance or diagrams.
- Request at least one teammate's review. Human review remains required when AI review is added.
- Resolve feedback and get a passing `CI ready` status before merging. It requires Repository checks, Pre-commit checks, and Android checks to succeed. Use squash merge for a concise main-branch history.

## Done means

Acceptance criteria are met, relevant verification is recorded, review is complete, the PR is merged, and affected documentation is current. Update the project status after merging.

Keep local SDK paths, signing keys, credentials, and generated build outputs out of commits. Use the checked-in Gradle wrapper for builds.
