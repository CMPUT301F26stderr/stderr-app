package io.github.CMPUT301F26stderr.campsbridge.data.notification

import io.github.CMPUT301F26stderr.campsbridge.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.Fields
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.read
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.stored
import io.github.CMPUT301F26stderr.campsbridge.domain.notification.InboxReceipt

object InboxReceiptDocument : DocumentCodec<InboxReceipt>() {
    override fun encode(value: InboxReceipt): Map<String, Any?> =
        mapOf("readAt" to value.readAt?.stored())

    override fun read(fields: Fields): InboxReceipt =
        InboxReceipt(fields.id, fields.optionalInstant("readAt"))
}
