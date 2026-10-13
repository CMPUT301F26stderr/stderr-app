package io.github.CMPUT301F26stderr.campsbridge.data.account

import io.github.CMPUT301F26stderr.campsbridge.data.firestore.DocumentCodec
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.Fields
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.read
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.stored
import io.github.CMPUT301F26stderr.campsbridge.domain.account.DeviceRegistration

object DeviceRegistrationDocument : DocumentCodec<DeviceRegistration>() {
    override fun encode(value: DeviceRegistration): Map<String, Any?> =
        mapOf(
            "fcmToken" to value.fcmToken,
            "updatedAt" to value.updatedAt.stored(),
        )

    override fun read(fields: Fields): DeviceRegistration =
        DeviceRegistration(fields.id, fields.string("fcmToken"), fields.instant("updatedAt"))
}
