package com.example.luhikawa.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.luhikawa.ui.theme.BgBeigea
import com.example.luhikawa.ui.theme.BgDarka
import com.example.luhikawa.ui.theme.InriaSerif
import com.example.luhikawa.ui.theme.TextBeigea
import com.example.luhikawa.ui.theme.TextDarka
import com.example.luhikawa.data.TaskRepository
import com.example.luhikawa.R
import java.time.Instant
import java.time.ZoneOffset
import java.util.Calendar
import com.example.luhikawa.ui.HomeComponents.*


class MainActivityH : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDarka
                ) {
                    val navController = rememberNavController()
                    RegistroScreen(navController = navController)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordatorioScreen(navController: NavController, taskId: String? = null) {
    var reminderName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(0) }
    var selectedImportance by remember { mutableStateOf("Alta") }

    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf("15 Oct 2026") }
    var selectedTime by remember { mutableStateOf("16:00") }

    var selectedCategory by remember { mutableStateOf("Trabajo") }
    var selectedFrequency by remember { mutableStateOf("Todos los días") }

    val repository = remember { TaskRepository() }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(taskId) {
        if (!taskId.isNullOrEmpty()) {
            repository.getTaskById(taskId) { documentData: Map<String, Any>? ->
                if (documentData != null) {
                    reminderName = documentData["title"] as? String ?: ""
                    selectedCategory = documentData["category"] as? String ?: "Trabajo"
                    selectedDate = documentData["date"] as? String ?: "15 Oct 2026"
                    selectedTime = documentData["time"] as? String ?: "16:00"
                    selectedImportance = documentData["importance"] as? String ?: "Alta"
                    selectedIcon = (documentData["icon"] as? Long ?: 0L).toInt()

                    if (documentData.containsKey("frequency")) {
                        selectedFrequency = documentData["frequency"] as? String ?: "Todos los días"
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BgDarka)) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HeaderSection()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    NuevoRecordatorioHeader(isEditing = !taskId.isNullOrEmpty())

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Detalles",
                        style = TextStyle(
                            fontFamily = InriaSerif,
                            fontSize = 26.sp,
                            color = TextBeigea
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    InputLabel(text = "Nombre del recordatorio")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextFieldCustom(
                        value = reminderName,
                        onValueChange = { reminderName = it },
                        placeholder = "Ej: Cita con el dentista"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputLabel(text = "Ícono")
                    Spacer(modifier = Modifier.height(6.dp))
                    IconSelector(
                        selectedIndex = selectedIcon,
                        onIconSelected = { selectedIcon = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputLabel(text = "Importancia")
                    Spacer(modifier = Modifier.height(6.dp))
                    ImportanceSelector(
                        selectedOption = selectedImportance,
                        onOptionSelected = { selectedImportance = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputLabel(text = "Fecha y Hora")
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showDatePicker = true }
                        ) {
                            DateTimeSelector(
                                icon = Icons.Outlined.CalendarMonth,
                                text = selectedDate,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showTimePicker = true }
                        ) {
                            DateTimeSelector(
                                icon = Icons.Default.Schedule,
                                text = selectedTime,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    InputLabel(text = "Clasificación")
                    Spacer(modifier = Modifier.height(6.dp))
                    IndexStyleCategorySelector(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )

                    if (selectedCategory == "Hábitos") {
                        Spacer(modifier = Modifier.height(16.dp))
                        InputLabel(text = "Frecuencia de repetición")
                        Spacer(modifier = Modifier.height(6.dp))
                        FrequencySelector(
                            selectedOption = selectedFrequency,
                            onOptionSelected = { selectedFrequency = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                Button(
                    onClick = {
                        if (reminderName.isNotBlank()) {
                            val dateParts = selectedDate.split(" ")
                            val monthMap = mapOf(
                                "Ene" to 1, "Feb" to 2, "Mar" to 3, "Abr" to 4, "May" to 5, "Jun" to 6,
                                "Jul" to 7, "Ago" to 8, "Sep" to 9, "Oct" to 10, "Nov" to 11, "Dic" to 12
                            )
                            val day = dateParts[0].toInt()
                            val month = monthMap[dateParts[1]] ?: 1
                            val year = dateParts[2].toInt()
                            val fechaIso = "%04d-%02d-%02d".format(year, month, day)

                            val taskMap = mutableMapOf<String, Any>(
                                "title" to reminderName,
                                "category" to if (selectedCategory == "Todo") "Tareas" else selectedCategory,
                                "date" to selectedDate,
                                "dueDate" to fechaIso,
                                "time" to selectedTime,
                                "importance" to selectedImportance,
                                "icon" to selectedIcon,
                                "completed" to false
                            )

                            if (selectedCategory == "Hábitos") {
                                taskMap["frequency"] = selectedFrequency
                            }

                            val onSuccessAction = {
                                Toast.makeText(
                                    context,
                                    if (!taskId.isNullOrEmpty()) "¡Actualizado con éxito!" else "¡Guardado con éxito!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                                Unit
                            }

                            val onFailureAction: (Exception) -> Unit = { e ->
                                Toast.makeText(
                                    context,
                                    "Error: ${e.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            if (!taskId.isNullOrEmpty()) {
                                repository.updateTask(taskId, taskMap, onSuccessAction, onFailureAction)
                            } else {
                                repository.saveTask(taskMap, onSuccessAction, onFailureAction)
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Por favor escribe un nombre",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BgBeigea,
                        contentColor = TextDarka
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (!taskId.isNullOrEmpty()) "ACTUALIZAR" else "GUARDAR",
                        style = TextStyle(
                            fontFamily = InriaSerif,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
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
                                val fecha = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()
                                val year = fecha.year
                                val month = fecha.monthValue
                                val day = fecha.dayOfMonth
                                val months = arrayOf(
                                    "Ene", "Feb", "Mar", "Abr", "May", "Jun",
                                    "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"
                                )
                                selectedDate = "$day ${months[month - 1]} $year"
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("Aceptar", fontFamily = InriaSerif, color = BgBeigea, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar", fontFamily = InriaSerif, color = TextBeigea.copy(alpha = 0.6f))
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = BgDarka
                )
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = BgDarka,
                        titleContentColor = TextBeigea,
                        headlineContentColor = BgBeigea,
                        weekdayContentColor = TextBeigea.copy(alpha = 0.6f),
                        subheadContentColor = TextBeigea,
                        yearContentColor = TextBeigea,
                        currentYearContentColor = BgBeigea,
                        selectedYearContentColor = BgDarka,
                        selectedYearContainerColor = BgBeigea,
                        dayContentColor = TextBeigea,
                        disabledDayContentColor = TextBeigea.copy(alpha = 0.2f),
                        selectedDayContentColor = BgDarka,
                        selectedDayContainerColor = BgBeigea,
                        todayContentColor = BgBeigea,
                        todayDateBorderColor = BgBeigea,
                        navigationContentColor = BgBeigea
                    )
                )
            }
        }

        if (showTimePicker) {
            val calendar = Calendar.getInstance()

            DisposableEffect(Unit) {
                val timePickerDialog = TimePickerDialog(
                    context,
                    R.style.CustomTimePickerTheme,
                    { _, hourOfDay, minute ->
                        selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                        showTimePicker = false
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )

                timePickerDialog.setOnCancelListener {
                    showTimePicker = false
                }

                timePickerDialog.show()

                onDispose {
                    timePickerDialog.dismiss()
                }
            }
        }
    }
}


