# stderr-app

An Android campsite reservation app for CMPUT 301, built by a team of six. Entrants join campsite lotteries; selected entrants can book available dates. Campsite owners manage listings and reservations, and administrators moderate the app.

## Team workspace

- [GitHub Wiki](https://github.com/CMPUT301F26stderr/stderr-app/wiki): onboarding, architecture, responsibilities, UI storyboards, decisions, and meeting notes.
- [Issues](https://github.com/CMPUT301F26stderr/stderr-app/issues): requirements, tasks, bugs, and acceptance criteria.
- [Projects](https://github.com/orgs/CMPUT301F26stderr/projects): the team's Kanban board.
- Canva: editable prototypes; keep the design links and exported diagrams in the wiki.
- [Course requirements](doc/00_requirements.md) and [preparation deliverables](doc/01_prep.md): reference documents.

## Getting started

1. Clone this repository and open its root in Android Studio.
2. Install the SDK required by `app/build.gradle.kts` (currently Android API 37). The Gradle daemon requires JDK 25, as recorded in `gradle/gradle-daemon-jvm.properties`.
3. Allow Gradle sync to finish, then run the `app` configuration on an emulator or device.
4. [Install the shared pre-commit hooks](CONTRIBUTING.md#install-local-hooks) after cloning. They run automatically on commits.
5. Before opening a PR, run the same Android checks used by CI:

   ```bash
   bash scripts/check.sh
   ```

   On Windows PowerShell:

   ```powershell
   .\gradlew.bat --no-daemon :app:lintDebug :app:testDebugUnitTest :app:assembleDebug
   ```

The checks run Android Lint, local unit tests, and a debug APK build. Device tests are separate and require an emulator or device.

## Repository layout

| Path | Purpose |
| --- | --- |
| `app/` | Android application, resources, and tests |
| `.github/` | Issue and PR templates, review ownership, and Actions workflows |
| `doc/` | Course reference documents and team roster |
| `firebase/` | Reserved for future Firebase configuration and backend code |
| `scripts/` | Shared development commands |
| `gradle/` and root build files | Reproducible build configuration |

Detailed team documentation belongs in the wiki. Keep executable configuration, agent instructions, and short contribution rules in this repository.

## Automation

CI checks pull requests and pushes to `main` with the shared pre-commit hooks and Android checks. File checks cover changed files; secret scanning covers the checkout. Manual CI runs audit all tracked files. The manual **Internal release** workflow runs the Android checks on `main` and uploads a debug APK as an Actions artifact for team testing. It does not publish a store release.

AI review and Firebase integration are pending. See [repository setup](.github/README.md) for the remaining GitHub configuration and wiki setup.

Read [CONTRIBUTING.md](CONTRIBUTING.md) before contributing. This project uses the [GNU GPL v3 license](LICENSE).
