package io.github.CMPUT301F26stderr.app.data.firestore

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/** Construct once, before any repository uses a client. No implicit project or credentials. */
data class FirebaseClients(
    val firestore: FirebaseFirestore,
    val storage: FirebaseStorage,
) {
    companion object {
        fun forApp(app: FirebaseApp): FirebaseClients =
            FirebaseClients(FirebaseFirestore.getInstance(app), FirebaseStorage.getInstance(app))

        fun forEmulators(
            app: FirebaseApp,
            host: String,
            firestorePort: Int = 8080,
            storagePort: Int = 9199,
        ): FirebaseClients {
            require(host.isNotBlank())
            return forApp(app).also {
                it.firestore.useEmulator(host, firestorePort)
                it.storage.useEmulator(host, storagePort)
            }
        }
    }
}
