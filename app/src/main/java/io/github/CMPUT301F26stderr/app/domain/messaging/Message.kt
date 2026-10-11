package io.github.CMPUT301F26stderr.app.domain.messaging

import java.time.Instant

data class Message(
    val id: String,
    val reservationId: String,
    val senderId: String,
    val body: String,
    val createdAt: Instant,
    val readAt: Instant? = null,
) {
    init {
        require(body.isNotBlank())
    }
}
