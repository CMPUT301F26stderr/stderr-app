package io.github.CMPUT301F26stderr.app.data.messaging

import com.google.firebase.firestore.FirebaseFirestore
import io.github.CMPUT301F26stderr.app.data.booking.ReservationDocument
import io.github.CMPUT301F26stderr.app.data.campsite.CampsiteDocument
import io.github.CMPUT301F26stderr.app.data.firestore.DocumentKeys
import io.github.CMPUT301F26stderr.app.data.firestore.FirestorePaths
import io.github.CMPUT301F26stderr.app.data.firestore.activeAccount
import io.github.CMPUT301F26stderr.app.data.firestore.atomic
import io.github.CMPUT301F26stderr.app.data.firestore.observe
import io.github.CMPUT301F26stderr.app.data.firestore.read
import io.github.CMPUT301F26stderr.app.data.firestore.require
import io.github.CMPUT301F26stderr.app.data.firestore.stored
import io.github.CMPUT301F26stderr.app.data.notification.NotificationWrites
import io.github.CMPUT301F26stderr.app.domain.messaging.Message
import io.github.CMPUT301F26stderr.app.domain.messaging.MessageRepository
import io.github.CMPUT301F26stderr.app.domain.notification.Notification
import io.github.CMPUT301F26stderr.app.domain.notification.NotificationSubject
import io.github.CMPUT301F26stderr.app.domain.notification.NotificationType
import io.github.CMPUT301F26stderr.app.domain.notification.SubjectType
import io.github.CMPUT301F26stderr.app.domain.shared.PersistenceFailure.Reason.NOT_ALLOWED
import io.github.CMPUT301F26stderr.app.domain.shared.requireOperation
import kotlinx.coroutines.flow.map
import java.time.Clock

class FirestoreMessageRepository(
    private val db: FirebaseFirestore,
    private val clock: Clock,
) : MessageRepository {
    override fun observeConversation(reservationId: String) =
        db
            .collection("messages")
            .whereEqualTo("reservationId", reservationId)
            .observe(MessageDocument)
            .map { it.sortedBy { message -> message.createdAt } }

    override fun observeAll() =
        db.collection("messages").observe(MessageDocument).map {
            it.sortedByDescending { message -> message.createdAt }
        }

    override suspend fun send(
        reservationId: String,
        senderId: String,
        body: String,
    ): String {
        require(body.isNotBlank())
        val reference = db.collection("messages").document()
        db.atomic { tx ->
            tx.activeAccount(db, senderId)
            val reservation =
                tx.require(
                    db.document(FirestorePaths.reservation(reservationId)),
                    ReservationDocument,
                )
            val campsite =
                tx.require(
                    db.document(FirestorePaths.campsite(reservation.campsiteId)),
                    CampsiteDocument,
                )
            val participants = setOf(reservation.entrantId, campsite.ownerId)
            requireOperation(
                participants.size == 2 && senderId in participants,
                NOT_ALLOWED,
                "Conversation requires two distinct participants",
            )
            val recipient = participants.single { it != senderId }
            tx.activeAccount(db, recipient)
            val message = Message(reference.id, reservationId, senderId, body, clock.instant())
            tx.set(reference, MessageDocument.encode(message))
            val eventId = DocumentKeys.compound(NotificationType.NEW_MESSAGE.name, message.id)
            NotificationWrites.enqueue(
                db,
                tx,
                Notification(
                    eventId,
                    senderId,
                    NotificationType.NEW_MESSAGE,
                    NotificationSubject(SubjectType.MESSAGE, message.id),
                    null,
                    message.createdAt,
                ),
                listOf(recipient),
            )
        }
        return reference.id
    }

    override suspend fun markRead(
        messageId: String,
        readerId: String,
    ) {
        db.atomic { tx ->
            tx.activeAccount(db, readerId)
            val ref = db.document(FirestorePaths.message(messageId))
            val message = tx.require(ref, MessageDocument)
            val reservation =
                tx.require(
                    db.document(FirestorePaths.reservation(message.reservationId)),
                    ReservationDocument,
                )
            val site =
                tx.require(
                    db.document(FirestorePaths.campsite(reservation.campsiteId)),
                    CampsiteDocument,
                )
            requireOperation(
                readerId != message.senderId &&
                    readerId in setOf(reservation.entrantId, site.ownerId),
                NOT_ALLOWED,
                "Only the recipient can mark this message read",
            )
            if (message.readAt == null) tx.update(ref, "readAt", clock.instant().stored())
        }
    }
}
