package io.github.CMPUT301F26stderr.app.data.review

import io.github.CMPUT301F26stderr.app.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.app.data.firestore.Fields
import io.github.CMPUT301F26stderr.app.data.firestore.read
import io.github.CMPUT301F26stderr.app.domain.review.Reaction

object ReactionDocument : DocumentCodec<Reaction>() {
    override fun encode(value: Reaction): Map<String, Any?> =
        mapOf("type" to value.type.name)

    override fun read(fields: Fields): Reaction =
        Reaction(fields.id, fields.enum("type"))
}
