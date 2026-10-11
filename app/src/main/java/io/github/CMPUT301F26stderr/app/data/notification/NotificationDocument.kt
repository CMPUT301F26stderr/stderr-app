package io.github.CMPUT301F26stderr.app.data.notification

import io.github.CMPUT301F26stderr.app.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.app.data.firestore.Fields
import io.github.CMPUT301F26stderr.app.data.firestore.read
import io.github.CMPUT301F26stderr.app.data.firestore.stored
import io.github.CMPUT301F26stderr.app.domain.notification.Announcement
import io.github.CMPUT301F26stderr.app.domain.notification.Notification
import io.github.CMPUT301F26stderr.app.domain.notification.NotificationSubject

object NotificationDocument : DocumentCodec<Notification>() {
    override fun encode(value: Notification): Map<String, Any?> =
        mapOf(
            "senderId" to value.senderId,
            "type" to value.type.name,
            "subject" to mapOf("type" to value.subject.type.name, "id" to value.subject.id),
            "announcement" to
                value.announcement?.let { mapOf("title" to it.title, "body" to it.body) },
            "createdAt" to value.createdAt.stored(),
        )

    override fun read(fields: Fields): Notification =
        Notification(
            fields.id,
            fields.optionalString("senderId"),
            fields.enum("type"),
            fields.nested("subject").let { NotificationSubject(it.enum("type"), it.string("id")) },
            fields.optionalNested("announcement")?.let {
                Announcement(it.string("title"), it.string("body"))
            },
            fields.instant("createdAt"),
        )
}
