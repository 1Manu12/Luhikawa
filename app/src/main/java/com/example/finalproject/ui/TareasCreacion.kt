package com.example.finalproject.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivityTarea : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDarka
                ) {
                    TareaScreen()
                }
            }
        }
    }
}

@Composable
fun TareaScreen() {
    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var selectedImportance by remember { mutableStateOf("Media") }
    var selectedDate by remember { mutableStateOf("15 Oct 2023") }
    var selectedTime by remember { mutableStateOf("18:00") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDarka)
    ) {
        HeaderSection()

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BgBeigea)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NUEVA TAREA",
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarka
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Detalles",
                style = TextStyle(
                    fontFamily = InriaSerif,
                    fontSize = 26.sp,
                    color = TextBeigea
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputLabel(text = "Nombre de la tarea")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextFieldCustom(
                value = taskName,
                onValueChange = { taskName = it },
                placeholder = "Ej: Proyecto de matemáticas"
            )

            Spacer(modifier = Modifier.height(20.dp))

            InputLabel(text = "Descripción")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextFieldCustom(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                placeholder = "Ej: Investigar fuentes y redactar..."
            )

            Spacer(modifier = Modifier.height(20.dp))

            InputLabel(text = "Prioridad")
            Spacer(modifier = Modifier.height(8.dp))
            ImportanceSelector(
                selectedOption = selectedImportance,
                onOptionSelected = { selectedImportance = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            InputLabel(text = "Fecha y Hora límite")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DateTimeSelector(
                    icon = Icons.Outlined.CalendarMonth,
                    text = selectedDate,
                    modifier = Modifier.weight(1f)
                )
                DateTimeSelector(
                    icon = Icons.Default.Schedule,
                    text = selectedTime,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { },
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
                    text = "GUARDAR TAREA",
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        BottomNavBar()
    }
}