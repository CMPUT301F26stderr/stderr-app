package io.github.CMPUT301F26stderr.app.domain.review

import java.time.Instant

data class Review(
    val campsiteId: String,
    val authorId: String,
    val rating: Long,
    val body: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        require(rating in 1..5)
    }
}

data class Reply(
    val id: String,
    val authorId: String,
    val parentReplyId: String?,
    val body: String,
    val createdAt: Instant,
) {
    init {
        require(body.isNotBlank())
    }
}

enum class ReactionType {
    LIKE,
    HELPFUL,
}

data class Reaction(
    val reactorId: String,
    val type: ReactionType,
)
