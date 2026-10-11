package io.github.CMPUT301F26stderr.app.domain.booking

import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    suspend fun get(id: String): Reservation?

    suspend fun getPayment(id: String): Payment?

    fun observeForEntrant(entrantId: String): Flow<List<Reservation>>

    fun observeForCampsite(campsiteId: String): Flow<List<Reservation>>

    suspend fun reserve(request: BookingRequest): String

    suspend fun cancel(request: CancellationRequest)
}
