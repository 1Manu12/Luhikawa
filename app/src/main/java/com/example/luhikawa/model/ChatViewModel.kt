package com.example.luhikawa.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luhikawa.BuildConfig
import com.example.luhikawa.domain.Chat
import com.example.luhikawa.ui.mode.AppMode
import com.example.luhikawa.ui.mode.config
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class MensajeChat(
    val mensaje: String,
    val deUsers: Boolean
)

data class EstadoChat(
    val mensajes: List<MensajeChat> = listOf(
        MensajeChat("¡Hola! Soy Luhi, tu asistente personal. ¿En qué puedo ayudarte hoy?", deUsers = false)
    ),
    val cargando: Boolean = false
)

class ChatViewModel : ViewModel() {
    private val chat = Chat()
    var conversationBot : Int = 0

    var geminiUse = false
    private val db = FirebaseFirestore.getInstance()
    private val claveApi = BuildConfig.ApiKey

    private var modo: AppMode = AppMode.TAREAS
    fun setModo(nuevo: AppMode) { modo = nuevo }

    private fun uidActual(): String? = FirebaseAuth.getInstance().currentUser?.uid

    private val modeloGemini = GenerativeModel(
        modelName = "gemini-3.6-flash",
        apiKey = claveApi,
        systemInstruction = content {
            text(
                "Te llamas Luhi. Eres la asistente inteligente oficial de la aplicación Luhikawa.\n" +
                        "Tu objetivo es ayudar al usuario a organizar sus registros según el MODO ACTUAL que se te indica en cada mensaje.\n" +
                        "Usa solo las categorías válidas del modo actual.\n\n" +
                        "INSTRUCCIONES PARA EJECUTAR ACCIONES CRUD:\n" +
                        "- Si el usuario te pide CREAR o AGREGAR algo, responde confirmando amablemente e incluye al final:\n" +
                        "  ACCION_CREAR: {\"title\": \"Nombre\", \"category\": \"Categoría válida\", \"dueDate\": \"AAAA-MM-DD\", \"amount\": 0}\n" +
                        "  (dueDate es opcional, solo si el usuario da una fecha. amount es solo para el modo microfinanzas.)\n\n" +
                        "- Si te pide COMPLETAR o MARCAR COMO HECHO, responde confirmando e incluye al final:\n" +
                        "  ACCION_COMPLETAR: {\"title\": \"Nombre\"}\n\n" +
                        "- Si te pide ELIMINAR o BORRAR, responde confirmando e incluye al final:\n" +
                        "  ACCION_ELIMINAR: {\"title\": \"Nombre\"}\n\n" +
                        "- Si el usuario solo pregunta o conversa, responde normalmente sin comandos."
            )
        }
    )

    private val sesionChat = modeloGemini.startChat()

    private val _estado = MutableStateFlow(EstadoChat())
    val estado: StateFlow<EstadoChat> = _estado.asStateFlow()

