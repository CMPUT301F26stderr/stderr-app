package io.github.CMPUT301F26stderr.campsbridge.domain.account

import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun get(id: String): Account?

    fun observe(id: String): Flow<Account?>

    fun observeAll(): Flow<List<Account>>

    suspend fun create(
        id: String,
        profile: ProfileUpdate,
    ): Account

    suspend fun updateProfile(
        id: String,
        profile: ProfileUpdate,
    )

    suspend fun updateSettings(
        id: String,
        settings: AccountSettings,
    )

    suspend fun updatePreferences(
        id: String,
        preferences: Preferences,
    )

    suspend fun enableOwner(id: String)

    suspend fun registerDevice(
        id: String,
        registration: DeviceRegistration,
    )

    suspend fun unregisterDevice(
        id: String,
        installationId: String,
    )
}
