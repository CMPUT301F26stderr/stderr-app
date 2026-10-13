package io.github.CMPUT301F26stderr.campsbridge.data.lottery

import io.github.CMPUT301F26stderr.campsbridge.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.Fields
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.parseDate
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.read
import io.github.CMPUT301F26stderr.campsbridge.domain.lottery.LotteryOffer

object LotteryOfferDocument : DocumentCodec<LotteryOffer>() {
    override fun encode(value: LotteryOffer): Map<String, Any?> =
        mapOf("nights" to value.nights.map { it.toString() })

    override fun read(fields: Fields): LotteryOffer =
        LotteryOffer(fields.id, fields.strings("nights").map(::parseDate))
}
