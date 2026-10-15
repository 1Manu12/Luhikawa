package com.example.luhikawa.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.luhikawa.data.HomeTaskRepository
import com.example.luhikawa.data.MicrofinanzasRepository
import com.example.luhikawa.data.TaskRepository
import com.example.luhikawa.ui.HomeComponents.RectanguloConImagen
import com.example.luhikawa.ui.mode.AppMode
import com.example.luhikawa.ui.mode.ModeViewModel          // MODO
import com.example.luhikawa.ui.mode.config                 // MODO
import com.example.luhikawa.ui.mode.rememberModeViewModel  // MODO
import com.example.luhikawa.ui.theme.InriaSerif
import com.example.luhikawa.ui.theme.luhikawaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import java.time.LocalDate


class MainActivityCalendar : ComponentActivity() {
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
                        startDestination = "calendario"
                    ) {
                        composable(route = "calendario") {
                            CalendarScreen(navController = navController)
                        }

                        composable(route = "perfil") {
                            PerfilScreen(navController = navController)
                        }

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
}

@Composable
fun CalendarScreen(
    navController: NavController,
    modeViewModel: ModeViewModel = rememberModeViewModel()   // MODO: opcional
) {
    val modo = modeViewModel.mode                            // MODO
    val config = modo.config()                               // MODO

    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }

    val taskRepo = remember { TaskRepository() }
    val homeRepo = remember { HomeTaskRepository() }
    val financeRepo = remember { MicrofinanzasRepository() }

    val consulta: () -> Query = {
        when (modo) {
            AppMode.TAREAS -> taskRepo.getTasksQuery("Todas", context)
            AppMode.HOGAR -> homeRepo.getTasksQuery("Todas", context)
            AppMode.MICROFINANZAS -> financeRepo.getTasksQuery("Todas", context)
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
    val eliminarDb: (String, () -> Unit) -> Unit = { id, ok ->
        when (modo) {
            AppMode.TAREAS -> taskRepo.deleteTask(id, ok)
            AppMode.HOGAR -> homeRepo.deleteTask(id, ok)
            AppMode.MICROFINANZAS -> financeRepo.deleteTask(id, ok)
        }
    }

//Se reinicia al cambiar de modo
    var tareas by remember(modo) { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var fechaSeleccionada by remember(modo) { mutableStateOf<LocalDate?>(null) }

    val hoy = LocalDate.now()
    var anio by remember { mutableStateOf(hoy.year) }
    var mes by remember { mutableStateOf(hoy.monthValue) }

    val cargarTareas = {
        val uid = taskRepo.getUserId(context)
        if (!uid.isNullOrEmpty()) {
            consulta()
                .get()
                .addOnSuccessListener { result ->
                    tareas = result.documents.mapNotNull { doc ->
                        doc.data?.let { data ->
                            // En "tasks" pueden quedar registros viejos de hogar/finanzas: no se muestran
                            val modoDoc = (data["mode"] as? String)?.ifBlank { null } ?: "tareas"
                            if (modo == AppMode.TAREAS && modoDoc != "tareas") null
                            else data + ("taskId" to doc.id)
                        }
                    }
                }
                .addOnFailureListener {
                    tareas = emptyList()
                }
        } else {
            tareas = emptyList()
        }
    }

    LaunchedEffect(auth.currentUser?.uid, modo) { cargarTareas() }

    // Solo pendientes del modo actual
    val tareasPendientes = remember(tareas, modo) {
        tareas.filter { tarea ->
            !(tarea["completed"] as? Boolean ?: false)
        }
    }

    // Agrupar tareas pendientes por fecha
    val tareasPorFecha = remember(tareasPendientes) {
        tareasPendientes.groupBy { tarea ->
            val fechaStr = tarea["dueDate"] as? String ?: return@groupBy null
            try {
                LocalDate.parse(fechaStr)
            } catch (e: Exception) {
                null
            }
        }.filterKeys { it != null }
    }

    @Composable
    fun colorParaFecha(fecha: LocalDate): Color? {
        val tareasDelDia = tareasPorFecha[fecha] ?: return null
        val maxImportante = tareasDelDia.any {
            it["important"] as? Boolean == true || it["importance"] == "Alta"
        }
        return if (maxImportante) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
    }

    val mesNombre = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )[mes - 1]

    val primerDia = LocalDate.of(anio, mes, 1)
    val diasEnMes = primerDia.lengthOfMonth()
    val offset = primerDia.dayOfWeek.value % 7
    val celdas = List(offset) { null } + (1..diasEnMes).map { it }
    val celdasCompletas = celdas + List((7 - (celdas.size % 7)) % 7) { null }

    val tareasDelDia = fechaSeleccionada?.let { tareasPorFecha[it] } ?: emptyList()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                RectanguloConImagen()
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector de mes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Mes anterior",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.clickable {
                                if (mes == 1) {
                                    mes = 12
                                    anio -= 1
                                } else {
                                    mes -= 1
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "$mesNombre $anio",
                            style = TextStyle(
                                fontFamily = InriaSerif,
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = "Mes siguiente",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.clickable {
                                if (mes == 12) {
                                    mes = 1
                                    anio += 1
                                } else {
                                    mes += 1
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Días de la semana
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("D", "L", "M", "M", "J", "V", "S").forEach { d ->
                            Text(
                                text = d,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                fontFamily = InriaSerif
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Cuadrícula del mes
                    celdasCompletas.chunked(7).forEach { semana ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            semana.forEach { dia ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(
                                            if (dia != null) {
                                                val fecha = LocalDate.of(anio, mes, dia)
                                                colorParaFecha(fecha) ?: Color.Transparent
                                            } else Color.Transparent
                                        )
                                        .clickable(enabled = dia != null) {
                                            if (dia != null) {
                                                fechaSeleccionada = LocalDate.of(anio, mes, dia)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (dia != null) {
                                        val fechaActual = LocalDate.of(anio, mes, dia)
                                        Text(
                                            text = dia.toString(),
                                            color = if (colorParaFecha(fechaActual) != null)
                                                MaterialTheme.colorScheme.onPrimary
                                            else
                                                MaterialTheme.colorScheme.onBackground,
                                            fontFamily = InriaSerif,
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        config.tituloLista,
                        style = TextStyle(
                            fontFamily = InriaSerif,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (tareasDelDia.isEmpty()) {
                item {
                    Text(
                        text = if (fechaSeleccionada == null)
                            "Toca una fecha para ver sus registros"
                        else config.textoSinItems,   // MODO
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontFamily = InriaSerif,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            } else {
                items(tareasDelDia.size) { index ->
                    val tarea = tareasDelDia[index]
                    val titulo = tarea["title"] as? String ?: tarea["titulo"] as? String ?: "Sin título"
                    val fechaLegible = tarea["date"] as? String ?: ""
                    val horaFormateada = tarea["time"] as? String ?: ""
                    val importante = tarea["important"] as? Boolean == true
                    val iconIndex = (tarea["icon"] as? Number)?.toInt() ?: 0
                    val id = tarea["taskId"] as? String
                    val monto = (tarea["amount"] as? Number)?.toDouble()   // MODO

                    val infoTiempo = buildString {
                        if (fechaLegible.isNotEmpty()) append(fechaLegible)
                        if (fechaLegible.isNotEmpty() && horaFormateada.isNotEmpty()) append(" • ")
                        if (horaFormateada.isNotEmpty()) append(horaFormateada)
                    }
                    val textoBase =
                        if (infoTiempo.isNotEmpty()) "$titulo - $infoTiempo" else titulo
//Muestra de monto
                    val textoConFecha =
                        if (monto != null) "$textoBase • \$%,.2f".format(monto) else textoBase

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        SwipeableTaskItem(
                            textoTarea = textoConFecha,
                            fechaTarea = tarea["date"] as? String,
                            isCafe = (index % 2 == 0),
                            iconIndex = iconIndex,
                            onCircleClick = {
                                if (id != null) {
                                    completarDb(id, true) { cargarTareas() }
                                }
                            },
                            onImportanteClick = {
                                if (id != null) {
                                    importanteDb(id, importante) { cargarTareas() }
                                }
                            },
                            onFechaClick = {},
                            onBasuraClick = {},
                            onEliminar = {
                                if (id != null) {
                                    eliminarDb(id) { cargarTareas() }
                                }
                            },
                            onClick = {
                                if (id != null) navController.navigate("recordatorio?taskId=$id")
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}