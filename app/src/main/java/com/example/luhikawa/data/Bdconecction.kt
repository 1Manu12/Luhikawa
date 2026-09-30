package com.example.luhikawa.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.ByteArrayOutputStream
import java.io.InputStream

class TaskRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // Tareas

    fun getTasksQuery(categoriaSeleccionada: String) =
        if (categoriaSeleccionada == "Todas") {
            db.collection("tasks")
        } else {
            db.collection("tasks").whereEqualTo("category", categoriaSeleccionada)
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
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("tasks")
            .add(taskMap)
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

// Usuarios

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
                    doc.getString("nombreCompleto")
                }
                onSuccess(nombres)
            }
    }

    fun getUserPhotoBase64(nombreCompleto: String, onSuccess: (String?) -> Unit) {
        db.collection("users")
            .whereEqualTo("nombreCompleto", nombreCompleto)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    onSuccess(result.documents[0].getString("fotoBase64"))
                } else {
                    onSuccess(null)
                }
            }
    }

    fun guardarFotoPerfilBase64(
        uri: Uri,
        context: Context,
        selectedAccount: String,
        onSuccess: (Bitmap, String) -> Unit
    ) {
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

                if (selectedAccount.isNotEmpty()) {
                    db.collection("users")
                        .whereEqualTo("nombreCompleto", selectedAccount)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (!querySnapshot.isEmpty) {
                                val docId = querySnapshot.documents[0].id
                                db.collection("users").document(docId)
                                    .set(mapOf("fotoBase64" to base64String), SetOptions.merge())
                                    .addOnSuccessListener {
                                        onSuccess(bitmapReducido, base64String)
                                    }
                            }
                        }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}