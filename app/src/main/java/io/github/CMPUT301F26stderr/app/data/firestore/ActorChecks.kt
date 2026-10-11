package io.github.CMPUT301F26stderr.app.data.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import io.github.CMPUT301F26stderr.app.data.account.AccountDocument
import io.github.CMPUT301F26stderr.app.domain.account.Account
import io.github.CMPUT301F26stderr.app.domain.account.Role
import io.github.CMPUT301F26stderr.app.domain.shared.PersistenceFailure.Reason.INACTIVE
import io.github.CMPUT301F26stderr.app.domain.shared.PersistenceFailure.Reason.NOT_ALLOWED
import io.github.CMPUT301F26stderr.app.domain.shared.requireOperation

internal fun Transaction.activeAccount(
    db: FirebaseFirestore,
    id: String,
    role: Role? = null,
): Account {
    val account = require(db.document(FirestorePaths.account(id)), AccountDocument)
    requireOperation(account.isActive, INACTIVE, "Account removed")
    requireOperation(role == null || role in account.roles, NOT_ALLOWED, "Account lacks role")
    return account
}
