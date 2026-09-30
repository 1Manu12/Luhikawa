package com.example.luhikawa.domain.services


import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import android.os.Handler
import androidx.navigation.NavGraph.Companion.findStartDestination

object AuthService {

    fun handleGoogleCredentialResponse(
        result: GetCredentialResponse,
        auth: FirebaseAuth,
        db: FirebaseFirestore,
        context: Context,
        onSuccess: () -> Unit
    ) {
        val credential = result.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

                // Autenticación con Firebase Auth
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = auth.currentUser
                            val userId = firebaseUser?.uid ?: ""
                            val email = firebaseUser?.email ?: ""
                            val nombre = firebaseUser?.displayName ?: "Usuario Google"

                            // Operación con Firestore
                            val userMap = hashMapOf(
                                "uid" to userId,
                                "usuario" to nombre,
                                "nombreCompleto" to nombre,
                                "email" to email
                            )

                            db.collection("users").document(userId)
                                .set(userMap, SetOptions.merge())
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "¡Bienvenida de vuelta, $nombre!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Notificamos a la pantalla de Login para navegar
                                    onSuccess()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        context,
                                        "Error al guardar en Firestore: ${e.localizedMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        } else {
                            Toast.makeText(
                                context,
                                "Fallo en Firebase: ${task.exception?.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error al parsear credenciales: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}