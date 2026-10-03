package com.example.luhikawa.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import java.io.ByteArrayOutputStream
import java.io.InputStream

class TaskRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    fun getUserId(context: Context? = null): String? {
        val authUid = auth.currentUser?.uid
        if (!authUid.isNullOrEmpty()) return authUid

        return context?.let { AccountManager(it).getCurrentAccountUid() }
    }

    fun getTasksQuery(categoriaSeleccionada: String, context: Context? = null): Query {
        val uid = getUserId(context) ?: return db.collection("tasks").whereEqualTo("userId", "NO_USER")

        var query: Query = db.collection("tasks").whereEqualTo("userId", uid)

        if (categoriaSeleccionada != "Todas") {
            query = query.whereEqualTo("category", categoriaSeleccionada)
        }

        return query
    }

    fun getTaskById(taskId: String, onSuccess: (Map<String, Any>?) -> Unit) {
        db.collection("tasks").document(taskId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onSuccess(document.data)
                } else {
                    onSuccess(null)
                }
            }
    }

    fun saveTask(
        taskMap: Map<String, Any>,
        context: Context? = null,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val uid = (taskMap["userId"] as? String) ?: getUserId(context)

        if (uid.isNullOrEmpty()) {
            onFailure(Exception("Usuario no autenticado"))
            return
        }

        val mutableTaskMap = taskMap.toMutableMap()
        mutableTaskMap["userId"] = uid

        db.collection("tasks")
            .add(mutableTaskMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun updateTask(
        taskId: String,
        taskMap: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("tasks").document(taskId)
            .update(taskMap)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun markTaskAsCompleted(taskId: String, completed: Boolean, onSuccess: () -> Unit) {
        db.collection("tasks").document(taskId)
            .update("completed", completed)
            .addOnSuccessListener { onSuccess() }
    }

    fun toggleTaskImportance(taskId: String, currentImportance: Boolean, onSuccess: () -> Unit) {
        db.collection("tasks").document(taskId)
            .update("important", !currentImportance)
            .addOnSuccessListener { onSuccess() }
    }

    fun updateTaskDueDate(taskId: String, fechaIso: String, onSuccess: () -> Unit) {
        db.collection("tasks").document(taskId)
            .update("dueDate", fechaIso)
            .addOnSuccessListener { onSuccess() }
    }

    fun deleteTask(taskId: String, onSuccess: () -> Unit) {
        db.collection("tasks").document(taskId)
            .delete()
            .addOnSuccessListener { onSuccess() }
    }
}

// Repositorio de Usuarios
class UserRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getUserProfileUrl(userId: String, onSuccess: (String) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onSuccess(document.getString("fotoPerfil") ?: "")
                }
            }
    }

    fun getAllUsersNames(onSuccess: (List<String>) -> Unit) {
        db.collection("users").get()
            .addOnSuccessListener { result ->
                val nombres = result.documents.mapNotNull { doc ->
                    doc.getString("displayName") ?: doc.getString("nombreCompleto")
                }
                onSuccess(nombres)
            }
    }

    fun getUserPhotoBase64(identifier: String, onSuccess: (String?) -> Unit) {
        if (identifier.isEmpty()) {
            onSuccess(null)
            return
        }

        db.collection("users").document(identifier).get()
            .addOnSuccessListener { doc ->
                if (doc.exists() && doc.contains("fotoBase64")) {
                    onSuccess(doc.getString("fotoBase64"))
                } else {
                    db.collection("users")
                        .whereEqualTo("displayName", identifier)
                        .get()
                        .addOnSuccessListener { result ->
                            if (!result.isEmpty) {
                                onSuccess(result.documents[0].getString("fotoBase64"))
                            } else {
                                db.collection("users")
                                    .whereEqualTo("nombreCompleto", identifier)
                                    .get()
                                    .addOnSuccessListener { fallbackRes ->
                                        if (!fallbackRes.isEmpty) {
                                            onSuccess(fallbackRes.documents[0].getString("fotoBase64"))
                                        } else {
                                            onSuccess(null)
                                        }
                                    }
                                    .addOnFailureListener { onSuccess(null) }
                            }
                        }
                        .addOnFailureListener { onSuccess(null) }
                }
            }
            .addOnFailureListener {
                onSuccess(null)
            }
    }

    fun guardarFotoPerfilBase64(
        uri: Uri,
        context: Context,
        identifier: String,
        onSuccess: (Bitmap, String) -> Unit
    ) {
        if (identifier.isEmpty()) return

        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmapOriginal = BitmapFactory.decodeStream(inputStream)

            if (bitmapOriginal != null) {
                val maxDimension = 300
                val ratio = Math.min(
                    maxDimension.toFloat() / bitmapOriginal.width,
                    maxDimension.toFloat() / bitmapOriginal.height
                )
                val width = Math.round(ratio * bitmapOriginal.width)
                val height = Math.round(ratio * bitmapOriginal.height)
                val bitmapReducido = Bitmap.createScaledBitmap(bitmapOriginal, width, height, true)

                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmapReducido.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

                val userRef = db.collection("users").document(identifier)
                userRef.get().addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        userRef.set(mapOf("fotoBase64" to base64String), SetOptions.merge())
                            .addOnSuccessListener { onSuccess(bitmapReducido, base64String) }
                    } else {
                        db.collection("users")
                            .whereEqualTo("displayName", identifier)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (!querySnapshot.isEmpty) {
                                    val docId = querySnapshot.documents[0].id
                                    db.collection("users").document(docId)
                                        .set(mapOf("fotoBase64" to base64String), SetOptions.merge())
                                        .addOnSuccessListener { onSuccess(bitmapReducido, base64String) }
                                }
                            }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteFotoDePerfil(
        userId: String,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (userId.isBlank()) {
            onError("ID de usuario no válido")
            return
        }

        val updates = mapOf<String, Any?>(
            "fotoBase64" to null,
            "photoBase64" to null
        )

        db.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener {
                onExito()
            }
            .addOnFailureListener { e ->
                db.collection("users").document(userId)
                    .set(updates, SetOptions.merge())
                    .addOnSuccessListener { onExito() }
                    .addOnFailureListener { err ->
                        onError(err.localizedMessage ?: "Error al quitar la foto")
                    }
            }
    }

    fun editnameUser(
        context: Context,
        targetUid: String,
        nuevoNombre: String,
        accountManager: AccountManager? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (targetUid.isBlank()) {
            onError("El ID de usuario no es válido.")
            return
        }

        if (firebaseUser != null && firebaseUser.uid == targetUid) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(nuevoNombre)
                .build()
            firebaseUser.updateProfile(profileUpdates)
        }

        // Guarda tanto displayName como nombreCompleto para mantener sincronizada la BD
        val datosActualizados = mapOf(
            "displayName" to nuevoNombre,
            "nombreCompleto" to nuevoNombre
        )

        db.collection("users").document(targetUid)
            .set(datosActualizados, SetOptions.merge())
            .addOnSuccessListener {
                accountManager?.updateAccountData(targetUid, nuevoNombre, firebaseUser?.email ?: "")
                Toast.makeText(context, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Error al actualizar el nombre en Firestore")
            }
    }

    fun editEmailUser(
        context: Context,
        targetUid: String,
        nuevoEmail: String,
        accountManager: AccountManager,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val authUser = FirebaseAuth.getInstance().currentUser

        // 1. Actualización en Firestore
        db.collection("users").document(targetUid)
            .set(mapOf("email" to nuevoEmail), SetOptions.merge())
            .addOnSuccessListener {
                val currentName = firebaseUserDisplayName(targetUid)
                accountManager.updateAccountData(targetUid, currentName, nuevoEmail)

                // 2. Actualización en Firebase Authentication
                if (authUser != null && authUser.uid == targetUid) {
                    authUser.verifyBeforeUpdateEmail(nuevoEmail)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Se envió enlace de verificación al nuevo correo.", Toast.LENGTH_LONG).show()
                            onSuccess()
                        }
                        .addOnFailureListener {
                            authUser.updateEmail(nuevoEmail)
                                .addOnSuccessListener {
                                    onSuccess()
                                }
                                .addOnFailureListener { e ->
                                    // Si Firebase Auth requiere reciente autenticación, Firestore ya se actualizó
                                    Toast.makeText(context, "Correo actualizado en la base de datos. Para la sesión principal, vuelve a iniciar sesión.", Toast.LENGTH_LONG).show()
                                    onSuccess()
                                }
                        }
                } else {
                    onSuccess()
                }
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Error al actualizar email en Firestore")
            }
    }

    private fun firebaseUserDisplayName(uid: String): String {
        val user = FirebaseAuth.getInstance().currentUser
        return if (user?.uid == uid) user.displayName ?: "Usuario" else "Usuario"
    }

    fun editPasswordUser(
        context: Context,
        passActual: String,
        nuevaPass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null || user.email.isNullOrEmpty()) {
            onError("No hay una sesión de usuario activa.")
            return
        }

        if (passActual.isBlank() || nuevaPass.isBlank()) {
            onError("Debes completar ambos campos de contraseña.")
            return
        }

        if (nuevaPass.length < 6) {
            onError("La nueva contraseña debe tener al menos 6 caracteres.")
            return
        }

        val credential = EmailAuthProvider.getCredential(user.email!!, passActual)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(nuevaPass)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onError(e.localizedMessage ?: "Error al cambiar la contraseña")
                    }
            }
            .addOnFailureListener {
                onError("La contraseña actual es incorrecta.")
            }
    }
}

@Composable
fun ProfileAvatar(
    nombreUsuario: String,
    modifier: Modifier = Modifier,
    repository: UserRepository = remember { UserRepository() },
    onClick: () -> Unit = {}
) {
    var base64String by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(nombreUsuario) {
        if (nombreUsuario.isNotEmpty()) {
            repository.getUserPhotoBase64(nombreUsuario) { photoBase64 ->
                base64String = photoBase64
            }
        }
    }

    val bitmap = remember(base64String) {
        if (!base64String.isNullOrEmpty()) {
            base64ToBitmap(base64String!!)
        } else {
            null
        }
    }

    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Foto por defecto",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}

fun decodeBase64ToBitmap(base64String: String?): ImageBitmap? {
    if (base64String.isNullOrEmpty()) return null
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

fun base64ToBitmap(base64Str: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}