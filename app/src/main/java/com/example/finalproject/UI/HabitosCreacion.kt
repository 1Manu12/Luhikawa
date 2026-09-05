package com.example.finalproject.UI

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.InriaSerif
import com.example.finalproject.R

class MainActivityH : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDark3
                ) {
                    RecordatorioScreen()
                }
            }
        }
    }
}

@Composable
fun RecordatorioScreen() {
    var reminderName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(0) }
    var selectedImportance by remember { mutableStateOf("Alta") }
    var selectedDate by remember { mutableStateOf("15 Oct 2023") }
    var selectedTime by remember { mutableStateOf("16:00") }

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
            NuevoRecordatorioHeader()

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

            InputLabel(text = "Nombre del recordatorio")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextFieldCustom(
                value = reminderName,
                onValueChange = { reminderName = it },
                placeholder = "Ej: Cita con el dentista"
            )

            Spacer(modifier = Modifier.height(24.dp))

            InputLabel(text = "Ícono")
            Spacer(modifier = Modifier.height(8.dp))
            IconSelector(
                selectedIndex = selectedIcon,
                onIconSelected = { selectedIcon = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            InputLabel(text = "Importancia")
            Spacer(modifier = Modifier.height(8.dp))
            ImportanceSelector(
                selectedOption = selectedImportance,
                onOptionSelected = { selectedImportance = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            InputLabel(text = "Fecha y Hora")
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
                    text = "GUARDAR",
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

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(BgBeige3)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.menu_24),
            contentDescription = "Menú",
            tint = TextDark3,
            modifier = Modifier.size(24.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.logolk),
            contentDescription = "Logo LK",
            modifier = Modifier
                .size(70.dp)
                .padding(end = 4.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun NuevoRecordatorioHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgBeige3)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "NUEVO RECORDATORIO",
            style = TextStyle(
                fontFamily = InriaSerif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark3
            )
        )
    }
}

@Composable
fun InputLabel(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = InriaSerif,
            fontSize = 16.sp,
            color = TextBeige3.copy(alpha = 0.8f)
        )
    )
}

@Composable
fun OutlinedTextFieldCustom(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                style = TextStyle(
                    fontFamily = InriaSerif,
                    color = Color.Gray
                )
            )
        },
        textStyle = TextStyle(
            fontFamily = InriaSerif,
            color = TextBeige3,
            fontSize = 18.sp
        ),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBorder3,
            unfocusedBorderColor = AccentBorder3,
            cursorColor = BgBeige3
        ),
        trailingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.edit_24),
                contentDescription = "Editar",
                tint = AccentBorder3,
                modifier = Modifier.size(20.dp)
            )
        }
    )
}

@Composable
fun IconSelector(selectedIndex: Int, onIconSelected: (Int) -> Unit) {
    val iconsList = listOf(
        ImageVector.vectorResource(R.drawable.notifications_24),
        ImageVector.vectorResource(R.drawable.fitness_center_24),
        ImageVector.vectorResource(R.drawable.medication_24),
        ImageVector.vectorResource(R.drawable.water_drop_24),
        ImageVector.vectorResource(R.drawable.directions_car_24)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        iconsList.forEachIndexed { index, icon ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) BgBeige3 else Color.Transparent)
                    .border(
                        BorderStroke(1.dp, AccentBorder3),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onIconSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Icono $index",
                    tint = if (isSelected) BgDark3 else TextBeige3,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun ImportanceSelector(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val options = listOf("Baja", "Media", "Alta")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, AccentBorder3),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        options.forEach { text ->
            val isSelected = text == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) BgBeige3 else Color.Transparent,
                        shape = when (text) {
                            "Baja" -> RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                            "Alta" -> RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                            else -> RoundedCornerShape(0.dp)
                        }
                    )
                    .clickable { onOptionSelected(text) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 18.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) BgDark3 else TextBeige3
                    )
                )
            }
        }
    }
}

@Composable
fun DateTimeSelector(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(50.dp)
            .border(
                BorderStroke(1.dp, AccentBorder3),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentBorder3,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = TextStyle(
                fontFamily = InriaSerif,
                fontSize = 16.sp,
                color = TextBeige3
            )
        )
    }
}

@Composable
fun BottomNavBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDark3)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(icon = ImageVector.vectorResource(R.drawable.calendar_month_24), contentDescription = "Calendario")
        BottomNavItem(icon = ImageVector.vectorResource(R.drawable.cloud_24), contentDescription = "Clima")
        Box(
            modifier = Modifier
                .size(60.dp)
                .border(BorderStroke(1.dp, Color.White), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            )
        }
        BottomNavItem(icon = ImageVector.vectorResource(R.drawable.check_circle_24), contentDescription = "Agenda")
        BottomNavItem(icon = ImageVector.vectorResource(R.drawable.person_24), contentDescription = "Perfil")
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, contentDescription: String) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = Color.White.copy(alpha = 0.7f),
        modifier = Modifier.size(28.dp)
    )
}