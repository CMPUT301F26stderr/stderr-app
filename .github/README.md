# Repository setup

These files define the repository scaffolding. GitHub settings, the project board, and wiki pages must be configured separately; adding these files does not create them.

## GitHub settings

1. Create or select the team project. Use Backlog, Ready, In progress, In review, and Done as statuses. Track story points, risk, and checkpoint with project fields or milestones. Link the chosen board from the repository README and wiki.
2. Protect `main`: require a pull request, one approval, resolved review conversations, and the `Android checks` and `Pre-commit checks` statuses after their first run. Enable squash merging.
3. Assign a primary owner and backup reviewer for shared infrastructure and each feature area. Add their GitHub handles to `CODEOWNERS` once agreed. Its initial entries are comments and do not request reviews.
4. Enable the wiki and create the pages below. Store the Canva links there and embed exported diagrams as wiki images for course submissions.
5. Choose the AI-review provider and configure its integration. For an Actions integration, add a separate advisory job to `ci.yml` using the provider's documented setup. No AI-review job is active in this skeleton.

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

- `ci.yml` runs shared pre-commit checks, Android Lint, local unit tests, and a debug build on pull requests, pushes to `main`, and manual runs. File hooks check the PR/push diff; secrets are scanned across the checkout. Manual runs audit all tracked files. Android reports are uploaded even if checks fail.
- `release.yml` runs manually on `main`, runs the same checks, and uploads a debug APK for 14 days. This is an internal test artifact, using the runner's debug signing key; signing continuity and production distribution are future work.
- Neither workflow runs device tests or deploys Firebase. AI review is pending provider selection.

The JDK and SDK selections match the current Gradle daemon and app configuration. Keep them synchronized when build requirements change. GitHub Actions must run once to confirm the complete hosted build; local static checks alone cannot establish that it works.
