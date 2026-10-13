package io.github.CMPUT301F26stderr.campsbridge.data.campsite

import io.github.CMPUT301F26stderr.campsbridge.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.Fields
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.parseDate
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.read
import io.github.CMPUT301F26stderr.campsbridge.domain.campsite.CalendarNight

object CalendarNightDocument : DocumentCodec<CalendarNight>() {
    override fun encode(value: CalendarNight): Map<String, Any?> =
        mapOf(
            "isOffered" to value.isOffered,
            "reservationId" to value.reservationId,
        )

    override fun read(fields: Fields): CalendarNight =
        CalendarNight(
            parseDate(fields.id),
            fields.boolean("isOffered"),
            fields.optionalString("reservationId"),
        )
}
