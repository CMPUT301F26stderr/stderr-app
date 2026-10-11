package io.github.CMPUT301F26stderr.app.domain.messaging

import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun observeConversation(reservationId: String): Flow<List<Message>>

    fun observeAll(): Flow<List<Message>>

    suspend fun send(
        reservationId: String,
        senderId: String,
        body: String,
    ): String

    suspend fun markRead(
        messageId: String,
        readerId: String,
    )
}
