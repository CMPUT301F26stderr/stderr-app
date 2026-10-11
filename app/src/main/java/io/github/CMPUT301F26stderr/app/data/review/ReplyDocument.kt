package io.github.CMPUT301F26stderr.app.data.review

import io.github.CMPUT301F26stderr.app.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.app.data.firestore.Fields
import io.github.CMPUT301F26stderr.app.data.firestore.read
import io.github.CMPUT301F26stderr.app.data.firestore.stored
import io.github.CMPUT301F26stderr.app.domain.review.Reply

object ReplyDocument : DocumentCodec<Reply>() {
    override fun encode(value: Reply): Map<String, Any?> =
        mapOf(
            "authorId" to value.authorId,
            "parentReplyId" to value.parentReplyId,
            "body" to value.body,
            "createdAt" to value.createdAt.stored(),
        )

    override fun read(fields: Fields): Reply =
        Reply(
            fields.id,
            fields.string("authorId"),
            fields.optionalString("parentReplyId"),
            fields.string("body"),
            fields.instant("createdAt"),
        )
}
