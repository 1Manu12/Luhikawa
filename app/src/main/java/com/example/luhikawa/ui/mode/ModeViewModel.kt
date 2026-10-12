package com.example.luhikawa.ui.mode

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth

class ModeViewModel(
    application: Application,
    private val uid: String
) : ViewModel() {

    private val prefs = application.getSharedPreferences("luhikawa_prefs", Context.MODE_PRIVATE)

    var mode by mutableStateOf(AppMode.fromKey(prefs.getString("modo_app_$uid", null)))
        private set

    // OJO: NO se llama setMode (choca con el setter generado de "mode")
    fun cambiarModo(newMode: AppMode) {
        mode = newMode
        prefs.edit().putString("modo_app_$uid", newMode.key).apply()
    }

    companion object {
        fun factory(application: Application, uid: String) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ModeViewModel(application, uid) as T
        }
    }
}

/** Valor por defecto para pantallas que no reciben el ModeViewModel por parámetro. */
@Composable
fun rememberModeViewModel(): ModeViewModel {
    val app = LocalContext.current.applicationContext as Application
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
    return viewModel(
        key = "mode_$uid",
        factory = ModeViewModel.factory(app, uid)
    )
}