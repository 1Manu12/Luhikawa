package com.example.luhikawa

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val BgDark = Color(0xFF1A1717)
val BgBeige = Color(0xFFC7AF93)
val TextBeige = Color(0xFFC7AF93)
val TextDark = Color(0xFF1A1717)
val AccentBorder = Color(0xFFC7AF93)

val CustomFontFamily = FontFamily.Serif

class MainActivityH : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDarka
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
            .background(BgDarka)
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
                    color = TextBeigea
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
            .background(BgBeigea)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menú",
            tint = TextDarka,
            modifier = Modifier.size(24.dp)
        )

        // Logo LK integrado en el encabezado
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
            .background(BgBeigea)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "NUEVO RECORDATORIO",
            style = TextStyle(
                fontFamily = InriaSerif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarka
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
            color = TextBeigea.copy(alpha = 0.8f)
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
            color = TextBeigea,
            fontSize = 18.sp
        ),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBordera,
            unfocusedBorderColor = AccentBordera,
            cursorColor = BgBeigea
        ),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = AccentBordera,
                modifier = Modifier.size(20.dp)
            )
        }
    )
}

@Composable
fun IconSelector(selectedIndex: Int, onIconSelected: (Int) -> Unit) {
    val iconsList = listOf(
        Icons.Outlined.Notifications,
        Icons.Default.FitnessCenter,
        Icons.Default.Medication,
        Icons.Default.WaterDrop,
        Icons.Default.DirectionsCar
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
                    .background(if (isSelected) BgBeigea else Color.Transparent)
                    .border(
                        BorderStroke(1.dp, AccentBordera),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onIconSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Icono $index",
                    tint = if (isSelected) BgDarka else TextBeigea,
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
                BorderStroke(1.dp, AccentBordera),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        options.forEach { text ->
            val isSelected = text == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) BgBeigea else Color.Transparent,
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
                        color = if (isSelected) BgDarka else TextBeigea
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
                BorderStroke(1.dp, AccentBordera),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentBordera,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = TextStyle(
                fontFamily = InriaSerif,
                fontSize = 16.sp,
                color = TextBeigea
            )
        )
    }
}

@Composable
fun BottomNavBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDarka)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(icon = Icons.Default.CalendarMonth, contentDescription = "Calendario")
        BottomNavItem(icon = Icons.Outlined.Cloud, contentDescription = "Clima")
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
        BottomNavItem(icon = Icons.Default.Schedule, contentDescription = "Agenda")
        BottomNavItem(icon = Icons.Outlined.PersonOutline, contentDescription = "Perfil")
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