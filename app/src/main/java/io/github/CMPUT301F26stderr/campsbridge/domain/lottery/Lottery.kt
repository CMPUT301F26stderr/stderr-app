package io.github.CMPUT301F26stderr.campsbridge.domain.lottery

import io.github.CMPUT301F26stderr.campsbridge.domain.shared.Cancellation
import java.time.Instant
import java.time.LocalDate

data class LotteryConfiguration(
    val registrationOpensAt: Instant,
    val registrationClosesAt: Instant,
    val bookingOpensAt: Instant,
    val bookingClosesAt: Instant,
    val winnerCount: Long,
    val waitlistCapacity: Long?,
) {
    init {
        require(registrationOpensAt < registrationClosesAt)
        require(registrationClosesAt <= bookingOpensAt && bookingOpensAt < bookingClosesAt)
        require(winnerCount > 0)
        require(waitlistCapacity == null || waitlistCapacity > 0)
    }
}

data class Lottery(
    val id: String,
    val ownerId: String,
    val title: String,
    val description: String,
    val configuration: LotteryConfiguration,
    val waitingCount: Long,
    val createdAt: Instant,
    val publishedAt: Instant? = null,
    val drawnAt: Instant? = null,
    val cancellation: Cancellation? = null,
) {
    init {
        require(title.isNotBlank())
        require(waitingCount >= 0)
    }

    fun registrationIsOpen(now: Instant) =
        publishedAt != null &&
            cancellation == null &&
            drawnAt == null &&
            now >= configuration.registrationOpensAt &&
            now < configuration.registrationClosesAt

    fun bookingIsOpen(now: Instant) =
        publishedAt != null &&
            cancellation == null &&
            drawnAt != null &&
            now >= configuration.bookingOpensAt &&
            now < configuration.bookingClosesAt
}

data class LotteryOffer(
    val campsiteId: String,
    val nights: List<LocalDate>,
) {
    init {
        require(nights.isNotEmpty() && nights == nights.distinct().sorted())
    }
}

enum class EntryState {
    WAITING,
    WITHDRAWN,
    LOST,
    OFFERED,
    DECLINED,
}

data class LotteryEntry(
    val lotteryId: String,
    val accountId: String,
    val state: EntryState,
    val joinedAt: Instant,
    val stateChangedAt: Instant,
)

enum class DrawResult {
    COMPLETED,
    ALREADY_DRAWN,
}
