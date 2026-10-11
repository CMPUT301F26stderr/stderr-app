package io.github.CMPUT301F26stderr.app.domain.lottery

import kotlinx.coroutines.flow.Flow

interface LotteryRepository {
    suspend fun get(id: String): Lottery?

    fun observe(id: String): Flow<Lottery?>

    fun observeAll(): Flow<List<Lottery>>

    fun observeForOwner(ownerId: String): Flow<List<Lottery>>

    fun observeOffers(id: String): Flow<List<LotteryOffer>>

    fun observeEntries(id: String): Flow<List<LotteryEntry>>

    fun observeForEntrant(accountId: String): Flow<List<LotteryEntry>>

    suspend fun create(
        ownerId: String,
        title: String,
        description: String,
        configuration: LotteryConfiguration,
    ): String

    suspend fun update(
        id: String,
        ownerId: String,
        title: String,
        description: String,
        configuration: LotteryConfiguration,
    )

    suspend fun setOffer(
        id: String,
        ownerId: String,
        offer: LotteryOffer,
    )

    suspend fun removeOffer(
        id: String,
        ownerId: String,
        campsiteId: String,
    )

    suspend fun publish(
        id: String,
        ownerId: String,
    )

    suspend fun join(
        id: String,
        accountId: String,
    )

    suspend fun withdraw(
        id: String,
        accountId: String,
    )

    suspend fun decline(
        id: String,
        accountId: String,
    )

    suspend fun draw(id: String): DrawResult

    suspend fun cancel(
        id: String,
        actorId: String,
        reason: String? = null,
    )
}
