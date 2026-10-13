# Firebase integration

This directory holds the [persistence contract](SCHEMA.md), Firestore/Storage rules, query indexes, and emulator configuration. Kotlin models, codecs, and repository implementations live in the Android module.

## Local development

From the repository root, start local services with:

```sh
nix develop --command firebase emulators:start --config firebase/firebase.json --project demo-stderr
```

`demo-stderr` is an emulator-only project identifier. Create one `FirebaseClients` instance using an explicitly initialized `FirebaseApp`, then pass it to `AppContainer`. Android emulator clients use host `10.0.2.2`; other devices require a reachable host. Configure emulator connections before using the clients. Firestore and Storage rules require signed-in accounts.

## Application integration

The container exposes domain repository interfaces and `resumePendingWork()` for draws, removal cascades, inbox delivery, and obsolete image cleanup. Invoke that method from the application/controller lifecycle after initialization and after mutations, with retry/error feedback. Inbox delivery creates Firestore receipts; OS/FCM push delivery requires a separate platform integration.

Provide Firebase options through bootstrap configuration. This persistence layer compiles without `google-services.json`. Keep cloud environment setup and deployment instructions in the team Wiki.

## Testing and pending work

`PersistenceConventionTest` covers document codecs, document keys, date ranges, and model validation. Run it with the local unit tests through `bash scripts/check.sh` from the repository root. That command does not run Firestore Emulator integration tests.

Pending work:

- Firebase project configuration and device authentication/bootstrap.
- Application/controller lifecycle wiring and OS/FCM push delivery.
- Emulator workflow tests for concurrency and interrupted operations.
- Seed and migration commands; see [tooling conventions](tooling/README.md).
