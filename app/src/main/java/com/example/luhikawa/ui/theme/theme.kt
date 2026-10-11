package com.example.luhikawa.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppTheme(val displayName: String) {
    BEIGE("Beige (Predeterminado)"),
    DORADO("Dorado / Latón"),
    MARRON("Marrón / Café"),
    BLANCO("Blanco Roto / Marfil"),
    VERDE_ESMERALDA("Verde Esmeralda"),
    VERDE_AMARILLITO("Verde Amarillito"),
    AZUL_MARINO("Azul Marino / Noche"),
    VINO_TINTO("Vino Tinto / Burdeos"),
    GRIS_TITANIO("Gris Perla / Titanio"),
    GRIS_HIELO("Gris Hielo / Glacial"),
    PLOMO_CLARO("Plomo Claro / Aluminium"),
    BLANCO_HUESO("Blanco Hueso / Crema Claro"),
    CHAMPANA_CLARO("Champaña Claro"),
    GRIS_TAUPE("Gris Taupe / Lino"),

    // ── Coquette (rosados) ──
    COQUETTE_BALLET("Coquette Ballet (Rosa)"),
    COQUETTE_FRESA("Coquette Fresa"),
    COQUETTE_MALVA("Coquette Malva"),

    SYSTEM("Predeterminado del Sistema")
}

// ═════════════ MODO OSCURO ═════════════
// Función helper para construir esquemas homogéneos con el color acento seleccionado
private fun buildColorScheme(accentColor: Color): ColorScheme {
    return darkColorScheme(
        primary = accentColor,
        onPrimary = Color.Black,
        secondary = accentColor,
        tertiary = accentColor,
        background = BackgroundColor, // Mantiene el fondo oscuro base (#1E1E1E)
        onBackground = accentColor,   // Los textos principales adoptan el color del tema
        surface = SurfaceDark,        // Las tarjetas mantienen el contenedor oscuro
        onSurface = accentColor       // El contenido sobre las tarjetas adopta el color acento
    )
}

// ═════════════ MODO CLARO ═════════════
// Genera automáticamente una versión clara a partir del color acento:
// fondo = acento muy aclarado, texto = acento muy oscurecido, primario = acento oscurecido.
private fun buildLightColorScheme(accentColor: Color): ColorScheme {
    // Los acentos que ya son claros (marfil, hielo, champaña...) se oscurecen más
    // para que el texto y los botones tengan contraste sobre el fondo claro.
    val lum = accentColor.luminance()
    val oscurecer = (0.35f + (lum - 0.3f).coerceAtLeast(0f) * 0.6f).coerceAtMost(0.65f)

    val primario = lerp(accentColor, Color.Black, oscurecer)
    val fondo = lerp(accentColor, Color.White, 0.90f)
    val superficie = lerp(accentColor, Color.White, 0.78f)
    val superficieVariante = lerp(accentColor, Color.White, 0.68f)
    val texto = lerp(accentColor, Color.Black, 0.75f)

    return lightColorScheme(
        primary = primario,
        onPrimary = Color.White,
        primaryContainer = lerp(primario, Color.Black, 0.35f),
        onPrimaryContainer = Color.White,
        secondary = primario,
        tertiary = primario,
        background = fondo,
        onBackground = texto,
        surface = superficie,
        onSurface = texto,
        surfaceVariant = superficieVariante,
        onSurfaceVariant = texto,
        outline = primario.copy(alpha = 0.6f)
    )
}

// Esquema claro con colores elegidos a mano (para los temas coquette)
private fun buildCoquetteLightScheme(
    fondo: Color,
    superficie: Color,
    primario: Color,
    texto: Color
): ColorScheme {
    return lightColorScheme(
        primary = primario,
        onPrimary = Color.White,
        primaryContainer = lerp(primario, Color.Black, 0.3f),
        onPrimaryContainer = Color.White,
        secondary = primario,
        tertiary = primario,
        background = fondo,
        onBackground = texto,
        surface = superficie,
        onSurface = texto,
        surfaceVariant = lerp(superficie, primario, 0.15f),
        onSurfaceVariant = texto,
        outline = primario.copy(alpha = 0.6f)
    )
}

// Color acento de cada tema "normal"
private fun acentoDe(appTheme: AppTheme, systemIsDark: Boolean): Color = when (appTheme) {
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
    AppTheme.SYSTEM -> if (systemIsDark) AccentColor32 else AccentBlanco
    else -> AccentColor32
}

@Composable
fun luhikawaTheme(
    appTheme: AppTheme = AppTheme.BEIGE,
    modoClaro: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()

    val colorScheme: ColorScheme = when (appTheme) {
        AppTheme.COQUETTE_BALLET ->
            if (modoClaro) buildCoquetteLightScheme(
                CoquetteBalletFondo, CoquetteBalletSuperficie,
                CoquetteBalletPrimario, CoquetteBalletTexto
            ) else buildColorScheme(AccentCoquetteBalletDark)

        AppTheme.COQUETTE_FRESA ->
            if (modoClaro) buildCoquetteLightScheme(
                CoquetteFresaFondo, CoquetteFresaSuperficie,
                CoquetteFresaPrimario, CoquetteFresaTexto
            ) else buildColorScheme(AccentCoquetteFresaDark)

        AppTheme.COQUETTE_MALVA ->
            if (modoClaro) buildCoquetteLightScheme(
                CoquetteMalvaFondo, CoquetteMalvaSuperficie,
                CoquetteMalvaPrimario, CoquetteMalvaTexto
            ) else buildColorScheme(AccentCoquetteMalvaDark)

        else -> {
            val acento = acentoDe(appTheme, systemIsDark)
            if (modoClaro) buildLightColorScheme(acento) else buildColorScheme(acento)
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            // Fondo claro -> íconos oscuros en la barra de estado
            val fondoClaro = colorScheme.background.luminance() > 0.5f
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = fondoClaro
                isAppearanceLightNavigationBars = fondoClaro
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}