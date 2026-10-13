# Persistence permissions

Cloud policy for [SCHEMA.md](SCHEMA.md). Firebase Auth UID is the account ID. Roles are additive and come from `accounts/{uid}.roles`; a removed account cannot start ordinary operations. Client-supplied actor IDs are never proof of identity. Console/tooling assigns `ADMIN`; app clients may only add `OWNER` to their own existing roles.

| Operation | Entrant | Owner | Admin |
| --- | --- | --- | --- |
| Read published listings/lotteries and availability | Yes | Yes | Yes |
| Read drafts | No | Own | All |
| Read profiles/reviews | Yes | Yes | Yes |
| Edit profile/settings/preferences; register devices | Own | Own | Own |
| Add owner role | Own account | Already enabled | Own account |
| Grant admin role | No | No | Console/tooling only |
| Remove own account | Yes | Yes | Yes |
| Create/manage campsite, calendar, lottery, offers | Requires OWNER | Own | Requires OWNER and ownership |
| Join/leave a lottery; decline an offer | Own entry | Requires ENTRANT | Requires ENTRANT |
| Read entries | Own | Own lottery | All |
| Draw after registration closes | No | Own lottery | Maintenance |
| Book | Own unused winning opportunity | Requires ENTRANT | Requires ENTRANT |
| Read reservations/payments | Own | Own campsite | All |
| Cancel/reject | Own reservation | Own campsite | Any for moderation |
| Upload/replace image | Own profile | Own profile/campsite | Own profile |
| Remove image/listing | Own profile image | Own profile/campsite | Any |
| Rate/review | Own review | Requires ENTRANT | Requires ENTRANT |
| Reply/react | Own reply/reaction | Own reply/reaction | Own reply/reaction |
| Read notifications | Own inbox | Own inbox/sent events | All logs |
| Announce to lottery entrants | No | Own lottery | Requires OWNER and ownership |
| Read messages | Reservation participant | Reservation participant | All logs |
| Send/read-receipt messages | Participant | Participant | Participant |
| Remove another account/owner | No | No | Yes |

Profile reads expose the complete account document, including contact/settings fields. Device registrations and inboxes have separate rules. Published removed listing records remain readable for history; discovery excludes them. No unauthenticated reads are allowed.

## Coordinated writes

| Data | Cloud-authorized writer |
| --- | --- |
| Waiting count | Entrant's matching entry transition; draw operator; admin removal cleanup |
| Draw seed/outcomes/completion | Lottery owner or admin, after the deadline; seed is immutable |
| Calendar occupancy | Winning entrant's matching reservation transaction; matching cancellation |
| Payments | Matching booking/cancellation transaction; amount is immutable after creation |
| System notification/delivery job | Matching draw, reservation, or message operation |
| Inbox creation | Valid delivery job, processed by its participants/owner or admin |
| Inbox read time | Recipient only; write once |
| Upload intent/attachment | Target owner; removal/obsolete cleanup is also available to admin |
| Removal jobs/cascades | Original self-removal actor or admin; immutable removal facts |
| Migration records | Console/Admin SDK tooling only |

The current global `AppContainer.resumePendingWork()` scans private collections, so run it only under an admin session. Owner-scoped draws and participant-scoped delivery remain available. A future non-admin foreground scheduler must filter work before calling these operations; client code has no special system privilege. Query rules are not filters: private collection queries must constrain account/owner/participant fields, or run as admin.

Rules enforce access, immutable identities, allowed transitions, and selected transaction relationships. Repositories still own the lottery's random ranking and complete multi-night orchestration: Rules cannot run the Kotlin ranking algorithm or iterate an arbitrary stay. Do not mistake access rules for a replacement booking/draw implementation. Emulator permission tests cover direct denied writes and representative coordinated operations.

Storage objects carry `attachmentId` metadata. Creation requires a matching, unexpired Firestore `_imageUploads/{blobId}` intent authorized for that attachment. Objects cannot be overwritten; replacement uses a new blob. Rules can therefore resolve ownership without duplicating roles or putting an actor in the image document. Obsolete cleanup jobs also retain their attachment reference for authorization. Profile images are shared; draft campsite images are owner/admin-only.

## Test and deploy

```sh
cd firebase/tooling
npm ci
cd ../..
nix develop --command firebase emulators:exec --config firebase/firebase.json --project demo-stderr --only firestore,storage 'node --test firebase/tooling/tests/permissions.test.mjs'

nix develop --command firebase deploy --config firebase/firebase.json --project project-3bc1a00e-fb80-48ba-948 --only firestore:rules,storage
```

CLI deployment requires a logged-in project member. Anonymous app accounts never deploy rules. Local files take effect in the cloud only after deployment. Rule tests do not deploy or use the real project.