    private fun agregarMensajeLuhi(texto: String, cargando: Boolean = false) {
        _estado.value = _estado.value.copy(
            mensajes = _estado.value.mensajes + MensajeChat(texto, deUsers = false),
            cargando = cargando
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun enviarMensaje(textoUsers: String) {
        val textoLimpio = textoUsers.trim()
        if (textoLimpio.isEmpty() || _estado.value.cargando) return


        if(geminiUse) {

            if (claveApi.isBlank()) {
                agregarMensajeLuhi("Error: luhi esta perdido, intentalo mas tarde.")
                return
            }

            _estado.value = _estado.value.copy(
                mensajes = _estado.value.mensajes + MensajeChat(textoLimpio, deUsers = true),
                cargando = true
            )

            viewModelScope.launch {
                try {
                    val cfg = modo.config()
                    val resumenTareas = obtenerResumenFirestore()

                    val promptConContexto = buildString {
                        append("[Modo actual: ${modo.key} (${cfg.descripcionIA}). ")
                        append("Categorías válidas: ${cfg.categorias.joinToString(", ")}.]\n")
                        if (resumenTareas.isNotBlank()) append("[Estado actual:\n$resumenTareas]\n")
                        append("\nMensaje del usuario: $textoLimpio")
                    }

                    val respuesta = sesionChat.sendMessage(promptConContexto)
                    val textoOriginal = respuesta.text ?: "No pude obtener una respuesta."

                    agregarMensajeLuhi(procesarComandosCrud(textoOriginal))
                } catch (e: Exception) {
                    agregarMensajeLuhi("Error al conectar con Luhi")
                }
            }
        }else {

            if(textoLimpio.toIntOrNull() != null || conversationBot == 0 || chat.ask == 1) {

                when (conversationBot) {
                    0 -> {
                        conversationBot = 1
                        agregarMensajeLuhi(chat.greeting(), false)
                    }

                    1 -> {
                        val selectionBot = textoLimpio.toInt()
                        if(selectionBot == 1 || selectionBot == 2 || selectionBot == 3) {
                            conversationBot = 2
                            agregarMensajeLuhi(chat.message(selectionBot), false)
                        }else {
                            agregarMensajeLuhi("Opcion no correcta, intente de nuevo...")
                        }
                    }

                    2 -> {
                        if (chat.waitingData == true) {
                            agregarMensajeLuhi(chat.dataCollector(textoLimpio), false)
                            agregarMensajeLuhi("Recuerda de modificar la fecha de entrega ya que estas son predeterminadas por el bot con la misma fecha de hoy")
                            conversationBot = 0
                            chat.waitingData = false
                        }

                        if (chat.fail) {
                            chat.fail = false
                            conversationBot = 0
                            agregarMensajeLuhi(".......................", false)
                        }


                        if (chat.ask == 2 ) {
                           agregarMensajeLuhi(chat.activateModelB())
                        } else if (chat.ask == 3) {
                            conversationBot = 3
                        }
                    }

                    3 -> {
                        if (chat.ask == 3) {
                            val confirm = textoLimpio.toInt()
                            if (confirm == 1 || confirm == 2) {
                                when (confirm) {
                                    1 -> {
                                        conversationBot = 0
                                        chat.resetModel()
                                        agregarMensajeLuhi("reseteo exitoso", false)
                                        enviarMensaje("Hola")
                                    }

                                    2 -> {
                                        agregarMensajeLuhi("reseteo cancelado", false)
                                        conversationBot = 0
                                        agregarMensajeLuhi("......................")
                                        enviarMensaje("Hola")
                                    }

                                }
                            } else {
                                agregarMensajeLuhi("el numero digitado no se encuentr como opcion, digite 1 para borrar y 2 para cancelar")
                                agregarMensajeLuhi("intente de nuevo...")
                                conversationBot = 0
                                enviarMensaje(".")
                            }

                        }
                    }

                }
            }else{
                agregarMensajeLuhi("Digita una opcion correcta...", false )
                agregarMensajeLuhi("Intentalo de nuevo", false)
            }

        }
    }

    private suspend fun procesarComandosCrud(textoOriginal: String): String {
        var texto = textoOriginal

        if (texto.contains("ACCION_CREAR:")) {
            val cmd = texto.substringAfter("ACCION_CREAR:")
            val titulo = extraerValor(cmd, "title")
            if (titulo.isNotBlank()) {
                crearFirestore(
                    titulo = titulo,
                    categoria = extraerValor(cmd, "category"),
                    dueDate = extraerValor(cmd, "dueDate"),
                    monto = extraerNumero(cmd, "amount")
                )
            }
            texto = texto.substringBefore("ACCION_CREAR:").trim()
        }

        if (texto.contains("ACCION_COMPLETAR:")) {
            val titulo = extraerValor(texto.substringAfter("ACCION_COMPLETAR:"), "title")
            if (titulo.isNotBlank()) completarFirestore(titulo)
            texto = texto.substringBefore("ACCION_COMPLETAR:").trim()
        }

        if (texto.contains("ACCION_ELIMINAR:")) {
            val titulo = extraerValor(texto.substringAfter("ACCION_ELIMINAR:"), "title")
            if (titulo.isNotBlank()) eliminarFirestore(titulo)
            texto = texto.substringBefore("ACCION_ELIMINAR:").trim()
        }

        return texto
    }

    private fun extraerValor(texto: String, clave: String): String = try {
        "\"$clave\"\\s*:\\s*\"([^\"]*)\"".toRegex(RegexOption.IGNORE_CASE)
            .find(texto)?.groupValues?.get(1) ?: ""
    } catch (e: Exception) { "" }

    private fun extraerNumero(texto: String, clave: String): Double? = try {
        "\"$clave\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)".toRegex(RegexOption.IGNORE_CASE)
            .find(texto)?.groupValues?.get(1)?.toDouble()
    } catch (e: Exception) { null }

    /** Documentos del usuario actual que pertenecen al modo actual (sin "mode" = "tareas"). */
    private suspend fun documentosDelModo(): List<DocumentSnapshot> {
        val uid = uidActual() ?: return emptyList()
        return db.collection("tasks").whereEqualTo("userId", uid).get().await().documents
            .filter { (it.getString("mode")?.ifBlank { null } ?: "tareas") == modo.key }
    }

    private fun List<DocumentSnapshot>.buscarPorTitulo(titulo: String): DocumentSnapshot? =
        firstOrNull { it.getString("title").equals(titulo, ignoreCase = true) }
            ?: firstOrNull { it.getString("title")?.contains(titulo, ignoreCase = true) == true }

    private suspend fun crearFirestore(titulo: String, categoria: String, dueDate: String, monto: Double?) {
        try {
            val uid = uidActual() ?: return
            val cfg = modo.config()
            val categoriaFinal = if (categoria in cfg.categorias) categoria else cfg.categoriaInicial
            val fechaValida = Regex("\\d{4}-\\d{2}-\\d{2}").matches(dueDate)

            val nueva = mutableMapOf<String, Any>(
                "userId" to uid,
                "mode" to modo.key,
                "title" to titulo,
                "category" to categoriaFinal,
                "date" to if (fechaValida) dueDate else "",
                "dueDate" to if (fechaValida) dueDate else "",
                "time" to "",
                "importance" to "Media",
                "icon" to 0,
                "completed" to false
            )
            if (modo == AppMode.MICROFINANZAS && monto != null) nueva["amount"] = monto

            db.collection("tasks").add(nueva).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun completarFirestore(titulo: String) {
        try {
            documentosDelModo().buscarPorTitulo(titulo)?.reference?.update("completed", true)?.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun eliminarFirestore(titulo: String) {
        try {
            documentosDelModo().buscarPorTitulo(titulo)?.reference?.delete()?.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun obtenerResumenFirestore(): String {
        return try {
            if (uidActual() == null) return "El usuario no ha iniciado sesión."
            val docs = documentosDelModo()
            if (docs.isEmpty()) return "No hay registros en este modo."

            docs.mapIndexed { i, doc ->
                val titulo = doc.getString("title") ?: "Sin título"
                val categoria = doc.getString("category") ?: "General"
                val completada = doc.getBoolean("completed") ?: false
                val monto = doc.getDouble("amount")?.let { ", Monto: $it" } ?: ""
                "${i + 1}. $titulo [Categoría: $categoria, Completada: $completada$monto]"
            }.joinToString("\n")
        } catch (e: Exception) {
            "No pude consultar los registros."
        }
    }
}