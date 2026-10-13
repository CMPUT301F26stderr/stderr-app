package io.github.CMPUT301F26stderr.campsbridge.data.firestore

import io.github.CMPUT301F26stderr.campsbridge.domain.shared.Cancellation
import io.github.CMPUT301F26stderr.campsbridge.domain.shared.Removal

fun Removal.stored() =
    mapOf("at" to at.stored(), "actorId" to actorId, "reason" to reason)

fun Cancellation.stored() =
    mapOf("at" to at.stored(), "actorId" to actorId, "reason" to reason)

fun Fields.removal() =
    Removal(instant("at"), string("actorId"), optionalString("reason"))

fun Fields.cancellation() =
    Cancellation(instant("at"), string("actorId"), optionalString("reason"))
