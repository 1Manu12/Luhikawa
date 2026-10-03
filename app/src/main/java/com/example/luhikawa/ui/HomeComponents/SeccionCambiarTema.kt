package com.example.luhikawa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luhikawa.ui.theme.*

@Composable
fun SeccionCambiarTema(
    themeViewModel: ThemeViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    val currentTheme by themeViewModel.selectedTheme.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "Tema de la aplicación",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tema de la aplicación",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = currentTheme.displayName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(getThemePreviewColor(currentTheme))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), CircleShape)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Seleccionar Tema",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Box(modifier = Modifier.heightIn(max = 350.dp)) {
                    LazyColumn {
                        items(AppTheme.entries.toTypedArray()) { theme ->
                            if (theme != AppTheme.SYSTEM) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            themeViewModel.setTheme(theme)
                                            showDialog = false
                                        }
                                        .padding(vertical = 10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(getThemePreviewColor(theme))
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = theme.displayName,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    RadioButton(
                                        selected = (theme == currentTheme),
                                        onClick = {
                                            themeViewModel.setTheme(theme)
                                            showDialog = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

private fun getThemePreviewColor(theme: AppTheme): Color {
    return when (theme) {
        AppTheme.BEIGE -> AccentColor32
        AppTheme.DORADO -> AccentDorado
        AppTheme.MARRON -> AccentMarron
        AppTheme.BLANCO -> AccentBlanco
        AppTheme.VERDE_ESMERALDA -> AccentVerdeEsmeralda
        AppTheme.VERDE_AMARILLITO -> AccentVerdeAmarillito
        AppTheme.AZUL_MARINO -> AccentAzulMarino
        AppTheme.VINO_TINTO -> AccentVinoTinto
        AppTheme.GRIS_TITANIO -> AccentGrisTitanio
        AppTheme.GRIS_HIELO -> AccentGrisHielo
        AppTheme.PLOMO_CLARO -> AccentPlomoClaro
        AppTheme.BLANCO_HUESO -> AccentBlancoHueso
        AppTheme.CHAMPANA_CLARO -> AccentChampanaClaro
        AppTheme.GRIS_TAUPE -> AccentGrisTaupe
        else -> AccentColor32
    }
}