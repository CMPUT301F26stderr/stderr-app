package io.github.CMPUT301F26stderr.campsbridge.domain.shared

import java.time.Instant

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
) {
    init {
        require(latitude.isFinite() && latitude in -90.0..90.0)
        require(longitude.isFinite() && longitude in -180.0..180.0)
    }
}

data class Removal(
    val at: Instant,
    val actorId: String,
    val reason: String? = null,
)

data class Cancellation(
    val at: Instant,
    val actorId: String,
    val reason: String? = null,
)

/** Expected operation failures. Transport/decoding failures remain distinguishable. */
class PersistenceFailure(
    val reason: Reason,
    message: String,
) : Exception(message) {
    enum class Reason {
        NOT_FOUND,
        INACTIVE,
        NOT_ALLOWED,
        CONFLICT,
        WINDOW_CLOSED,
        CAPACITY_REACHED,
        ALREADY_USED,
        STALE_QUOTE,
        INVALID_STATE,
    }
}

fun requireOperation(
    condition: Boolean,
    reason: PersistenceFailure.Reason,
    message: String,
) {
    if (!condition) throw PersistenceFailure(reason, message)
}
