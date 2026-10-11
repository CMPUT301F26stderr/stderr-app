package io.github.CMPUT301F26stderr.app.data.campsite

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import io.github.CMPUT301F26stderr.app.data.firestore.FirestorePaths
import io.github.CMPUT301F26stderr.app.data.firestore.activeAccount
import io.github.CMPUT301F26stderr.app.data.firestore.atomic
import io.github.CMPUT301F26stderr.app.data.firestore.observe
import io.github.CMPUT301F26stderr.app.data.firestore.read
import io.github.CMPUT301F26stderr.app.data.firestore.removal
import io.github.CMPUT301F26stderr.app.data.firestore.require
import io.github.CMPUT301F26stderr.app.domain.account.Role
import io.github.CMPUT301F26stderr.app.domain.campsite.CalendarNight
import io.github.CMPUT301F26stderr.app.domain.campsite.Campsite
import io.github.CMPUT301F26stderr.app.domain.campsite.CampsiteDetails
import io.github.CMPUT301F26stderr.app.domain.campsite.CampsiteRepository
import io.github.CMPUT301F26stderr.app.domain.shared.PersistenceFailure.Reason.CONFLICT
import io.github.CMPUT301F26stderr.app.domain.shared.PersistenceFailure.Reason.INACTIVE
import io.github.CMPUT301F26stderr.app.domain.shared.PersistenceFailure.Reason.NOT_ALLOWED
import io.github.CMPUT301F26stderr.app.domain.shared.requireOperation
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.LocalDate

class FirestoreCampsiteRepository(
    private val db: FirebaseFirestore,
    private val clock: Clock,
) : CampsiteRepository {
    override suspend fun get(id: String) =
        db.document(FirestorePaths.campsite(id)).read(CampsiteDocument)

    override fun observe(id: String) =
        db.document(FirestorePaths.campsite(id)).observe(CampsiteDocument)

    override fun observePublished() =
        db.collection("campsites").observe(CampsiteDocument).map { sites ->
            sites.filter { it.isPublished }
        }

    override fun observeForOwner(ownerId: String) =
        db.collection("campsites").whereEqualTo("ownerId", ownerId).observe(CampsiteDocument)

    override fun observeAll() =
        db.collection("campsites").observe(CampsiteDocument)

    override suspend fun create(
        ownerId: String,
        details: CampsiteDetails,
    ): String {
        val reference = db.collection("campsites").document()
        db.atomic { tx ->
            tx.activeAccount(db, ownerId, Role.OWNER)
            tx.set(
                reference,
                CampsiteDocument.encode(Campsite(reference.id, ownerId, details, clock.instant())),
            )
        }
        return reference.id
    }

    private suspend fun change(
        id: String,
        ownerId: String,
        transform: (Campsite) -> Campsite,
    ) {
        val ref = db.document(FirestorePaths.campsite(id))
        db.atomic { tx ->
            tx.activeAccount(db, ownerId, Role.OWNER)
            val site = tx.require(ref, CampsiteDocument)
            requireOperation(site.ownerId == ownerId, NOT_ALLOWED, "Different campsite owner")
            requireOperation(site.removal == null, INACTIVE, "Campsite removed")
            tx.set(ref, CampsiteDocument.encode(transform(site)))
        }
    }

    override suspend fun update(
        id: String,
        ownerId: String,
        details: CampsiteDetails,
    ) =
        change(id, ownerId) { it.copy(details = details) }

    override suspend fun publish(
        id: String,
        ownerId: String,
    ) =
        change(id, ownerId) { it.copy(publishedAt = it.publishedAt ?: clock.instant()) }

    override fun observeCalendar(
        id: String,
        start: LocalDate,
        endExclusive: LocalDate,
    ) =
        db
            .collection("${FirestorePaths.campsite(id)}/calendar")
            .whereGreaterThanOrEqualTo(FieldPath.documentId(), start.toString())
            .whereLessThan(FieldPath.documentId(), endExclusive.toString())
            .observe(CalendarNightDocument)

    override suspend fun setOffered(
        id: String,
        ownerId: String,
        dates: Set<LocalDate>,
        offered: Boolean,
    ) {
        // Each night is independent; bounded transactions allow arbitrarily long calendar edits.
        for (date in dates.sorted()) {
            db.atomic { tx ->
                tx.activeAccount(db, ownerId, Role.OWNER)
                val site = tx.require(db.document(FirestorePaths.campsite(id)), CampsiteDocument)
                requireOperation(
                    site.ownerId == ownerId && site.removal == null,
                    NOT_ALLOWED,
                    "Campsite is not editable",
                )
                val ref = db.document(FirestorePaths.night(id, date))
                val night = tx.read(ref, CalendarNightDocument) ?: CalendarNight(date, false)
                requireOperation(
                    offered || night.reservationId == null,
                    CONFLICT,
                    "Cancel the reservation before withdrawing a night",
                )
                tx.set(ref, CalendarNightDocument.encode(night.copy(isOffered = offered)))
            }
        }
    }
}
