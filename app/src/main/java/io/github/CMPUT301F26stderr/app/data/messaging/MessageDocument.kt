package io.github.CMPUT301F26stderr.app.data.messaging

import io.github.CMPUT301F26stderr.app.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.app.data.firestore.Fields
import io.github.CMPUT301F26stderr.app.data.firestore.read
import io.github.CMPUT301F26stderr.app.data.firestore.stored
import io.github.CMPUT301F26stderr.app.domain.messaging.Message

object MessageDocument : DocumentCodec<Message>() {
    override fun encode(value: Message): Map<String, Any?> =
        mapOf(
            "reservationId" to value.reservationId,
            "senderId" to value.senderId,
            "body" to value.body,
            "createdAt" to value.createdAt.stored(),
            "readAt" to value.readAt?.stored(),
        )

    override fun read(fields: Fields): Message =
        Message(
            fields.id,
            fields.string("reservationId"),
            fields.string("senderId"),
            fields.string("body"),
            fields.instant("createdAt"),
            fields.optionalInstant("readAt"),
        )
}
