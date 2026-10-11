package io.github.CMPUT301F26stderr.app.domain.image

import java.time.Instant

enum class ImageTargetType {
    ACCOUNT,
    CAMPSITE,
}

data class ImageTarget(
    val type: ImageTargetType,
    val id: String,
)

data class ImageAttachment(
    val target: ImageTarget,
    val blobId: String,
    val uploadedAt: Instant,
)

/** Content is opened by the caller; implementation owns closing the supplied stream. */
data class ImageUpload(
    val contentType: String,
    val openStream: () -> java.io.InputStream,
)

/** Publication succeeded even if obsolete object cleanup needs retry. */
data class ImageUpdateResult(
    val attachment: ImageAttachment?,
    val obsoleteBlobId: String?,
)
