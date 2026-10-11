package io.github.CMPUT301F26stderr.app.data.review

import io.github.CMPUT301F26stderr.app.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.app.data.firestore.Fields
import io.github.CMPUT301F26stderr.app.data.firestore.read
import io.github.CMPUT301F26stderr.app.data.firestore.stored
import io.github.CMPUT301F26stderr.app.domain.review.Review

object ReviewDocument : DocumentCodec<Review>() {
    override fun encode(value: Review): Map<String, Any?> =
        mapOf(
            "rating" to value.rating,
            "body" to value.body,
            "createdAt" to value.createdAt.stored(),
            "updatedAt" to value.updatedAt.stored(),
        )

    override fun read(fields: Fields): Review =
        Review(
            fields.parentId,
            fields.id,
            fields.long("rating"),
            fields.optionalString("body"),
            fields.instant("createdAt"),
            fields.instant("updatedAt"),
        )
}
