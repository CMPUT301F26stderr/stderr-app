package io.github.CMPUT301F26stderr.campsbridge.data.booking

import io.github.CMPUT301F26stderr.campsbridge.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.Fields
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.read
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.stored
import io.github.CMPUT301F26stderr.campsbridge.domain.booking.Payment

object PaymentDocument : DocumentCodec<Payment>() {
    override fun encode(value: Payment): Map<String, Any?> =
        mapOf(
            "amountCents" to value.amountCents,
            "paidAt" to value.paidAt.stored(),
            "refundedAt" to value.refundedAt?.stored(),
        )

    override fun read(fields: Fields): Payment =
        Payment(
            fields.id,
            fields.long("amountCents"),
            fields.instant("paidAt"),
            fields.optionalInstant("refundedAt"),
        )
}
