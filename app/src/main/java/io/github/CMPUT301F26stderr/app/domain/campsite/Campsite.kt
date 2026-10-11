package io.github.CMPUT301F26stderr.app.domain.campsite

import io.github.CMPUT301F26stderr.app.domain.shared.Coordinates
import io.github.CMPUT301F26stderr.app.domain.shared.Removal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class CampsiteDetails(
    val name: String,
    val description: String,
    val location: Coordinates,
    val locationLabel: String,
    val timeZone: String,
    val maxGuests: Long,
    val amenities: Set<String>,
    val tags: Set<String>,
    val nightlyPriceCents: Long,
    val rules: String,
) {
    init {
        require(name.isNotBlank())
        require(maxGuests > 0)
        require(nightlyPriceCents >= 0)
        ZoneId.of(timeZone)
    }
}

data class Campsite(
    val id: String,
    val ownerId: String,
    val details: CampsiteDetails,
    val createdAt: Instant,
    val publishedAt: Instant? = null,
    val removal: Removal? = null,
) {
    val isPublished: Boolean
        get() = publishedAt != null && removal == null
}

data class CalendarNight(
    val date: LocalDate,
    val isOffered: Boolean,
    val reservationId: String? = null,
) {
    val isFree: Boolean
        get() = isOffered && reservationId == null
}
