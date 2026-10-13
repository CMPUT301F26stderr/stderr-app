package io.github.CMPUT301F26stderr.campsbridge.data.moderation

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import io.github.CMPUT301F26stderr.campsbridge.data.account.AccountDocument
import io.github.CMPUT301F26stderr.campsbridge.data.booking.FirestoreBookingRepository
import io.github.CMPUT301F26stderr.campsbridge.data.booking.ReservationDocument
import io.github.CMPUT301F26stderr.campsbridge.data.campsite.CampsiteDocument
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.DocumentKeys
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.Fields
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.FirestorePaths
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.activeAccount
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.atomic
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.cancellation
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.read
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.removal
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.require
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.stored
import io.github.CMPUT301F26stderr.campsbridge.data.lottery.FirestoreLotteryRepository
import io.github.CMPUT301F26stderr.campsbridge.data.lottery.LotteryDocument
import io.github.CMPUT301F26stderr.campsbridge.data.lottery.LotteryEntryDocument
import io.github.CMPUT301F26stderr.campsbridge.domain.account.AccountSettings
import io.github.CMPUT301F26stderr.campsbridge.domain.account.Preferences
import io.github.CMPUT301F26stderr.campsbridge.domain.account.Role
import io.github.CMPUT301F26stderr.campsbridge.domain.booking.CancellationRequest
import io.github.CMPUT301F26stderr.campsbridge.domain.campsite.Campsite
import io.github.CMPUT301F26stderr.campsbridge.domain.image.ImageRepository
import io.github.CMPUT301F26stderr.campsbridge.domain.image.ImageTarget
import io.github.CMPUT301F26stderr.campsbridge.domain.image.ImageTargetType
import io.github.CMPUT301F26stderr.campsbridge.domain.lottery.EntryState
import io.github.CMPUT301F26stderr.campsbridge.domain.moderation.ModerationRepository
import io.github.CMPUT301F26stderr.campsbridge.domain.shared.PersistenceFailure.Reason.INVALID_STATE
import io.github.CMPUT301F26stderr.campsbridge.domain.shared.PersistenceFailure.Reason.NOT_ALLOWED
import io.github.CMPUT301F26stderr.campsbridge.domain.shared.Removal
import io.github.CMPUT301F26stderr.campsbridge.domain.shared.requireOperation
import kotlinx.coroutines.tasks.await
import java.time.Clock
import java.time.LocalDate

