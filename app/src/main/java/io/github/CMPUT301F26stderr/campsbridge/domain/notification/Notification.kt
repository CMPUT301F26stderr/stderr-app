package io.github.CMPUT301F26stderr.campsbridge.domain.notification

import java.time.Instant

enum class NotificationType {
    LOTTERY_WON,
    LOTTERY_LOST,
    RESERVATION_CONFIRMED,
    RESERVATION_CANCELLED,
    OWNER_ANNOUNCEMENT,
    NEW_MESSAGE,
}

enum class SubjectType {
    LOTTERY,
    RESERVATION,
    MESSAGE,
}

data class NotificationSubject(
    val type: SubjectType,
    val id: String,
)

data class Announcement(
    val title: String,
    val body: String,
) {
    init {
        require(title.isNotBlank())
        require(body.isNotBlank())
    }
}

data class Notification(
    val id: String,
    val senderId: String?,
    val type: NotificationType,
    val subject: NotificationSubject,
    val announcement: Announcement?,
    val createdAt: Instant,
) {
    init {
        require((type == NotificationType.OWNER_ANNOUNCEMENT) == (announcement != null))
        val expected =
            when (type) {
                NotificationType.LOTTERY_WON,
                NotificationType.LOTTERY_LOST,
                NotificationType.OWNER_ANNOUNCEMENT,
                -> SubjectType.LOTTERY

                NotificationType.RESERVATION_CONFIRMED,
                NotificationType.RESERVATION_CANCELLED,
                -> SubjectType.RESERVATION

                NotificationType.NEW_MESSAGE -> SubjectType.MESSAGE
            }
        require(subject.type == expected)
    }
}

data class InboxReceipt(
    val notificationId: String,
    val readAt: Instant?,
)
