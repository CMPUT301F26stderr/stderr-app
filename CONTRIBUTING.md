# Contributing

Detailed workflow and responsibilities live in the [team wiki](https://github.com/CMPUT301F26stderr/stderr-app/wiki). These are the repository's baseline rules.

## Install local hooks

Run `nix develop` to enter the development shell. It includes pre-commit, Python, Go, Java, Gitleaks, and ktfmt. The `ktfmt` command uses the same pinned version and four-space Kotlin style as the hooks. Pre-commit manages its own hook environments, so the first installation still downloads their dependencies.

Without Nix, install the pinned runner with `uv tool install pre-commit==4.6.2` (or `pipx install pre-commit==4.6.2`). Make Java available on `PATH` for Kotlin checks; the project's JDK 25 works.

After cloning, run:

```bash
pre-commit install --install-hooks
```

Hooks check staged files for whitespace, final newlines, valid YAML/JSON/TOML, conflict markers, oversized files (over 1 MiB), secrets, and Kotlin formatting. Markdown hard line breaks are preserved. Whitespace and newline hooks may fix files; review and stage those changes before retrying the commit. Kotlin checks use pinned ktfmt with four-space Kotlin style in check mode; format with the matching IDE formatter and stage the result.

For a full checkout audit, including secrets in files that are not staged:

```bash
pre-commit run --hook-stage manual --all-files
```

CI applies file checks to the PR/push diff and scans the entire checkout for secrets. Manual workflow runs check all tracked files. This allows untouched existing code to adopt formatting as it is changed; a full audit can report older formatting issues. Commit-message validation and pre-push checks are not enabled.

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
- Include screenshots for visible UI changes and update affected wiki guidance or diagrams.
- Request at least one teammate's review. Human review remains required when AI review is added.
- Resolve feedback and get passing required checks before merging. Use squash merge for a concise main-branch history.

## Done means

Acceptance criteria are met, relevant verification is recorded, review is complete, the PR is merged, and affected documentation is current. Update the project status after merging.

Keep local SDK paths, signing keys, credentials, and generated build outputs out of commits. Use the checked-in Gradle wrapper for builds.