/** Removal marks the target first; durable jobs resume the dependent cleanup. */
class FirestoreModerationRepository(
    private val db: FirebaseFirestore,
    private val images: ImageRepository,
    private val lotteries: FirestoreLotteryRepository,
    private val clock: Clock,
) : ModerationRepository {
    override suspend fun removeAccount(
        accountId: String,
        actorId: String,
        reason: String?,
    ) {
        db.atomic { tx ->
            val account =
                tx.require(db.document(FirestorePaths.account(accountId)), AccountDocument)
            if (accountId != actorId) tx.activeAccount(db, actorId, Role.ADMIN)
            val removal = account.removal ?: Removal(clock.instant(), actorId, reason)
            tx.set(
                db.document(FirestorePaths.account(accountId)),
                AccountDocument.encode(
                    account.copy(
                        name = "",
                        email = null,
                        phone = null,
                        roles = emptySet(),
                        preferences = Preferences(),
                        settings = AccountSettings(notificationsEnabled = false),
                        removal = removal,
                    ),
                ),
            )
            enqueue(tx, ImageTarget(ImageTargetType.ACCOUNT, accountId), removal)
        }
        resumeRemovals()
    }

    override suspend fun removeCampsite(
        campsiteId: String,
        actorId: String,
        reason: String?,
    ) {
        db.atomic { tx ->
            val actor = tx.activeAccount(db, actorId)
            val site =
                tx.require(db.document(FirestorePaths.campsite(campsiteId)), CampsiteDocument)
            requireOperation(
                actor.id == site.ownerId || Role.ADMIN in actor.roles,
                NOT_ALLOWED,
                "Not owner or admin",
            )
            markSite(tx, site, Removal(clock.instant(), actorId, reason))
        }
        resumeRemovals()
    }

    private fun enqueue(
        tx: com.google.firebase.firestore.Transaction,
        target: ImageTarget,
        removal: Removal,
    ) {
        tx.set(
            db.collection("_removals").document(DocumentKeys.attachment(target)),
            removal.stored(),
        )
    }

    private fun markSite(
        tx: com.google.firebase.firestore.Transaction,
        site: Campsite,
        requested: Removal,
    ) {
        val removal = site.removal ?: requested
        tx.update(db.document(FirestorePaths.campsite(site.id)), "removal", removal.stored())
        enqueue(tx, ImageTarget(ImageTargetType.CAMPSITE, site.id), removal)
    }

    override suspend fun resumeRemovals() {
        // Account removal may enqueue campsite jobs, so drain until there are no jobs.
        while (true) {
            val jobs =
                db
                    .collection("_removals")
                    .get(Source.SERVER)
                    .await()
                    .documents
            if (jobs.isEmpty()) return
            for (job in jobs) {
                val target = DocumentKeys.target(job.id)
                val removal = Fields(job.reference.path, job.data!!).removal()
                if (target.type == ImageTargetType.ACCOUNT) {
                    cleanupAccount(target.id, removal)
                } else {
                    cleanupSite(target.id, removal)
                }
                images.delete(target)
                job.reference.delete().await()
            }
        }
    }

    private suspend fun cleanupAccount(
        id: String,
        removal: Removal,
    ) {
        for (
        site in
        db.collection("campsites").whereEqualTo("ownerId", id).read(CampsiteDocument)
        ) {
            db.atomic { tx ->
                val current =
                    tx.require(db.document(FirestorePaths.campsite(site.id)), CampsiteDocument)
                markSite(tx, current, removal)
            }
        }
        for (
        lottery in
        db
            .collection("lotteries")
            .whereEqualTo("ownerId", id)
            .read(LotteryDocument)
        ) {
            cancelLottery(lottery.id, removal)
        }
        for (
        entry in
        db
            .collectionGroup("entries")
            .whereEqualTo("accountId", id)
            .read(LotteryEntryDocument)
        ) {
            db.atomic { tx ->
                val lotteryRef = db.document(FirestorePaths.lottery(entry.lotteryId))
                val lottery = tx.require(lotteryRef, LotteryDocument)
                val entryRef = db.document(FirestorePaths.entry(entry.lotteryId, id))
                val current = tx.require(entryRef, LotteryEntryDocument)
                // Do not change frozen draw membership; the removed account cannot book.
                if (
                    current.state == EntryState.WAITING &&
                    clock.instant() < lottery.configuration.registrationClosesAt
                ) {
                    requireOperation(
                        lottery.waitingCount > 0,
                        INVALID_STATE,
                        "Invalid waiting count",
                    )
                    tx.set(
                        entryRef,
                        LotteryEntryDocument.encode(
                            current.copy(
                                state = EntryState.WITHDRAWN,
                                stateChangedAt = clock.instant(),
                            ),
                        ),
                    )
                    tx.update(lotteryRef, "waitingCount", lottery.waitingCount - 1)
                }
            }
        }
        for (
        reservation in
        db
            .collection("reservations")
            .whereEqualTo("entrantId", id)
            .read(ReservationDocument)
        ) {
            cancelFuture(reservation.id, removal)
        }
        val devices =
            db
                .collection("${FirestorePaths.account(id)}/devices")
                .get(Source.SERVER)
                .await()
                .documents
        for (group in devices.chunked(200)) {
            val batch = db.batch()
            group.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }
    }

    private suspend fun cleanupSite(
        id: String,
        removal: Removal,
    ) {
        // Small project dataset: derive memberships from offers, without duplicating campsite IDs.
        for (
        offer in
        db.collectionGroup("offers").get(Source.SERVER).await().documents.filter {
            it.id == id
        }
        ) {
            val lotteryId =
                offer.reference.parent.parent!!
                    .id
            cancelLottery(lotteryId, removal)
        }
        for (
        reservation in
        db
            .collection("reservations")
            .whereEqualTo("campsiteId", id)
            .read(ReservationDocument)
        ) {
            cancelFuture(reservation.id, removal)
        }
    }

    private suspend fun cancelLottery(
        id: String,
        removal: Removal,
    ) {
        val lottery = db.document(FirestorePaths.lottery(id)).read(LotteryDocument) ?: return
        if (
            lottery.cancellation == null &&
            (
                lottery.publishedAt == null ||
                    lottery.configuration.bookingClosesAt > clock.instant()
            )
        ) {
            lotteries.cancelForRemoval(id, removal.actorId, removal.reason)
        }
    }

    private suspend fun cancelFuture(
        id: String,
        removal: Removal,
    ) {
        db.atomic { tx ->
            val reservation =
                tx.require(db.document(FirestorePaths.reservation(id)), ReservationDocument)
            val today =
                LocalDate.ofInstant(
                    clock.instant(),
                    java.time.ZoneId.of(reservation.bookedCampsite.timeZone),
                )
            if (reservation.checkOutDate > today && reservation.cancellation == null) {
                val site =
                    tx.require(
                        db.document(FirestorePaths.campsite(reservation.campsiteId)),
                        CampsiteDocument,
                    )
                val role =
                    when (removal.actorId) {
                        reservation.entrantId -> Role.ENTRANT
                        site.ownerId -> Role.OWNER
                        else -> Role.ADMIN
                    }
                FirestoreBookingRepository.cancelInTransaction(
                    db,
                    tx,
                    reservation,
                    CancellationRequest(id, removal.actorId, role, removal.reason),
                    clock.instant(),
                )
            }
        }
    }
}
