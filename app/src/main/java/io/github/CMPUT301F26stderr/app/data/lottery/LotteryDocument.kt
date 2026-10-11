package io.github.CMPUT301F26stderr.app.data.lottery

import io.github.CMPUT301F26stderr.app.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.app.data.firestore.Fields
import io.github.CMPUT301F26stderr.app.data.firestore.cancellation
import io.github.CMPUT301F26stderr.app.data.firestore.read
import io.github.CMPUT301F26stderr.app.data.firestore.stored
import io.github.CMPUT301F26stderr.app.domain.lottery.Lottery
import io.github.CMPUT301F26stderr.app.domain.lottery.LotteryConfiguration

object LotteryDocument : DocumentCodec<Lottery>() {
    override fun encode(value: Lottery): Map<String, Any?> =
        mapOf(
            "ownerId" to value.ownerId,
            "title" to value.title,
            "description" to value.description,
            "registrationOpensAt" to value.configuration.registrationOpensAt.stored(),
            "registrationClosesAt" to value.configuration.registrationClosesAt.stored(),
            "bookingOpensAt" to value.configuration.bookingOpensAt.stored(),
            "bookingClosesAt" to value.configuration.bookingClosesAt.stored(),
            "winnerCount" to value.configuration.winnerCount,
            "waitlistCapacity" to value.configuration.waitlistCapacity,
            "waitingCount" to value.waitingCount,
            "createdAt" to value.createdAt.stored(),
            "publishedAt" to value.publishedAt?.stored(),
            "drawnAt" to value.drawnAt?.stored(),
            "cancellation" to value.cancellation?.stored(),
        )

    override fun read(fields: Fields): Lottery =
        Lottery(
            fields.id,
            fields.string("ownerId"),
            fields.string("title"),
            fields.string("description"),
            LotteryConfiguration(
                fields.instant("registrationOpensAt"),
                fields.instant("registrationClosesAt"),
                fields.instant("bookingOpensAt"),
                fields.instant("bookingClosesAt"),
                fields.long("winnerCount"),
                fields.optionalLong("waitlistCapacity"),
            ),
            fields.long("waitingCount"),
            fields.instant("createdAt"),
            fields.optionalInstant("publishedAt"),
            fields.optionalInstant("drawnAt"),
            fields.optionalNested("cancellation")?.cancellation(),
        )
}
