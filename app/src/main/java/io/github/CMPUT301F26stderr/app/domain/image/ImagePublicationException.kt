package io.github.CMPUT301F26stderr.app.domain.image

/**
 * Upload succeeded, but publication acknowledgement failed. Reconcile the attachment before
 * cleanup.
 */
class ImagePublicationException(
    val target: ImageTarget,
    val uploadedBlobId: String,
    cause: Throwable,
) : Exception("Image publication needs reconciliation", cause)
