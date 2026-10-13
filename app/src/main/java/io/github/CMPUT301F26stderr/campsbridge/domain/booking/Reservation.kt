package io.github.CMPUT301F26stderr.campsbridge.domain.booking

import io.github.CMPUT301F26stderr.campsbridge.domain.account.Role
import io.github.CMPUT301F26stderr.campsbridge.domain.shared.Coordinates
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object BookingPolicy {
    const val VERSION = "full-refund-v1"
    const val TEXT = "Cancelling or rejecting a reservation returns the full simulated payment."
}

data class BookedCampsite(
    val name: String,
    val locationLabel: String,
    val location: Coordinates,
    val timeZone: String,
)

data class AcceptedTerms(
    val rules: String,
    val cancellationPolicyVersion: String,
    val acceptedAt: Instant,
)

data class ReservationCancellation(
    val at: Instant,
    val actorId: String,
    val initiatedAs: Role,
    val reason: String?,
)

data class Reservation(
    val id: String,
    val lotteryId: String,
    val entrantId: String,
    val campsiteId: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val guestCount: Long,
    val bookedCampsite: BookedCampsite,
    val acceptedTerms: AcceptedTerms,
    val createdAt: Instant,
    val cancellation: ReservationCancellation? = null,
) {
    init {
        require(checkOutDate > checkInDate)
        require(guestCount > 0)
    }

    val nightCount: Long
        get() = ChronoUnit.DAYS.between(checkInDate, checkOutDate)
}

data class Payment(
    val reservationId: String,
    val amountCents: Long,
    val paidAt: Instant,
    val refundedAt: Instant? = null,
) {
    init {
        require(amountCents >= 0)
    }
}

data class BookingRequest(
    val lotteryId: String,
    val entrantId: String,
    val campsiteId: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val guestCount: Long,
    val expectedTotalCents: Long,
    val acceptedRules: String,
    val policyVersion: String,
) {
    init {
        require(checkOutDate > checkInDate)
        require(guestCount > 0)
        require(expectedTotalCents >= 0)
    }
}

data class CancellationRequest(
    val reservationId: String,
    val actorId: String,
    val initiatedAs: Role,
    val reason: String? = null,
)

fun stayNights(
    start: LocalDate,
    end: LocalDate,
): List<LocalDate> {
    require(end > start)
    return generateSequence(start) { it.plusDays(1) }.takeWhile { it < end }.toList()
}
