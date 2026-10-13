package io.github.CMPUT301F26stderr.campsbridge.data.lottery

import io.github.CMPUT301F26stderr.campsbridge.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.Fields
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.read
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.require
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.stored
import io.github.CMPUT301F26stderr.campsbridge.domain.lottery.LotteryEntry

object LotteryEntryDocument : DocumentCodec<LotteryEntry>() {
    override fun encode(value: LotteryEntry): Map<String, Any?> =
        mapOf(
            "accountId" to value.accountId,
            "state" to value.state.name,
            "joinedAt" to value.joinedAt.stored(),
            "stateChangedAt" to value.stateChangedAt.stored(),
        )

    override fun read(fields: Fields): LotteryEntry {
        require(fields.id == fields.string("accountId"))
        return LotteryEntry(
            fields.parentId,
            fields.string("accountId"),
            fields.enum("state"),
            fields.instant("joinedAt"),
            fields.instant("stateChangedAt"),
        )
    }
}
