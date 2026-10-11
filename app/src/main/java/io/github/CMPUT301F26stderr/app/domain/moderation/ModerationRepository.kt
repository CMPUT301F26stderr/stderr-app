package io.github.CMPUT301F26stderr.app.domain.moderation

interface ModerationRepository {
    /** Retains references, scrubs profile data, and resumes dependent cleanup on retries. */
    suspend fun removeAccount(
        accountId: String,
        actorId: String,
        reason: String? = null,
    )

    suspend fun removeCampsite(
        campsiteId: String,
        actorId: String,
        reason: String? = null,
    )

    suspend fun resumeRemovals()
}
