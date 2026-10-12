package io.github.CMPUT301F26stderr.app.data.review

import com.google.firebase.firestore.FirebaseFirestore
import io.github.CMPUT301F26stderr.app.data.campsite.CampsiteDocument
import io.github.CMPUT301F26stderr.app.data.firestore.FirestorePaths
import io.github.CMPUT301F26stderr.app.data.firestore.activeAccount
import io.github.CMPUT301F26stderr.app.data.firestore.atomic
import io.github.CMPUT301F26stderr.app.data.firestore.observe
import io.github.CMPUT301F26stderr.app.data.firestore.read
import io.github.CMPUT301F26stderr.app.data.firestore.require
import io.github.CMPUT301F26stderr.app.domain.account.Role
import io.github.CMPUT301F26stderr.app.domain.review.Reaction
import io.github.CMPUT301F26stderr.app.domain.review.ReactionType
import io.github.CMPUT301F26stderr.app.domain.review.Reply
import io.github.CMPUT301F26stderr.app.domain.review.Review
import io.github.CMPUT301F26stderr.app.domain.review.ReviewRepository
import io.github.CMPUT301F26stderr.app.domain.shared.PersistenceFailure.Reason.INACTIVE
import io.github.CMPUT301F26stderr.app.domain.shared.requireOperation
import kotlinx.coroutines.flow.map
import java.time.Clock

class FirestoreReviewRepository(
    private val db: FirebaseFirestore,
    private val clock: Clock,
) : ReviewRepository {
    override fun observeReviews(campsiteId: String) =
        db.collection("${FirestorePaths.campsite(campsiteId)}/reviews").observe(ReviewDocument)

    override fun observeReplies(
        campsiteId: String,
        authorId: String,
    ) =
        db
            .collection("${FirestorePaths.review(campsiteId, authorId)}/replies")
            .observe(ReplyDocument)
            .map { it.sortedBy { reply -> reply.createdAt } }

    override fun observeReactions(
        campsiteId: String,
        authorId: String,
    ) =
        db
            .collection("${FirestorePaths.review(campsiteId, authorId)}/reactions")
            .observe(ReactionDocument)

    override suspend fun rate(
        campsiteId: String,
        authorId: String,
        rating: Long,
        body: String?,
    ) {
        require(rating in 1..5)
        db.atomic { tx ->
            tx.activeAccount(db, authorId, Role.ENTRANT)
            val campsite =
                tx.require(db.document(FirestorePaths.campsite(campsiteId)), CampsiteDocument)
            requireOperation(campsite.isPublished, INACTIVE, "Campsite unavailable")
            val ref = db.document(FirestorePaths.review(campsiteId, authorId))
            val old = tx.read(ref, ReviewDocument)
            val now = clock.instant()
            tx.set(
                ref,
                ReviewDocument.encode(
                    Review(
                        campsiteId,
                        authorId,
                        rating,
                        body?.takeIf { it.isNotBlank() },
                        old?.createdAt ?: now,
                        now,
                    ),
                ),
            )
        }
    }

    override suspend fun reply(
        campsiteId: String,
        authorId: String,
        senderId: String,
        parentReplyId: String?,
        body: String,
    ): String {
        require(body.isNotBlank())
        val ref = db.collection("${FirestorePaths.review(campsiteId, authorId)}/replies").document()
        db.atomic { tx ->
            tx.activeAccount(db, senderId)
            tx.require(db.document(FirestorePaths.review(campsiteId, authorId)), ReviewDocument)
            if (parentReplyId != null) {
                tx.require(
                    db.document(FirestorePaths.reply(campsiteId, authorId, parentReplyId)),
                    ReplyDocument,
                )
            }
            tx.set(
                ref,
                ReplyDocument.encode(Reply(ref.id, senderId, parentReplyId, body, clock.instant())),
            )
        }
        return ref.id
    }

    override suspend fun react(
        campsiteId: String,
        authorId: String,
        reactorId: String,
        reaction: ReactionType?,
    ) {
        db.atomic { tx ->
            tx.activeAccount(db, reactorId)
            tx.require(db.document(FirestorePaths.review(campsiteId, authorId)), ReviewDocument)
            val ref = db.document(FirestorePaths.reaction(campsiteId, authorId, reactorId))
            if (reaction == null) {
                tx.delete(ref)
            } else {
                tx.set(ref, ReactionDocument.encode(Reaction(reactorId, reaction)))
            }
        }
    }
}
