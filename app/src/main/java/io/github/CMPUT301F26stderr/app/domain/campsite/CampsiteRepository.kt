package io.github.CMPUT301F26stderr.app.domain.campsite

import kotlinx.coroutines.flow.Flow

interface CampsiteRepository {
    suspend fun get(id: String): Campsite?

    fun observe(id: String): Flow<Campsite?>

    fun observePublished(): Flow<List<Campsite>>

    fun observeForOwner(ownerId: String): Flow<List<Campsite>>

    fun observeAll(): Flow<List<Campsite>>

    suspend fun create(
        ownerId: String,
        details: CampsiteDetails,
    ): String

    suspend fun update(
        id: String,
        ownerId: String,
        details: CampsiteDetails,
    )

    suspend fun publish(
        id: String,
        ownerId: String,
    )

    fun observeCalendar(
        id: String,
        start: java.time.LocalDate,
        endExclusive: java.time.LocalDate,
    ): Flow<List<CalendarNight>>

    suspend fun setOffered(
        id: String,
        ownerId: String,
        dates: Set<java.time.LocalDate>,
        offered: Boolean,
    )
}
