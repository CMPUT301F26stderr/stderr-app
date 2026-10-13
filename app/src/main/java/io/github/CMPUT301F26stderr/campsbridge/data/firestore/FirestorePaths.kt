package io.github.CMPUT301F26stderr.campsbridge.data.firestore

import io.github.CMPUT301F26stderr.campsbridge.domain.image.ImageTarget
import io.github.CMPUT301F26stderr.campsbridge.domain.image.ImageTargetType
import java.time.LocalDate

object DocumentKeys {
    fun id(value: String): String {
        require(
            value.isNotBlank() &&
                value != "." &&
                value != ".." &&
                '/' !in value &&
                !value.matches(Regex("__.*__")),
        )
        require(value.toByteArray(Charsets.UTF_8).size <= 1500)
        return value
    }

    /** Length prefixes make tuples unambiguous, including IDs containing underscores. */
    fun compound(vararg parts: String): String =
        id(
            parts.joinToString("") {
                val p = id(it)
                "${p.length}:$p"
            },
        )

    fun reservation(
        lotteryId: String,
        entrantId: String,
    ): String =
        compound(lotteryId, entrantId)

    fun attachment(target: ImageTarget): String =
        id("${target.type.name.lowercase()}_${id(target.id)}")

    fun target(attachmentId: String): ImageTarget {
        val type =
            when (attachmentId.substringBefore('_')) {
                "account" -> ImageTargetType.ACCOUNT
                "campsite" -> ImageTargetType.CAMPSITE
                else -> throw IllegalArgumentException("Invalid image attachment ID")
            }
        return ImageTarget(type, id(attachmentId.substringAfter('_')))
    }

    fun blob(blobId: String): String =
        "images/${id(blobId)}/original"
}

object FirestorePaths {
    fun account(id: String) =
        "accounts/${DocumentKeys.id(id)}"

    fun device(
        account: String,
        installation: String,
    ) =
        "${account(account)}/devices/${DocumentKeys.id(installation)}"

    fun receipt(
        account: String,
        notification: String,
    ) =
        "${account(account)}/inbox/${DocumentKeys.id(notification)}"

    fun campsite(id: String) =
        "campsites/${DocumentKeys.id(id)}"

    fun night(
        campsite: String,
        date: LocalDate,
    ) =
        "${campsite(campsite)}/calendar/$date"

    fun lottery(id: String) =
        "lotteries/${DocumentKeys.id(id)}"

    fun offer(
        lottery: String,
        campsite: String,
    ) =
        "${lottery(lottery)}/offers/${DocumentKeys.id(campsite)}"

    fun entry(
        lottery: String,
        account: String,
    ) =
        "${lottery(lottery)}/entries/${DocumentKeys.id(account)}"

    fun reservation(id: String) =
        "reservations/${DocumentKeys.id(id)}"

    fun payment(id: String) =
        "payments/${DocumentKeys.id(id)}"

    fun image(target: ImageTarget) =
        "images/${DocumentKeys.attachment(target)}"

    fun review(
        campsite: String,
        author: String,
    ) =
        "${campsite(campsite)}/reviews/${DocumentKeys.id(author)}"

    fun reply(
        campsite: String,
        author: String,
        reply: String,
    ) =
        "${review(campsite, author)}/replies/${DocumentKeys.id(reply)}"

    fun reaction(
        campsite: String,
        author: String,
        reactor: String,
    ) =
        "${review(campsite, author)}/reactions/${DocumentKeys.id(reactor)}"

    fun notification(id: String) =
        "notifications/${DocumentKeys.id(id)}"

    fun message(id: String) =
        "messages/${DocumentKeys.id(id)}"
}
