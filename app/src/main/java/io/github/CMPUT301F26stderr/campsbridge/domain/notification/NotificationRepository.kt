package io.github.CMPUT301F26stderr.campsbridge.domain.notification

import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun get(id: String): Notification?

    fun observeInbox(accountId: String): Flow<List<InboxReceipt>>

    fun observeSent(senderId: String): Flow<List<Notification>>

    fun observeAll(): Flow<List<Notification>>

    /** Captures all entrants who joined and have not withdrawn, including after a draw. */
    suspend fun announce(
        lotteryId: String,
        ownerId: String,
        announcement: Announcement,
    ): String

    suspend fun markRead(
        accountId: String,
        notificationId: String,
    )

    /** Resumes durable delivery work. Call at startup and after mutations. */
    suspend fun resumeDeliveries()
}
