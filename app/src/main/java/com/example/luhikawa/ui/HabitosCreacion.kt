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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.luhikawa.ui.theme.InriaSerif
import com.example.luhikawa.data.TaskRepository
import com.example.luhikawa.data.HomeTaskRepository
import com.example.luhikawa.data.MicrofinanzasRepository
import com.example.luhikawa.data.AccountManager
import com.example.luhikawa.R
import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
import java.time.ZoneOffset
import java.util.Calendar
import com.example.luhikawa.ui.HomeComponents.*
import com.example.luhikawa.ui.mode.AppMode
import com.example.luhikawa.ui.mode.ModeViewModel
import com.example.luhikawa.ui.mode.config
import com.example.luhikawa.ui.mode.rememberModeViewModel
import com.example.luhikawa.ui.theme.luhikawaTheme

class MainActivityH : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            luhikawaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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
fun RecordatorioScreen(
    navController: NavController,
    taskId: String? = null,
    modeViewModel: ModeViewModel = rememberModeViewModel()
) {
    @Suppress("NAME_SHADOWING")
    val taskId = taskId?.takeUnless { it.isBlank() || it == "{taskId}" }

    var modoDeLaTarea by remember { mutableStateOf<AppMode?>(null) }
    val modo = modoDeLaTarea ?: modeViewModel.mode
    val config = modo.config()

    var reminderName by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(0) }
    var selectedImportance by remember { mutableStateOf("Alta") }

    val context = LocalContext.current
    val accountManager = remember { AccountManager(context) }

    val activeUserId = FirebaseAuth.getInstance().currentUser?.uid
        ?: accountManager.getCurrentAccountUid()

    var selectedDate by remember { mutableStateOf("15 Oct 2026") }
    var selectedTime by remember { mutableStateOf("16:00") }

    var selectedCategory by remember { mutableStateOf(config.categoriaInicial) }
    var selectedFrequency by remember { mutableStateOf("Todos los días") }

    val taskRepo = remember { TaskRepository() }
    val homeRepo = remember { HomeTaskRepository() }
    val financeRepo = remember { MicrofinanzasRepository() }

    val modoRepo = modeViewModel.mode

    val getTaskDb: (String, (Map<String, Any>?) -> Unit) -> Unit = { id, cb ->
        when (modoRepo) {
            AppMode.TAREAS -> taskRepo.getTaskById(id, cb)
            AppMode.HOGAR -> homeRepo.getTaskById(id, cb)
            AppMode.MICROFINANZAS -> financeRepo.getTaskById(id, cb)
        }
    }

    val saveTaskDb: (Map<String, Any>, () -> Unit, (Exception) -> Unit) -> Unit = { map, ok, fail ->
        when (modoRepo) {
            AppMode.TAREAS -> taskRepo.saveTask(map, context, ok, fail)
            AppMode.HOGAR -> homeRepo.saveTask(map, context, ok, fail)
            AppMode.MICROFINANZAS -> financeRepo.saveTask(map, context, ok, fail)
        }
    }

    val updateTaskDb: (String, Map<String, Any>, () -> Unit, (Exception) -> Unit) -> Unit = { id, map, ok, fail ->
        when (modoRepo) {
            AppMode.TAREAS -> taskRepo.updateTask(id, map, ok, fail)
            AppMode.HOGAR -> homeRepo.updateTask(id, map, ok, fail)
            AppMode.MICROFINANZAS -> financeRepo.updateTask(id, map, ok, fail)
        }
    }

    val deleteTaskDb: (String, () -> Unit) -> Unit = { id, ok ->
        when (modoRepo) {
            AppMode.TAREAS -> taskRepo.deleteTask(id, ok)
            AppMode.HOGAR -> homeRepo.deleteTask(id, ok)
            AppMode.MICROFINANZAS -> financeRepo.deleteTask(id, ok)
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(modo) {
        if (taskId.isNullOrEmpty()) selectedCategory = config.categoriaInicial
    }

    LaunchedEffect(taskId, modoRepo) {
        if (!taskId.isNullOrEmpty()) {
            getTaskDb(taskId) { documentData: Map<String, Any>? ->
                if (documentData != null) {
                    // Las tareas viejas sin "mode" cuentan como "tareas"
                    val modoCargado = AppMode.fromKey(documentData["mode"] as? String)
                    modoDeLaTarea = modoCargado

                    reminderName = documentData["title"] as? String ?: ""
                    selectedCategory = documentData["category"] as? String
                        ?: modoCargado.config().categoriaInicial
                    monto = (documentData["amount"] as? Number)?.toString() ?: ""
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

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            RectanguloConImagen()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                NuevoRecordatorioHeader(
                    isEditing = !taskId.isNullOrEmpty(),
                    tituloNuevo = config.tituloNuevo
                )

                Text(
                    text = "Detalles",
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    InputLabel(text = config.etiquetaNombre)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = reminderName,
                        onValueChange = { reminderName = it },
                        placeholder = {
                            Text(
                                text = config.placeholderNombre,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                fontFamily = InriaSerif
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                if (config.mostrarMonto) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        InputLabel(text = config.etiquetaMonto)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = monto,
                            onValueChange = { monto = it.filter { c -> c.isDigit() || c == '.' } },
                            placeholder = {
                                Text(
                                    text = "0.00",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    fontFamily = InriaSerif
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    InputLabel(text = "Ícono")
                    Spacer(modifier = Modifier.height(6.dp))
                    IconSelector(
                        selectedIndex = selectedIcon,
                        onIconSelected = { selectedIcon = it }
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    InputLabel(text = "Importancia")
                    Spacer(modifier = Modifier.height(6.dp))
                    ImportanceSelector(
                        selectedOption = selectedImportance,
                        onOptionSelected = { selectedImportance = it }
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
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
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    InputLabel(text = config.etiquetaClasificacion)
                    Spacer(modifier = Modifier.height(6.dp))
                    IndexStyleCategorySelector(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        categories = config.categorias
                    )
                }

                if (config.categoriaConFrecuencia != null &&
                    selectedCategory == config.categoriaConFrecuencia
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        InputLabel(text = "Frecuencia de repetición")
                        Spacer(modifier = Modifier.height(6.dp))
                        FrequencySelector(
                            selectedOption = selectedFrequency,
                            onOptionSelected = { selectedFrequency = it }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (reminderName.isBlank()) {
                            Toast.makeText(
                                context,
                                "Por favor escribe un nombre",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        if (activeUserId.isNullOrEmpty()) {
                            Toast.makeText(
                                context,
                                "Error: Users no autenticado",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        val montoValor = monto.toDoubleOrNull()
                        if (config.mostrarMonto && montoValor == null) {
                            Toast.makeText(
                                context,
                                "Escribe un monto válido",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        val dateParts = selectedDate.split(" ")
                        val monthMap = mapOf(
                            "Ene" to 1, "Feb" to 2, "Mar" to 3, "Abr" to 4, "May" to 5, "Jun" to 6,
                            "Jul" to 7, "Ago" to 8, "Sep" to 9, "Oct" to 10, "Nov" to 11, "Dic" to 12
                        )
                        val day = dateParts.getOrNull(0)?.toIntOrNull() ?: 15
                        val month = monthMap[dateParts.getOrNull(1)] ?: 10
                        val year = dateParts.getOrNull(2)?.toIntOrNull() ?: 2026
                        val fechaIso = "%04d-%02d-%02d".format(year, month, day)

                        val taskMap = mutableMapOf<String, Any>(
                            "userId" to activeUserId,
                            "title" to reminderName,
                            "category" to selectedCategory,
                            "date" to selectedDate,
                            "dueDate" to fechaIso,
                            "time" to selectedTime,
                            "importance" to selectedImportance,
                            "icon" to selectedIcon,
                            "completed" to false
                        )

                        // El modo solo se escribe al CREAR; editar no lo cambia
                        if (taskId.isNullOrEmpty()) taskMap["mode"] = modo.key

                        if (config.mostrarMonto && montoValor != null) {
                            taskMap["amount"] = montoValor
                        }

                        if (config.categoriaConFrecuencia != null &&
                            selectedCategory == config.categoriaConFrecuencia
                        ) {
                            taskMap["frequency"] = selectedFrequency
                        }

                        val onSuccessAction: () -> Unit = {
                            Toast.makeText(
                                context,
                                if (!taskId.isNullOrEmpty()) "¡Actualizado con éxito!" else config.toastGuardado,
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
                            updateTaskDb(taskId, taskMap, onSuccessAction, onFailureAction)
                        } else {
                            saveTaskDb(taskMap, onSuccessAction, onFailureAction)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (!taskId.isNullOrEmpty()) "ACTUALIZAR" else config.textoGuardar,
                        style = TextStyle(
                            fontFamily = InriaSerif,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }

                // Opción para eliminar tarea (solo si se está editando)
                if (!taskId.isNullOrEmpty()) {
                    OutlinedButton(
                        onClick = {
                            deleteTaskDb(taskId) {
                                Toast.makeText(context, "Tarea eliminada", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Eliminar tarea",
                            style = TextStyle(
                                fontFamily = InriaSerif,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
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
                        Text(
                            "Aceptar",
                            fontFamily = InriaSerif,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(
                            "Cancelar",
                            fontFamily = InriaSerif,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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