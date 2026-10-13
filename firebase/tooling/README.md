# Persistence tooling

Seed and migration commands are not implemented. Use the following conventions when adding them:

- Keep reusable development fixtures in `fixtures/` when seeding is implemented.
- Default tooling to the Firebase emulators; require an explicit project for cloud writes.
- Migrations must be safe to rerun and write `_migrations/{migrationId}.completedAt` only on success.
- Use reseeding for disposable development data. Do not run migrations from Android startup.
