package com.example.luhikawa.ui.HomeComponents

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.luhikawa.R
import com.example.luhikawa.data.AccountManager
import com.example.luhikawa.data.switchNextAccount
import com.example.luhikawa.ui.theme.AccentAzulMarino
import com.example.luhikawa.ui.theme.AccentBlanco
import com.example.luhikawa.ui.theme.AccentDorado
import com.example.luhikawa.ui.theme.AccentGrisTitanio
import com.example.luhikawa.ui.theme.AccentMarron
import com.example.luhikawa.ui.theme.AccentVerdeAmarillito
import com.example.luhikawa.ui.theme.AccentVerdeEsmeralda
import com.example.luhikawa.ui.theme.AccentVinoTinto
import com.example.luhikawa.ui.theme.AppTheme
import com.example.luhikawa.ui.theme.InriaSerif

@Composable
fun RectanguloConImagen() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logolk),
            contentDescription = "Logo LK",
            modifier = Modifier
                .size(85.dp)
                .align(Alignment.CenterEnd),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun EtiquetaTexto(
    texto: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected || isPressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
        label = "fondoAnimado"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected || isPressed) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
        label = "textoAnimado"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = textColor,
            fontSize = 14.sp,
            fontFamily = InriaSerif
        )
    }
}

@Composable
fun ImagenDerechaTextoIzquierda() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 35.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.width(180.dp)
        ) {
            Text(
                text = "Tómate tu tiempo.\nEl descanso también es productivo",
                style = TextStyle(
                    fontFamily = InriaSerif,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
            )
        }

        Image(
            painter = painterResource(id = R.drawable.gato),
            contentDescription = "Gato descansando",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun RectanguloCompletadoPapelera(
    textoTarea: String,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(22.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = textoTarea,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontFamily = InriaSerif,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logolk),
            contentDescription = "Logo LK",
            modifier = Modifier
                .size(90.dp)
                .padding(end = 4.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun NuevoRecordatorioHeader(isEditing: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isEditing) "EDITAR RECORDATORIO" else "NUEVO RECORDATORIO",
            style = TextStyle(
                fontFamily = InriaSerif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
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
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
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
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            )
        },
        textStyle = TextStyle(
            fontFamily = InriaSerif,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
        ),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = MaterialTheme.colorScheme.primary,
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
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onIconSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Icono $index",
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(26.dp)
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
                BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        options.forEach { text ->
            val isSelected = text == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = when (text) {
                            "Baja" -> RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                            "Alta" -> RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                            else -> RoundedCornerShape(0.dp)
                        }
                    )
                    .clickable { onOptionSelected(text) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
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
                BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = TextStyle(
                fontFamily = InriaSerif,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

@Composable
fun IndexStyleCategorySelector(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("Trabajo", "Estudio", "Hábitos", "Personal", "Lista de deseos", "Cumpleaños")

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(categories.size) { index ->
            val category = categories[index]
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}

@Composable
fun FrequencySelector(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val options = listOf("Todos los días", "Día de por medio", "Cada dos semanas")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        options.forEach { text ->
            val isSelected = text == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = when (text) {
                            "Todos los días" -> RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                            "Cada dos semanas" -> RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                            else -> RoundedCornerShape(0.dp)
                        }
                    )
                    .clickable { onOptionSelected(text) }
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = TextStyle(
                        fontFamily = InriaSerif,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ParteAbajo(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val context = LocalContext.current
    val backgroundColor = MaterialTheme.colorScheme.background
    val iconColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor) // Mismo tono oscuro sin contraste
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    )
    {
// CALENDARIO
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            BottomNavItem(
                icon = Icons.Default.CalendarMonth,
                contentDescription = "Calendario",
                tint = Color.White,
                onClick = {
                    if (currentRoute != "calendario") {
                        navController.navigate("calendario") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }

// IA
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            BottomNavItem(
                icon = Icons.Default.AutoAwesome,
                contentDescription = "IA",
                tint = Color.White,
                onClick = {
                    if (currentRoute != "ia") {
                        navController.navigate("ia") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
// RECORDATORIO
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
                .border(BorderStroke(1.5.dp, Color.White), CircleShape)
                .clickable {
                    if (currentRoute?.startsWith("recordatorio") != true) {
                        navController.navigate("recordatorio?taskId={taskId}") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Icono o contenido del recordatorio si lo lleva dentro
        }
// HOME / AGENDA
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                contentDescription = "Agenda",
                tint = Color.White,
                onClick = {
                    navController.navigate("greeting") {
                        popUpTo("greeting") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
// PERFIL
        Box(
            modifier = Modifier
                .size(56.dp)
                .pointerInput(navController) {
                    detectTapGestures(
                        onTap = {
                            val route = navController.currentBackStackEntry?.destination?.route
                            if (route != "perfil") {
                                navController.navigate("perfil") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        onDoubleTap = {
                            Log.d("DOUBLE_TAP", "¡Doble toque detectado!")
                            switchNextAccount(
                                context = context,
                                accountManager = AccountManager(context),
                                navController = navController,
                                onSuccess = {
                                    navController.navigate("greeting") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PersonOutline,
                contentDescription = "Perfil",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    contentDescription: String,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier
            .size(26.dp)
            .clickable(onClick = onClick)
    )
}

fun getIconFromIndex(index: Int): ImageVector {
    return when (index) {
        0 -> Icons.Default.Home
        1 -> Icons.Default.Star
        2 -> Icons.Default.Favorite
        3 -> Icons.Default.Person
        4 -> Icons.Default.Notifications
        5 -> Icons.Default.Settings
        else -> Icons.Default.CheckCircle
    }
}

@Composable
fun ThemeSelectorHome(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Selecciona tu tema",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(AppTheme.values()) { theme ->
                if (theme != AppTheme.SYSTEM) {
                    ThemeCircle(
                        theme = theme,
                        isSelected = theme == currentTheme,
                        onClick = { onThemeSelected(theme) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeCircle(
    theme: AppTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val previewColor = when (theme) {
        AppTheme.BEIGE -> Color(0xFFC7AF93)
        AppTheme.DORADO -> AccentDorado
        AppTheme.MARRON -> AccentMarron
        AppTheme.BLANCO -> AccentBlanco
        AppTheme.VERDE_ESMERALDA -> AccentVerdeEsmeralda
        AppTheme.VERDE_AMARILLITO -> AccentVerdeAmarillito
        AppTheme.AZUL_MARINO -> AccentAzulMarino
        AppTheme.VINO_TINTO -> AccentVinoTinto
        AppTheme.GRIS_TITANIO -> AccentGrisTitanio
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(previewColor, CircleShape)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() }
    )
}

sealed class Screen(val route: String) {
    object Greeting : Screen("greeting")
    object Login : Screen("login")
    object Perfil : Screen("perfil")
    object Calendario : Screen("calendario")
    object Ia : Screen("ia")
    object Recordatorio : Screen("recordatorio?taskId={taskId}") {
        fun createRoute(taskId: String? = null) = if (taskId != null) "recordatorio?taskId=$taskId" else "recordatorio"
    }
}

@Composable
fun PantallaConScroll(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp) // Espacio al final para que no tape la barra inferior
                ) {
                    content()
                }
            }
        }
    }
}