package io.github.CMPUT301F26stderr.app.data.image

import io.github.CMPUT301F26stderr.app.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.app.data.firestore.DocumentKeys
import io.github.CMPUT301F26stderr.app.data.firestore.Fields
import io.github.CMPUT301F26stderr.app.data.firestore.read
import io.github.CMPUT301F26stderr.app.data.firestore.stored
import io.github.CMPUT301F26stderr.app.domain.image.ImageAttachment

object ImageAttachmentDocument : DocumentCodec<ImageAttachment>() {
    override fun encode(value: ImageAttachment): Map<String, Any?> =
        mapOf(
            "blobId" to value.blobId,
            "uploadedAt" to value.uploadedAt.stored(),
        )

    override fun read(fields: Fields): ImageAttachment =
        ImageAttachment(
            DocumentKeys.target(fields.id),
            fields.string("blobId"),
            fields.instant("uploadedAt"),
        )
}
