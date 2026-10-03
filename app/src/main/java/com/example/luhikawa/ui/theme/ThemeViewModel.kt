package com.example.luhikawa.ui.theme

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var themeListenerRegistration: ListenerRegistration? = null
    private var activeUserId: String? = null

    private val _appThemeColor = MutableStateFlow(AppTheme.BEIGE)
    val appThemeColor: StateFlow<AppTheme> = _appThemeColor.asStateFlow()

    fun setUser(userId: String) {
        // Si el usuario no ha cambiado y ya hay un listener activo, no hacemos nada
        if (activeUserId == userId && themeListenerRegistration != null) return

        // 1. Desconectar inmediatamente el listener de la cuenta anterior
        themeListenerRegistration?.remove()
        themeListenerRegistration = null
        activeUserId = userId

        // 2. IMPORTANTE: Resetear el tema a Beige por defecto antes de cargar los datos de la nueva cuenta
        _appThemeColor.value = AppTheme.BEIGE

        if (userId == "guest" || userId.isBlank()) return

        // 3. Suscribirse ÚNICAMENTE al documento específico del UID activo
        themeListenerRegistration = db.collection("usuarios")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener

                val nombreColor = snapshot.getString("colorTema")
                if (nombreColor != null) {
                    val nuevoTema = try {
                        AppTheme.valueOf(nombreColor)
                    } catch (e: Exception) {
                        AppTheme.BEIGE
                    }
                    _appThemeColor.value = nuevoTema
                }
            }
    }

    fun setTheme(theme: AppTheme) {
        val currentUid = auth.currentUser?.uid ?: activeUserId ?: return
        if (currentUid == "guest" || currentUid.isBlank()) return

        // 1. Reflejar inmediatamente el cambio en la interfaz del usuario actual
        _appThemeColor.value = theme

        // 2. Guardar el tema EXCLUSIVAMENTE en el documento del UID del usuario autenticado
        val data = mapOf("colorTema" to theme.name)
        db.collection("usuarios")
            .document(currentUid)
            .set(data, SetOptions.merge())
    }

    override fun onCleared() {
        super.onCleared()
        themeListenerRegistration?.remove()
    }
}