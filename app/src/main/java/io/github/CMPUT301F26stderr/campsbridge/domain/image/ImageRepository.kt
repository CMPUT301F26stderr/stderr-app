package io.github.CMPUT301F26stderr.campsbridge.domain.image

import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    suspend fun get(target: ImageTarget): ImageAttachment?

    fun observe(target: ImageTarget): Flow<ImageAttachment?>

    fun observeAll(): Flow<List<ImageAttachment>>

    suspend fun replace(
        target: ImageTarget,
        upload: ImageUpload,
    ): ImageUpdateResult

    suspend fun delete(target: ImageTarget): ImageUpdateResult

    suspend fun downloadUrl(blobId: String): String

    suspend fun retryCleanup(blobId: String)
}
