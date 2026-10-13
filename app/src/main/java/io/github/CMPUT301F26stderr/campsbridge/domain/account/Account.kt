package io.github.CMPUT301F26stderr.campsbridge.domain.account

import io.github.CMPUT301F26stderr.campsbridge.domain.shared.Removal
import java.time.Instant

enum class Role {
    ENTRANT,
    OWNER,
    ADMIN,
}

enum class ColorMode {
    STANDARD,
    COLOR_BLIND,
}

data class Preferences(
    val tags: Set<String> = emptySet(),
    val amenities: Set<String> = emptySet(),
)

data class AccountSettings(
    val notificationsEnabled: Boolean = true,
    val colorMode: ColorMode = ColorMode.STANDARD,
    val fontScale: Double = 1.0,
    val controlScale: Double = 1.0,
) {
    init {
        require(fontScale.isFinite() && fontScale > 0)
        require(controlScale.isFinite() && controlScale > 0)
    }
}

data class Account(
    val id: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val roles: Set<Role>,
    val preferences: Preferences,
    val settings: AccountSettings,
    val createdAt: Instant,
    val removal: Removal? = null,
) {
    init {
        require(removal != null || (name.isNotBlank() && roles.isNotEmpty()))
    }

    val isActive: Boolean
        get() = removal == null
}

data class DeviceRegistration(
    val installationId: String,
    val fcmToken: String,
    val updatedAt: Instant,
)

data class ProfileUpdate(
    val name: String,
    val email: String?,
    val phone: String?,
)
