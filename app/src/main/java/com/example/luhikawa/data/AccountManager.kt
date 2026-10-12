package com.example.luhikawa.data

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class StoredAccount(
    val uid: String,
    val email: String,
    val password: String,
    val displayName: String
)

class AccountManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("luhikawa_accounts", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveAccount(account: StoredAccount) {
        val accounts = getSavedAccounts().toMutableList()
        val index = accounts.indexOfFirst { it.uid == account.uid }

        if (index != -1) {
            accounts[index] = account
        } else {
            accounts.add(account)
        }
        saveList(accounts)
    }

    fun getSavedAccounts(): List<StoredAccount> {
        val json = prefs.getString("accounts_list", null) ?: return emptyList()
        val type = object : TypeToken<List<StoredAccount>>() {}.type
        return gson.fromJson(json, type)
    }

    fun removeAccount(uid: String) {
        val accounts = getSavedAccounts().filter { it.uid != uid }
        saveList(accounts)

        if (getCurrentAccountUid() == uid) {
            setCurrentAccount("")
        }
    }

    fun setCurrentAccount(uid: String) {
        prefs.edit().putString("current_active_uid", uid).apply()
    }

    fun getCurrentAccountUid(): String? {
        val uid = prefs.getString("current_active_uid", null)
        return if (uid.isNullOrBlank()) null else uid
    }

    fun getCurrentAccount(): StoredAccount? {
        val currentUid = getCurrentAccountUid() ?: return null
        return getSavedAccounts().find { it.uid == currentUid }
    }

    fun updateAccountData(uid: String, newName: String, newEmail: String) {
        val accounts = getSavedAccounts().toMutableList()
        val index = accounts.indexOfFirst { it.uid == uid }
        if (index != -1) {
            val oldAcc = accounts[index]

            val emailFinal = if (newEmail.isNotBlank()) newEmail else oldAcc.email
            val nameFinal = if (newName.isNotBlank()) newName else oldAcc.displayName

            accounts[index] = StoredAccount(
                uid = oldAcc.uid,
                email = emailFinal,
                password = oldAcc.password,
                displayName = nameFinal
            )
            saveList(accounts)
        }
    }

    private fun saveList(list: List<StoredAccount>) {
        prefs.edit().putString("accounts_list", gson.toJson(list)).apply()
    }
}

fun switchNextAccount(
    context: Context,
    accountManager: AccountManager,
    navController: NavController,
    onSuccess: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val savedAccounts = accountManager.getSavedAccounts()
    val currentUid = auth.currentUser?.uid ?: accountManager.getCurrentAccountUid()

    if (savedAccounts.size <= 1) return

    val currentIndex = savedAccounts.indexOfFirst { it.uid == currentUid }
    val nextIndex = if (currentIndex != -1) (currentIndex + 1) % savedAccounts.size else 0
    val nextAccount = savedAccounts[nextIndex]

    if (nextAccount.uid == currentUid) return

    if (nextAccount.email.isNotBlank() && nextAccount.password.isNotBlank()) {
        auth.signOut()

        auth.signInWithEmailAndPassword(nextAccount.email, nextAccount.password)
            .addOnSuccessListener { authResult ->
                val newUid = authResult.user?.uid ?: nextAccount.uid
                accountManager.setCurrentAccount(newUid)
                onSuccess()

                navController.navigate("greeting") {
                    popUpTo(0) { inclusive = true }
                }
            }
            .addOnFailureListener {
                accountManager.setCurrentAccount(nextAccount.uid)
                onSuccess()

                navController.navigate("greeting") {
                    popUpTo(0) { inclusive = true }
                }
            }
    } else {
        accountManager.setCurrentAccount(nextAccount.uid)
        auth.signOut()
        onSuccess()

        navController.navigate("greeting") {
            popUpTo(0) { inclusive = true }
        }
    }
}