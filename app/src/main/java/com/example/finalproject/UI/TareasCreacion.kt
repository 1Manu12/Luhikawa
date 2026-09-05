package com.example.finalproject.UI

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.InriaSerif
import com.example.finalproject.R

class MainActivityTarea : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDark3
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
            .background(BgDark3)
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
                    .background(BgBeige3)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NUEVA TAREA",
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark3
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Detalles",
                style = TextStyle(
                    fontFamily = InriaSerif,
                    fontSize = 26.sp,
                    color = TextBeige3
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
                    icon = ImageVector.vectorResource(R.drawable.calendar_month_24),
                    text = selectedDate,
                    modifier = Modifier.weight(1f)
                )
                DateTimeSelector(
                    icon = ImageVector.vectorResource(R.drawable.schedule_24),
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
                    containerColor = BgBeige3,
                    contentColor = TextDark3
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