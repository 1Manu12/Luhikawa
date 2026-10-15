package com.example.luhikawa.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.luhikawa.R
import com.example.luhikawa.data.TaskRepository
import com.example.luhikawa.ui.HomeComponents.EtiquetaTexto
import com.example.luhikawa.ui.HomeComponents.ImagenDerechaTextoIzquierda
import com.example.luhikawa.ui.HomeComponents.RectanguloCompletadoPapelera
import com.example.luhikawa.ui.HomeComponents.RectanguloConImagen
import com.example.luhikawa.ui.HomeComponents.getIconFromIndex
import com.example.luhikawa.ui.theme.luhikawaTheme
import java.util.Calendar
import java.util.TimeZone
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.luhikawa.ui.MainActivityPerfil
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.luhikawa.data.HomeTaskRepository
import com.example.luhikawa.data.MicrofinanzasRepository
import com.example.luhikawa.ui.mode.AppMode
import com.example.luhikawa.ui.mode.ModeViewModel
import com.example.luhikawa.ui.mode.config
import com.example.luhikawa.ui.mode.rememberModeViewModel
import com.google.firebase.firestore.Query

val InriaSerif = FontFamily(
    Font(R.font.inriaserif_regular)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            luhikawaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") { LoginScreen(navController = navController) }
                        composable("registro") { RegistroScreen(navController = navController) }
                        composable("greeting") { Greeting(navController = navController) }
                        composable("perfil") { PerfilScreen(navController = navController) }
                        composable("calendario") { CalendarScreen(navController = navController) }
                        composable("ia") { AiScreen()}
                        composable(
                            route = "recordatorio?taskId={taskId}",
                            arguments = listOf(
                                navArgument("taskId") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            val taskId = backStackEntry.arguments?.getString("taskId")
                            RecordatorioScreen(navController = navController, taskId = taskId)
                        }
                    }
                }
            }
        }
    }

    private fun pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    modeViewModel: ModeViewModel = rememberModeViewModel()
) {
    val context = LocalContext.current
    val modo = modeViewModel.mode
    val config = modo.config()

    val taskRepo = remember { TaskRepository() }
    val homeRepo = remember { HomeTaskRepository() }
    val financeRepo = remember { MicrofinanzasRepository() }

    val consulta: (String) -> Query = { cat ->
        when (modo) {
            AppMode.TAREAS -> taskRepo.getTasksQuery(cat, context)
            AppMode.HOGAR -> homeRepo.getTasksQuery(cat, context)
            AppMode.MICROFINANZAS -> financeRepo.getTasksQuery(cat, context)
        }
    }
    val completarDb: (String, Boolean, () -> Unit) -> Unit = { id, v, ok ->
        when (modo) {
            AppMode.TAREAS -> taskRepo.markTaskAsCompleted(id, v, ok)
            AppMode.HOGAR -> homeRepo.markTaskAsCompleted(id, v, ok)
            AppMode.MICROFINANZAS -> financeRepo.markTaskAsCompleted(id, v, ok)
        }
    }
    val importanteDb: (String, Boolean, () -> Unit) -> Unit = { id, actual, ok ->
        when (modo) {
            AppMode.TAREAS -> taskRepo.toggleTaskImportance(id, actual, ok)
            AppMode.HOGAR -> homeRepo.toggleTaskImportance(id, actual, ok)
            AppMode.MICROFINANZAS -> financeRepo.toggleTaskImportance(id, actual, ok)
        }
    }
    val fechaDb: (String, String, () -> Unit) -> Unit = { id, f, ok ->
        when (modo) {
            AppMode.TAREAS -> taskRepo.updateTaskDueDate(id, f, ok)
            AppMode.HOGAR -> homeRepo.updateTaskDueDate(id, f, ok)
            AppMode.MICROFINANZAS -> financeRepo.updateTaskDueDate(id, f, ok)
        }
    }
    val eliminarDb: (String, () -> Unit) -> Unit = { id, ok ->
        when (modo) {
            AppMode.TAREAS -> taskRepo.deleteTask(id, ok)
            AppMode.HOGAR -> homeRepo.deleteTask(id, ok)
            AppMode.MICROFINANZAS -> financeRepo.deleteTask(id, ok)
        }
    }

    // ── Textos de la interfaz según el modo ──
    val tituloModo: String? = when (modo) {
        AppMode.TAREAS -> null
        AppMode.HOGAR -> "Gestión del Hogar"
        AppMode.MICROFINANZAS -> "Microfinanzas"
    }
    val subtitulo = when (modo) {
        AppMode.TAREAS -> "Today´s Focus"
        AppMode.HOGAR -> "Lo que toca hacer en casa"
        AppMode.MICROFINANZAS -> "Tus movimientos"
    }
    val tituloImportantes = if (modo == AppMode.MICROFINANZAS) "Prioritarios" else "Importantes"
    val tituloCompletadas = when (modo) {
        AppMode.TAREAS -> "Completadas"
        AppMode.HOGAR -> "Hechas"
        AppMode.MICROFINANZAS -> "Saldados"
    }
    val tituloEliminar = when (modo) {
        AppMode.TAREAS -> "Eliminar tarea"
        AppMode.HOGAR -> "Eliminar tarea del hogar"
        AppMode.MICROFINANZAS -> "Eliminar movimiento"
    }
    val textoEliminar = when (modo) {
        AppMode.TAREAS -> "¿Estás segura de que la quieres eliminar?"
        AppMode.HOGAR -> "¿Quieres eliminar esta tarea del hogar?"
        AppMode.MICROFINANZAS -> "¿Quieres eliminar este movimiento?"
    }
    val tituloRestaurar = when (modo) {
        AppMode.TAREAS -> "Restaurar tarea"
        AppMode.HOGAR -> "Restaurar tarea del hogar"
        AppMode.MICROFINANZAS -> "Marcar como pendiente"
    }
    val textoRestaurar = when (modo) {
        AppMode.TAREAS -> "¿Quieres restablecer esta tarea a su estado anterior?"
        AppMode.HOGAR -> "¿Quieres volver a poner esta tarea del hogar como pendiente?"
        AppMode.MICROFINANZAS -> "¿Quieres volver a marcar este movimiento como pendiente?"
    }

    // ── Estado (se reinicia al cambiar de modo, así nunca se mezclan datos) ──
    var categoriaSeleccionada by remember(modo) { mutableStateOf("Todas") }
    val categorias = listOf("Todas") + config.categorias

    val frasesMotivacionales = listOf(
        "Un pequeño paso hoy es un gran logro mañana, así que da el primero sin miedo.",
        "Tú puedes con todo lo que te propongas, solo recuerda avanzar un paso a la vez.",
        "Hazlo con calma, pero hazlo: la constancia siempre le gana a la prisa.",
        "Cada tarea completada es una victoria, por pequeña que parezca. ¡Celébrala!",
        "Hoy es un buen día para empezar eso que tanto has estado dejando para después.",
        "No tienes que ser perfecta, solo constante. Lo que haces hoy suma mucho.",
        "Lo importante es avanzar, aunque sea despacito, porque cada paso te acerca a tu meta.",
        "Tu esfuerzo de hoy será tu orgullo de mañana, así que sigue adelante con confianza.",
        "Respira hondo, organiza tus ideas y sigue adelante: ya lograste cosas más difíciles.",
        "Eres más capaz de lo que imaginas, y cada día demuestras lo fuerte que eres."
    )

    val fraseDelDia = rememberSaveable { frasesMotivacionales.random() }
    val prefs = remember { context.getSharedPreferences("luhikawa_prefs", Context.MODE_PRIVATE) }
    var frasesActivas by remember { mutableStateOf(prefs.getBoolean("frases_activas", true)) }
    var showMenuFrases by remember { mutableStateOf(false) }

    var listaDeTareas by remember(modo) {
        mutableStateOf<List<Pair<String, Map<String, Any>>>>(emptyList())
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedTaskIdForDate by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskIdToDelete by remember { mutableStateOf<String?>(null) }

    var showRestoreDialog by remember { mutableStateOf(false) }
    var selectedTaskIdForRestore by remember { mutableStateOf<String?>(null) }

    val cargarTareas = {
        consulta(categoriaSeleccionada)
            .get()
            .addOnSuccessListener { result ->
                android.util.Log.d("GreetingDebug", "modo=$modo docs=${result.size()}")
                listaDeTareas = result.documents.mapNotNull { doc ->
                    doc.data?.let { data ->
                        // En "tasks" pueden quedar registros viejos de hogar/finanzas: no se muestran
                        val modoDoc = (data["mode"] as? String)?.ifBlank { null } ?: "tareas"
                        if (modo == AppMode.TAREAS && modoDoc != "tareas") null else doc.id to data
                    }
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("GreetingDebug", "Error consultando $modo", e)
                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    LaunchedEffect(modo, categoriaSeleccionada) {
        cargarTareas()
    }

    val tareasImportantes = listaDeTareas.filter { (_, tarea) ->
        val completada = tarea["completed"] as? Boolean ?: false
        val importante = tarea["important"] as? Boolean ?: false
        importante && !completada
    }

    val tareasActivas = listaDeTareas.filter { (_, tarea) ->
        val completada = tarea["completed"] as? Boolean ?: false
        val importante = tarea["important"] as? Boolean ?: false
        !completada && !importante
    }

    val tareasActivasOrdenadas = tareasActivas.sortedWith { a, b ->
        val fechaA = a.second["dueDate"] as? String
        val fechaB = b.second["dueDate"] as? String
        when {
            fechaA == null && fechaB == null -> 0
            fechaA == null -> 1
            fechaB == null -> -1
            else -> fechaA.compareTo(fechaB)
        }
    }

    val tareasCompletadas = listaDeTareas.filter { (_, tarea) ->
        tarea["completed"] as? Boolean ?: false
    }

    val tareasPendientesTotales = listaDeTareas.filter { (_, tarea) ->
        !(tarea["completed"] as? Boolean ?: false)
    }

    val pendientes = tareasPendientesTotales.size
    val textoContador = when (modo) {
        AppMode.TAREAS -> "Tienes $pendientes tareas pendientes"
        AppMode.HOGAR ->
            if (pendientes == 0) "¡No te falta ninguna tarea del hogar!"
            else "Te faltan $pendientes tareas del hogar"
        AppMode.MICROFINANZAS ->
            if (pendientes == 0) "Todos tus movimientos están saldados"
            else "Tienes $pendientes movimientos por saldar"
    }

    // Resumen financiero (solo se usa en microfinanzas)
    fun sumaPor(tipo: String) = listaDeTareas
        .filter { (_, t) -> t["category"] == tipo }
        .sumOf { (_, t) -> (t["amount"] as? Number)?.toDouble() ?: 0.0 }

    val totalIngresos = sumaPor("Ingreso")
    val totalGastos = sumaPor("Gasto")

    // Texto de cada fila (en microfinanzas agrega el monto)
    fun textoFila(tarea: Map<String, Any>): String {
        val tituloBase = tarea["title"] as? String ?: "Sin título"
        val fechaLegible = tarea["date"] as? String ?: ""
        val horaFormateada = tarea["time"] as? String ?: ""
        val infoTiempo = buildString {
            if (fechaLegible.isNotEmpty()) append(fechaLegible)
            if (fechaLegible.isNotEmpty() && horaFormateada.isNotEmpty()) append(" • ")
            if (horaFormateada.isNotEmpty()) append(horaFormateada)
        }
        val base = if (infoTiempo.isNotEmpty()) "$tituloBase - $infoTiempo" else tituloBase
        val monto = (tarea["amount"] as? Number)?.toDouble()
        return if (modo == AppMode.MICROFINANZAS && monto != null) "$base • \$%,.2f".format(monto) else base
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                RectanguloConImagen()

                // Cabecera solo en hogar y microfinanzas
                if (tituloModo != null) {
                    Text(
                        text = tituloModo,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 30.sp,
                        fontFamily = InriaSerif,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categorias.forEach { categoria ->
                        EtiquetaTexto(
                            texto = categoria,
                            isSelected = (categoria == categoriaSeleccionada),
                            onClick = { categoriaSeleccionada = categoria }
                        )
                    }
                }

                Text(
                    text = textoContador,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 26.sp,
                    fontFamily = InriaSerif,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(top = 16.dp)
                        .clickable { categoriaSeleccionada = "Todas" }
                )

                // Resumen financiero: SOLO en microfinanzas
                if (modo == AppMode.MICROFINANZAS) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ResumenCelda("Ingresos", totalIngresos)
                            ResumenCelda("Gastos", totalGastos)
                            ResumenCelda("Balance", totalIngresos - totalGastos)
                        }
                    }
                }

                Text(
                    text = subtitulo,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Importantes
                    if (tareasImportantes.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$tituloImportantes (${tareasImportantes.size})",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp,
                                fontFamily = InriaSerif,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        items(tareasImportantes.size) { index ->
                            val (id, tarea) = tareasImportantes[index]
                            val iconIndex = (tarea["icon"] as? Number)?.toInt() ?: 0

                            SwipeableTaskItem(
                                textoTarea = textoFila(tarea),
                                fechaTarea = tarea["date"] as? String,
                                isCafe = (index % 2 == 0),
                                iconIndex = iconIndex,
                                onCircleClick = { completarDb(id, true) { cargarTareas() } },
                                onImportanteClick = { importanteDb(id, true) { cargarTareas() } },
                                onFechaClick = {
                                    selectedTaskIdForDate = id
                                    showDatePicker = true
                                },
                                onBasuraClick = {
                                    taskIdToDelete = id
                                    showDeleteDialog = true
                                },
                                onEliminar = { eliminarDb(id) { cargarTareas() } },
                                onClick = { navController.navigate("recordatorio?taskId=$id") }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(12.dp)) }
                    }

                    // Activas
                    items(tareasActivasOrdenadas.size) { index ->
                        val (id, tarea) = tareasActivasOrdenadas[index]
                        val esImportante = tarea["important"] as? Boolean ?: false
                        val iconIndex = (tarea["icon"] as? Number)?.toInt() ?: 0

                        SwipeableTaskItem(
                            textoTarea = textoFila(tarea),
                            fechaTarea = tarea["date"] as? String,
                            isCafe = (index % 2 == 0),
                            iconIndex = iconIndex,
                            onCircleClick = { completarDb(id, true) { cargarTareas() } },
                            onImportanteClick = { importanteDb(id, esImportante) { cargarTareas() } },
                            onFechaClick = {
                                selectedTaskIdForDate = id
                                showDatePicker = true
                            },
                            onBasuraClick = {
                                taskIdToDelete = id
                                showDeleteDialog = true
                            },
                            onEliminar = { eliminarDb(id) { cargarTareas() } },
                            onClick = { navController.navigate("recordatorio?taskId=$id") }
                        )
                    }

                    // Completadas / Hechas / Saldados
                    if (tareasCompletadas.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "$tituloCompletadas (${tareasCompletadas.size})",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp,
                                fontFamily = InriaSerif,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(tareasCompletadas.size) { index ->
                            val (id, tarea) = tareasCompletadas[index]
                            val titulo = tarea["title"] as? String ?: "Sin título"
                            val monto = (tarea["amount"] as? Number)?.toDouble()
                            val textoCompletada =
                                if (modo == AppMode.MICROFINANZAS && monto != null)
                                    "$titulo • \$%,.2f".format(monto)
                                else titulo

                            RectanguloCompletadoPapelera(
                                textoTarea = textoCompletada,
                                onDeleteClick = { eliminarDb(id) { cargarTareas() } },
                                onClick = {
                                    selectedTaskIdForRestore = id
                                    showRestoreDialog = true
                                }
                            )
                        }
                    }
                }

                // Gato y frases (mantén presionado para activar/desactivar)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures(onLongPress = { showMenuFrases = true })
                        }
                ) {
                    if (frasesActivas) {
                        ImagenDerechaTextoIzquierda(frase = fraseDelDia)
                    }

                    DropdownMenu(
                        expanded = showMenuFrases,
                        onDismissRequest = { showMenuFrases = false },
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (frasesActivas) "Desactivar frases motivacionales"
                                    else "Activar frases motivacionales",
                                    fontFamily = InriaSerif,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                frasesActivas = !frasesActivas
                                prefs.edit().putBoolean("frases_activas", frasesActivas).apply()
                                showMenuFrases = false
                                Toast.makeText(
                                    context,
                                    if (frasesActivas) "Frases activadas" else "Frases desactivadas",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    selectedTaskIdForDate?.let { taskId ->
                                        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                            timeInMillis = millis
                                        }
                                        val fechaIso = "%04d-%02d-%02d".format(
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH) + 1,
                                            calendar.get(Calendar.DAY_OF_MONTH)
                                        )
                                        fechaDb(taskId, fechaIso) { cargarTareas() }
                                    }
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("Aceptar", fontFamily = InriaSerif, color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancelar", fontFamily = InriaSerif, color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    DatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            headlineContentColor = MaterialTheme.colorScheme.primary,
                            weekdayContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            subheadContentColor = MaterialTheme.colorScheme.onSurface,
                            yearContentColor = MaterialTheme.colorScheme.onSurface,
                            currentYearContentColor = MaterialTheme.colorScheme.primary,
                            selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
                            selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                            dayContentColor = MaterialTheme.colorScheme.onSurface,
                            disabledDayContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                            selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                            todayContentColor = MaterialTheme.colorScheme.primary,
                            todayDateBorderColor = MaterialTheme.colorScheme.primary,
                            navigationContentColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = {
                        Text(text = tituloEliminar, fontFamily = InriaSerif, color = MaterialTheme.colorScheme.onSurface)
                    },
                    text = {
                        Text(
                            text = textoEliminar,
                            fontFamily = InriaSerif,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                taskIdToDelete?.let { id -> eliminarDb(id) { cargarTareas() } }
                                showDeleteDialog = false
                                taskIdToDelete = null
                            }
                        ) {
                            Text("Sí, eliminar", color = MaterialTheme.colorScheme.error, fontFamily = InriaSerif)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                taskIdToDelete = null
                            }
                        ) {
                            Text("Cancelar", fontFamily = InriaSerif, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                )
            }

            if (showRestoreDialog) {
                AlertDialog(
                    onDismissRequest = { showRestoreDialog = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                    title = {
                        Text(text = tituloRestaurar, fontFamily = InriaSerif, color = MaterialTheme.colorScheme.onSurface)
                    },
                    text = {
                        Text(
                            text = textoRestaurar,
                            fontFamily = InriaSerif,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                selectedTaskIdForRestore?.let { id -> completarDb(id, false) { cargarTareas() } }
                                showRestoreDialog = false
                                selectedTaskIdForRestore = null
                            }
                        ) {
                            Text(
                                "Sí, restablecer",
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = InriaSerif
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showRestoreDialog = false
                                selectedTaskIdForRestore = null
                            }
                        ) {
                            Text("Cancelar", fontFamily = InriaSerif, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ResumenCelda(etiqueta: String, valor: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = etiqueta,
            fontFamily = InriaSerif,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = "\$%,.2f".format(valor),
            fontFamily = InriaSerif,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SwipeableTaskItem(
    textoTarea: String,
    fechaTarea: String? = null,
    isCafe: Boolean,
    iconIndex: Int,
    onCircleClick: () -> Unit,
    onImportanteClick: () -> Unit,
    onFechaClick: () -> Unit,
    onBasuraClick: () -> Unit,
    onEliminar: () -> Unit,
    onClick: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val maxRevealWidth = 270.dp
    val density = LocalDensity.current
    val maxRevealWidthPx = with(density) { maxRevealWidth.toPx() }

    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        label = "swipeOffset"
    )

    val cardBackground = if (isCafe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
    val actionBackground = if (isCafe) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
    val contentColor = if (isCafe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
    val actionContentColor = if (isCafe) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(22.dp))
    ) {
        // Fondo con acciones al hacer Swipe
        Row(
            modifier = Modifier
                .matchParentSize()
                .background(
                    color = actionBackground,
                    shape = RoundedCornerShape(22.dp)
                ),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight()
                    .clickable {
                        onImportanteClick()
                        offsetX = 0f
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Importante",
                        tint = actionContentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Importante",
                        fontSize = 9.sp,
                        color = actionContentColor,
                        fontFamily = InriaSerif
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight()
                    .clickable {
                        onFechaClick()
                        offsetX = 0f
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Fecha",
                        tint = actionContentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Fecha",
                        fontSize = 9.sp,
                        color = actionContentColor,
                        fontFamily = InriaSerif
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight()
                    .clickable {
                        onBasuraClick()
                        offsetX = 0f
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Basura",
                        tint = actionContentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Basura",
                        fontSize = 9.sp,
                        color = actionContentColor,
                        fontFamily = InriaSerif
                    )
                }
            }
        }

        // Tarjeta principal frontal
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset { IntOffset(animatedOffset.toInt(), 0) }
                .background(
                    color = cardBackground,
                    shape = RoundedCornerShape(22.dp)
                )
                .then(
                    if (!isCafe) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(22.dp))
                    else Modifier
                )
                .clickable { onClick() }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = offsetX + dragAmount
                            offsetX = newOffset.coerceIn(-maxRevealWidthPx, 0f)
                        },
                        onDragEnd = {
                            offsetX = if (offsetX < -maxRevealWidthPx / 2f) -maxRevealWidthPx else 0f
                        }
                    )
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(androidx.compose.ui.graphics.Color.Transparent, shape = CircleShape)
                        .border(
                            width = 1.dp,
                            color = contentColor,
                            shape = CircleShape
                        )
                        .clickable { onCircleClick() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = getIconFromIndex(iconIndex),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = textoTarea,
                        color = contentColor,
                        fontFamily = InriaSerif,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!fechaTarea.isNullOrEmpty()) {
                        Text(
                            text = fechaTarea,
                            color = contentColor.copy(alpha = 0.6f),
                            fontFamily = InriaSerif,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}