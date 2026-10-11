package io.github.CMPUT301F26stderr.app.data.notification

import io.github.CMPUT301F26stderr.app.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.app.data.firestore.Fields
import io.github.CMPUT301F26stderr.app.data.firestore.read
import io.github.CMPUT301F26stderr.app.data.firestore.stored
import io.github.CMPUT301F26stderr.app.domain.notification.InboxReceipt

object InboxReceiptDocument : DocumentCodec<InboxReceipt>() {
    override fun encode(value: InboxReceipt): Map<String, Any?> =
        mapOf("readAt" to value.readAt?.stored())

    override fun read(fields: Fields): InboxReceipt =
        InboxReceipt(fields.id, fields.optionalInstant("readAt"))
}
