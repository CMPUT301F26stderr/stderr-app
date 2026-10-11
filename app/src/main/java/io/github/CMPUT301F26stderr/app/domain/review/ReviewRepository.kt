package io.github.CMPUT301F26stderr.app.domain.review

import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun observeReviews(campsiteId: String): Flow<List<Review>>

    fun observeReplies(
        campsiteId: String,
        authorId: String,
    ): Flow<List<Reply>>

    fun observeReactions(
        campsiteId: String,
        authorId: String,
    ): Flow<List<Reaction>>

    suspend fun rate(
        campsiteId: String,
        authorId: String,
        rating: Long,
        body: String?,
    )

    suspend fun reply(
        campsiteId: String,
        authorId: String,
        senderId: String,
        parentReplyId: String?,
        body: String,
    ): String

    suspend fun react(
        campsiteId: String,
        authorId: String,
        reactorId: String,
        reaction: ReactionType?,
    )
}
