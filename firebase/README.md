# Firebase integration

This directory holds the [persistence contract](SCHEMA.md), Firestore/Storage rules, query indexes, and emulator configuration. Kotlin models, codecs, and repository implementations live in the Android module. No cloud project is bound or deployed.

From the repository root, start local services with:

```sh
nix develop --command firebase emulators:start --config firebase/firebase.json --project demo-stderr
```

`demo-stderr` is an emulator-only project identifier. Create one `FirebaseClients` instance using an explicitly initialized `FirebaseApp`, then pass it to `AppContainer`. Android emulator clients use host `10.0.2.2`; other devices require a reachable host. Configure emulator connections before using the clients. The current rules allow signed-in cooperative course accounts; device-auth/bootstrap wiring remains pending.

The container exposes domain repository interfaces and `resumePendingWork()` for draws, removal cascades, inbox delivery, and obsolete image cleanup. Invoke that method from the future application/controller lifecycle after initialization and after mutations, with retry/error feedback. It does not send OS/FCM pushes. UI/controller/platform placeholder files describe the remaining integration work.

One focused JVM test file establishes codec/key conventions. Emulator concurrency and interruption tests, seed commands, and migration commands remain to be implemented. See [tooling](tooling/README.md).

Document cloud environment setup and deployment in the team Wiki. Provide actual Firebase options through bootstrap configuration; do not invent project IDs or credentials. No `google-services.json` is needed to compile this injectable persistence layer.
