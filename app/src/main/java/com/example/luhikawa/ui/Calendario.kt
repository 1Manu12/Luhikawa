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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
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
import com.example.luhikawa.ui.theme.InriaSerif
import com.example.luhikawa.ui.theme.luhikawaTheme
import com.google.firebase.firestore.FirebaseFirestore
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
fun CalendarScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var tareas by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }

    val hoy = LocalDate.now()
    var anio by remember { mutableStateOf(hoy.year) }
    var mes by remember { mutableStateOf(hoy.monthValue) }

    val cargarTareas = {
        db.collection("tasks").get()
            .addOnSuccessListener { result ->
                tareas = result.documents.mapNotNull { doc ->
                    doc.data?.let { it + ("taskId" to doc.id) }
                }
            }
    }

    LaunchedEffect(Unit) { cargarTareas() }

    val tareasPendientes = remember(tareas) {
        tareas.filter { tarea ->
            val completada = tarea["completed"] as? Boolean ?: false
            !completada
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
        val maxImportante = tareasDelDia.any { it["important"] as? Boolean == true }
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            RectanguloConImagen2()

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
                                    Text(
                                        text = dia.toString(),
                                        color = if (colorParaFecha(
                                                LocalDate.of(
                                                    anio,
                                                    mes,
                                                    dia
                                                )
                                            ) != null
                                        )
                                            MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
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
                    "Tareas asignadas",
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            val tareasDelDia = fechaSeleccionada?.let { tareasPorFecha[it] } ?: emptyList()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (tareasDelDia.isEmpty()) {
                    item {
                        Text(
                            text = if (fechaSeleccionada == null)
                                "Toca una fecha para ver sus tareas"
                            else "No hay tareas para este día",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            fontFamily = InriaSerif,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                } else {
                    items(tareasDelDia.size) { index ->
                        val tarea = tareasDelDia[index]
                        val titulo = tarea["title"] as? String ?: "Sin título"
                        val fechaLegible = tarea["date"] as? String ?: ""
                        val horaFormateada = tarea["time"] as? String ?: ""
                        val importante = tarea["important"] as? Boolean == true
                        val iconIndex = (tarea["icon"] as? Long)?.toInt() ?: 0
                        val id = tarea["taskId"] as? String

                        val infoTiempo = buildString {
                            if (fechaLegible.isNotEmpty()) append(fechaLegible)
                            if (fechaLegible.isNotEmpty() && horaFormateada.isNotEmpty()) append(" • ")
                            if (horaFormateada.isNotEmpty()) append(horaFormateada)
                        }
                        val textoConFecha =
                            if (infoTiempo.isNotEmpty()) "$titulo - $infoTiempo" else titulo

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .padding(vertical = 4.dp)
                        ) {
                            SwipeableTaskItem(
                                textoTarea = textoConFecha,
                                fechaTarea = tarea["date"] as? String,
                                isCafe = (index % 2 == 0),
                                iconIndex = iconIndex,
                                onCircleClick = {
                                    if (id != null) {
                                        db.collection("tasks").document(id)
                                            .update("completed", true)
                                            .addOnSuccessListener { cargarTareas() }
                                    }
                                },
                                onImportanteClick = {
                                    if (id != null) {
                                        db.collection("tasks").document(id)
                                            .update("important", !importante)
                                            .addOnSuccessListener { cargarTareas() }
                                    }
                                },
                                onFechaClick = {},
                                onBasuraClick = {},
                                onEliminar = {
                                    if (id != null) {
                                        db.collection("tasks").document(id)
                                            .delete()
                                            .addOnSuccessListener { cargarTareas() }
                                    }
                                },
                                onClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}