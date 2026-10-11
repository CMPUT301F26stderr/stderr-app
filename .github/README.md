# Repository setup

These files define the repository scaffolding. GitHub settings, the project board, and wiki pages must be configured separately; adding these files does not create them.

## GitHub settings

1. Create or select the team project. Use Backlog, Ready, In progress, In review, and Done as statuses. Track story points, risk, and checkpoint with project fields or milestones. Link the chosen board from the repository README and wiki.
2. Protect `main` after the first successful CI run: require a pull request, one approving review, code-owner approval, resolved review conversations, and the `CI ready` status from GitHub Actions. Require branches to be up to date, dismiss stale approvals when reviewable changes are pushed, and apply the rules to administrators without bypass. Enable squash merging. `CI ready` succeeds only when all three verification jobs succeed.
3. Workflows and `CODEOWNERS` are owned solely by `@by-justin`, who must have repository write access. With required code-owner reviews and no bypass, PRs authored by `@by-justin` that change these paths cannot satisfy approval requirements: GitHub does not allow self-approval. Add another owner or explicitly revise the protection policy before merging those PRs. Assign primary owners and backup reviewers for the remaining infrastructure and feature areas, then activate those entries in `CODEOWNERS`. The remaining example entries are comments.
4. Enable the wiki and create the pages below. Store the Canva links there and embed exported diagrams as wiki images for course submissions.
5. Choose the AI-review provider and configure its integration. For an Actions integration, add a separate advisory job to `ci.yml` using the provider's documented setup. No AI-review job is active in this skeleton.
6. Register and start a Linux x64 self-hosted runner with access to this repository. All jobs require the `self-hosted`, `linux`, and `x64` labels, matching the flake's `x86_64-linux` target. Provision the runner separately; jobs will wait if no matching runner is online. The NixOS runner must provide Nix, Bash, Git, access to the Nix daemon, a writable home/cache, and network access for downloads. Workflows enable `nix-command` and `flakes` through `NIX_CONFIG`; they do not install or reconfigure Nix on the host. Keep detailed runner setup in the wiki.

## Wiki pages

| Page | Contents |
| --- | --- |
| Home | Links to the project board, issues, and other wiki pages |
| Getting started | Android Studio setup, SDK/JDK requirements, and first build |
| Team workflow | Ownership and backup reviewers, board conventions, and review process |
| Architecture | Feature boundaries, shared contracts, data model, and CRC cards |
| UI and storyboards | Canva links, embedded diagram exports, and user-story references |
| Testing and releases | Checks, device testing, and internal build installation |
| Decisions | Dated decisions, their reasons, and related issues or PRs |
| Meeting notes | Action items, sprint commitments, and TA feedback |

Keep the wiki authoritative for detailed guidance. Update executable configuration in the same PR as related code changes, and link relevant wiki updates from the PR.

## Workflows

- `ci.yml` runs `Repository checks`, `Pre-commit checks`, and `Android checks` on every pull request, pushes to `main`, and manual runs. File hooks check the PR/push diff; secrets are scanned across the checkout. Manual runs audit all tracked files. After the Android checks pass, CI automatically uploads an internal debug APK and `COMMIT.txt` identifying the build commit. The artifact is named with its commit, run ID, and attempt. A missing APK or failed upload fails the Android job and `CI ready`. Android reports are uploaded even if checks fail. Both builds and reports are retained for 14 days. Download them from the CI run's summary in GitHub Actions. These debug APKs use the runner's debug signing key; signing continuity and production distribution are future work.
- `Repository checks` validates Actions with actionlint, repository shell scripts with ShellCheck, the hook configuration, and flake evaluation. It uses the lightweight `repository` Nix shell; flake validation does not build the Android app or all Nix packages.
- Toolchains come from the flake and its locked `nixos-26.05` input: `repository` for configuration validation, `hooks` for pre-commit/Python/Go/Java, and `default` for Android builds. Stable nixpkgs' pre-commit is overridden to the shared 4.6.2 version. CI uses the Nix Android SDK and JDK rather than downloading distribution-specific toolchains with setup actions.
- A persistent runner reuses packages in its Nix store until garbage collection removes them. Gradle's setup action caches Gradle dependencies and build data. CI also caches pre-commit environments, keyed by runner and the flake/hook configuration because those environments contain absolute paths. Cache hits speed up setup; all checks still run.
- `CI ready` always evaluates the three job results and fails if any result is failed, cancelled, skipped, or missing. Require this status in repository settings; it is not enabled by adding a workflow file. Keep its name unique and do not add path filters that prevent CI from running on some PRs. When adding a new mandatory verification job, add it to both the gate dependencies and its success assertion.
- CI does not run device tests or deploy Firebase. AI review is pending provider selection.

The JDK and SDK selections match the current Gradle daemon and app configuration. Keep them synchronized when build requirements change. GitHub Actions must run once on the self-hosted runner to confirm the complete build; local static checks alone cannot establish that it works.
