package io.github.CMPUT301F26stderr.app.data.lottery

import io.github.CMPUT301F26stderr.app.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.app.data.firestore.Fields
import io.github.CMPUT301F26stderr.app.data.firestore.parseDate
import io.github.CMPUT301F26stderr.app.data.firestore.read
import io.github.CMPUT301F26stderr.app.domain.lottery.LotteryOffer

object LotteryOfferDocument : DocumentCodec<LotteryOffer>() {
    override fun encode(value: LotteryOffer): Map<String, Any?> =
        mapOf("nights" to value.nights.map { it.toString() })

    override fun read(fields: Fields): LotteryOffer =
        LotteryOffer(fields.id, fields.strings("nights").map(::parseDate))
}
