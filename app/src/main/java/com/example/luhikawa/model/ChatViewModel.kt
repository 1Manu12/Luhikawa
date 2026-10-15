package com.example.luhikawa.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luhikawa.BuildConfig
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
    private val db = FirebaseFirestore.getInstance()
    private val claveApi = BuildConfig.ApiKey

    private var modo: AppMode = AppMode.TAREAS
    fun setModo(nuevo: AppMode) { modo = nuevo }

    private fun uidActual(): String? = FirebaseAuth.getInstance().currentUser?.uid

    private fun coleccionActual(): String = when (modo) {
        AppMode.TAREAS -> "tasks"
        AppMode.HOGAR -> "home_tasks"
        AppMode.MICROFINANZAS -> "finance_movements"
    }

    private val tiposMovimiento = listOf("Ingreso", "Gasto", "Ahorro", "Deuda", "Cobro pendiente")

    private val modeloGemini = GenerativeModel(
        modelName = "gemini-3.6-flash",
        apiKey = claveApi,
        systemInstruction = content {
            text(
                "Te llamas Luhi. Eres la asistente inteligente oficial de la aplicación Luhikawa.\n" +
                        "Tu objetivo es ayudar al usuario a organizar sus registros según el MODO ACTUAL que se te indica en cada mensaje.\n" +
                        "Usa solo las categorías válidas del modo actual.\n\n" +
                        "MODOS:\n" +
                        "- tareas: tareas, hábitos y recordatorios personales.\n" +
                        "- hogar: tareas de la casa.\n" +
                        "- microfinanzas: movimientos de dinero. Cada movimiento lleva un tipo (Ingreso, Gasto, Ahorro, Deuda o Cobro pendiente) y un monto mayor a 0. Si el usuario no da el monto, pídeselo y NO incluyas el comando.\n\n" +
                        "INSTRUCCIONES PARA EJECUTAR ACCIONES CRUD:\n" +
                        "- Si el usuario te pide CREAR o AGREGAR algo, responde confirmando amablemente e incluye al final:\n" +
                        "  ACCION_CREAR: {\"title\": \"Nombre\", \"category\": \"Categoría válida\", \"dueDate\": \"AAAA-MM-DD\", \"amount\": 0}\n" +
                        "  (dueDate es opcional, solo si el usuario da una fecha. amount es solo para el modo microfinanzas.)\n\n" +
                        "- Si te pide COMPLETAR o MARCAR COMO HECHO (en microfinanzas: saldar), responde confirmando e incluye al final:\n" +
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

    fun enviarMensaje(textoUsers: String) {
        val textoLimpio = textoUsers.trim()
        if (textoLimpio.isEmpty() || _estado.value.cargando) return

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
                val resumen = obtenerResumenFirestore()

                val prompt = buildString {
                    append("[Modo actual: ${modo.key} (${cfg.descripcionIA}). ")
                    append("Categorías válidas: ${cfg.categorias.joinToString(", ")}.]\n")
                    if (resumen.isNotBlank()) append("[Estado actual:\n$resumen]\n")
                    append("\nMensaje del usuario: $textoLimpio")
                }

                val respuesta = sesionChat.sendMessage(prompt)
                val textoOriginal = respuesta.text ?: "No pude obtener una respuesta."

                agregarMensajeLuhi(procesarComandosCrud(textoOriginal))
            } catch (e: Exception) {
                android.util.Log.e("LuhiError", "Fallo en la conexión: ${e.message}", e)
                agregarMensajeLuhi("Error al conectar con Luhi")
            }
        }
    }

    private suspend fun procesarComandosCrud(textoOriginal: String): String {
        var texto = textoOriginal

        if (texto.contains("ACCION_CREAR:")) {
            val cmd = texto.substringAfter("ACCION_CREAR:")
            val titulo = extraerValor(cmd, "title")
            val aviso = if (titulo.isNotBlank()) {
                crearFirestore(
                    titulo = titulo,
                    categoria = extraerValor(cmd, "category"),
                    dueDate = extraerValor(cmd, "dueDate"),
                    monto = extraerNumero(cmd, "amount")
                )
            } else null
            texto = texto.substringBefore("ACCION_CREAR:").trim()
            if (aviso != null) texto = "$texto\n\n$aviso".trim()
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
        "\"$clave\"\\s*:\\s*\"?([0-9]+(?:\\.[0-9]+)?)\"?".toRegex(RegexOption.IGNORE_CASE)
            .find(texto)?.groupValues?.get(1)?.toDouble()
    } catch (e: Exception) { null }

    private suspend fun documentosDelModo(): List<DocumentSnapshot> {
        val uid = uidActual() ?: return emptyList()
        val docs = db.collection(coleccionActual())
            .whereEqualTo("userId", uid).get().await().documents

        // En "tasks" pueden quedar registros viejos de otros modos
        return if (modo == AppMode.TAREAS) {
            docs.filter { (it.getString("mode")?.ifBlank { null } ?: "tareas") == "tareas" }
        } else docs
    }

    private fun List<DocumentSnapshot>.buscarPorTitulo(titulo: String): DocumentSnapshot? =
        firstOrNull { it.getString("title").equals(titulo, ignoreCase = true) }
            ?: firstOrNull { it.getString("title")?.contains(titulo, ignoreCase = true) == true }

    private suspend fun crearFirestore(
        titulo: String,
        categoria: String,
        dueDate: String,
        monto: Double?
    ): String? {
        return try {
            val uid = uidActual() ?: return "No pude guardarlo: no hay sesión iniciada."
            val cfg = modo.config()
            val categoriaFinal = if (categoria in cfg.categorias) categoria else cfg.categoriaInicial

            if (modo == AppMode.MICROFINANZAS) {
                if (monto == null || monto <= 0.0) {
                    return "No lo guardé: falta el monto del movimiento. ¿De cuánto es?"
                }
                if (categoriaFinal !in tiposMovimiento) {
                    return "No lo guardé: el tipo debe ser Ingreso, Gasto, Ahorro, Deuda o Cobro pendiente."
                }
            }

            val fechaValida = Regex("\\d{4}-\\d{2}-\\d{2}").matches(dueDate)
            val meses = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
            val fechaLegible = if (fechaValida) {
                val p = dueDate.split("-")
                "${p[2].toInt()} ${meses[p[1].toInt() - 1]} ${p[0]}"
            } else ""

            val nueva = mutableMapOf<String, Any>(
                "userId" to uid,
                "mode" to modo.key,
                "title" to titulo,
                "category" to categoriaFinal,
                "date" to fechaLegible,
                "dueDate" to if (fechaValida) dueDate else "",
                "time" to "",
                "importance" to "Media",
                "icon" to 0,
                "completed" to false
            )
            if (modo == AppMode.MICROFINANZAS && monto != null) nueva["amount"] = monto

            db.collection(coleccionActual()).add(nueva).await()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            "No pude guardarlo: ${e.localizedMessage ?: "error desconocido"}"
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

            val lineas = docs.mapIndexed { i, doc ->
                val titulo = doc.getString("title") ?: "Sin título"
                val categoria = doc.getString("category") ?: "General"
                val completada = doc.getBoolean("completed") ?: false
                val monto = (doc.get("amount") as? Number)?.toDouble()
                    ?.let { ", Monto: $it" } ?: ""
                val estadoTxt = if (modo == AppMode.MICROFINANZAS) "Saldado: $completada" else "Completada: $completada"
                "${i + 1}. $titulo [Categoría: $categoria, $estadoTxt$monto]"
            }

            // En microfinanzas, un resumen para que la IA pueda responder "¿cuánto he gastado?"
            if (modo == AppMode.MICROFINANZAS) {
                fun suma(tipo: String) = docs
                    .filter { it.getString("category") == tipo }
                    .sumOf { (it.get("amount") as? Number)?.toDouble() ?: 0.0 }
                val ingresos = suma("Ingreso")
                val gastos = suma("Gasto")
                lineas.joinToString("\n") +
                        "\nTotales: Ingresos $ingresos, Gastos $gastos, Balance ${ingresos - gastos}"
            } else lineas.joinToString("\n")
        } catch (e: Exception) {
            "No pude consultar los registros."
        }
    }
}