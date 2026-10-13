package io.github.CMPUT301F26stderr.campsbridge.data.account

import com.google.firebase.firestore.FirebaseFirestore
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.FirestorePaths
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.activeAccount
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.atomic
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.observe
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.read
import io.github.CMPUT301F26stderr.campsbridge.data.firestore.require
import io.github.CMPUT301F26stderr.campsbridge.domain.account.Account
import io.github.CMPUT301F26stderr.campsbridge.domain.account.AccountRepository
import io.github.CMPUT301F26stderr.campsbridge.domain.account.AccountSettings
import io.github.CMPUT301F26stderr.campsbridge.domain.account.DeviceRegistration
import io.github.CMPUT301F26stderr.campsbridge.domain.account.Preferences
import io.github.CMPUT301F26stderr.campsbridge.domain.account.ProfileUpdate
import io.github.CMPUT301F26stderr.campsbridge.domain.account.Role
import io.github.CMPUT301F26stderr.campsbridge.domain.shared.PersistenceFailure.Reason.INACTIVE
import io.github.CMPUT301F26stderr.campsbridge.domain.shared.requireOperation
import kotlinx.coroutines.tasks.await
import java.time.Clock

class FirestoreAccountRepository(
    private val db: FirebaseFirestore,
    private val clock: Clock,
) : AccountRepository {
    override suspend fun get(id: String) =
        db.document(FirestorePaths.account(id)).read(AccountDocument)

    override fun observe(id: String) =
        db.document(FirestorePaths.account(id)).observe(AccountDocument)

    override fun observeAll() =
        db.collection("accounts").observe(AccountDocument)

    override suspend fun create(
        id: String,
        profile: ProfileUpdate,
    ): Account {
        require(profile.name.isNotBlank())
        val reference = db.document(FirestorePaths.account(id))
        return db.atomic { tx ->
            val existing = tx.read(reference, AccountDocument)
            if (existing != null) {
                requireOperation(existing.isActive, INACTIVE, "Account removed")
                existing
            } else {
                val account =
                    Account(
                        id,
                        profile.name,
                        profile.email,
                        profile.phone,
                        setOf(Role.ENTRANT),
                        Preferences(),
                        AccountSettings(),
                        clock.instant(),
                    )
                tx.set(reference, AccountDocument.encode(account))
                account
            }
        }
    }

    private suspend fun change(
        id: String,
        transform: (Account) -> Account,
    ) {
        db.atomic { tx ->
            val account = tx.activeAccount(db, id)
            tx.set(
                db.document(FirestorePaths.account(id)),
                AccountDocument.encode(transform(account)),
            )
        }
    }

    override suspend fun updateProfile(
        id: String,
        profile: ProfileUpdate,
    ) {
        require(profile.name.isNotBlank())
        change(id) { it.copy(name = profile.name, email = profile.email, phone = profile.phone) }
    }

    override suspend fun updateSettings(
        id: String,
        settings: AccountSettings,
    ) =
        change(id) { it.copy(settings = settings) }

    override suspend fun updatePreferences(
        id: String,
        preferences: Preferences,
    ) =
        change(id) { it.copy(preferences = preferences) }

    override suspend fun enableOwner(id: String) =
        change(id) { it.copy(roles = it.roles + Role.OWNER) }

    override suspend fun registerDevice(
        id: String,
        registration: DeviceRegistration,
    ) {
        require(registration.fcmToken.isNotBlank())
        db.atomic { tx ->
            tx.activeAccount(db, id)
            tx.set(
                db.document(FirestorePaths.device(id, registration.installationId)),
                DeviceRegistrationDocument.encode(registration.copy(updatedAt = clock.instant())),
            )
        }
    }

    override suspend fun unregisterDevice(
        id: String,
        installationId: String,
    ) {
        db.document(FirestorePaths.device(id, installationId)).delete().await()
    }
}
