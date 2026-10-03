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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.luhikawa.ui.HomeComponents.AppThemeColor

// Enumeración con los temas anteriores y los 5 nuevos
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
    SYSTEM("Predeterminado del Sistema")
}

// 1. Esquema Beige
private val BeigeColorScheme = darkColorScheme(
    primary = AccentColor32,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextBeigea,
    surface = SurfaceDark,
    onSurface = TextBeigea
)

// 2. Esquema Dorado
private val DoradoColorScheme = darkColorScheme(
    primary = AccentDorado,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextDorado,
    surface = SurfaceDorado,
    onSurface = TextDorado
)

// 3. Esquema Marrón
private val MarronColorScheme = darkColorScheme(
    primary = AccentMarron,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextMarron,
    surface = SurfaceMarron,
    onSurface = TextMarron
)

// 4. Esquema Blanco Roto
private val BlancoColorScheme = darkColorScheme(
    primary = AccentBlanco,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextBlanco,
    surface = SurfaceBlanco,
    onSurface = TextBlanco
)

// 5. Esquema Verde Esmeralda
private val VerdeEsmeraldaColorScheme = darkColorScheme(
    primary = AccentVerdeEsmeralda,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextVerdeEsmeralda,
    surface = SurfaceVerdeEsmeralda,
    onSurface = TextVerdeEsmeralda
)

// 6. Esquema Verde Amarillito
private val VerdeAmarillitoColorScheme = darkColorScheme(
    primary = AccentVerdeAmarillito,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextVerdeAmarillito,
    surface = SurfaceVerdeAmarillito,
    onSurface = TextVerdeAmarillito
)

// 7. Esquema Azul Marino
private val AzulMarinoColorScheme = darkColorScheme(
    primary = AccentAzulMarino,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextAzulMarino,
    surface = SurfaceAzulMarino,
    onSurface = TextAzulMarino
)

// 8. Esquema Vino Tinto
private val VinoTintoColorScheme = darkColorScheme(
    primary = AccentVinoTinto,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextVinoTinto,
    surface = SurfaceVinoTinto,
    onSurface = TextVinoTinto
)

// 9. Esquema Gris Titanio
private val GrisTitanioColorScheme = darkColorScheme(
    primary = AccentGrisTitanio,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextGrisTitanio,
    surface = SurfaceGrisTitanio,
    onSurface = TextGrisTitanio
)

// 10. Esquema Gris Hielo
private val GrisHieloColorScheme = darkColorScheme(
    primary = AccentGrisHielo,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextGrisHielo,
    surface = SurfaceGrisHielo,
    onSurface = TextGrisHielo
)

// 11. Esquema Plomo Claro
private val PlomoClaroColorScheme = darkColorScheme(
    primary = AccentPlomoClaro,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextPlomoClaro,
    surface = SurfacePlomoClaro,
    onSurface = TextPlomoClaro
)

// 12. Esquema Blanco Hueso
private val BlancoHuesoColorScheme = darkColorScheme(
    primary = AccentBlancoHueso,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextBlancoHueso,
    surface = SurfaceBlancoHueso,
    onSurface = TextBlancoHueso
)

// 13. Esquema Champaña Claro
private val ChampanaClaroColorScheme = darkColorScheme(
    primary = AccentChampanaClaro,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextChampanaClaro,
    surface = SurfaceChampanaClaro,
    onSurface = TextChampanaClaro
)

// 14. Esquema Gris Taupe
private val GrisTaupeColorScheme = darkColorScheme(
    primary = AccentGrisTaupe,
    onPrimary = TextDarka,
    background = BackgroundColor,
    onBackground = TextGrisTaupe,
    surface = SurfaceGrisTaupe,
    onSurface = TextGrisTaupe
)

@Composable
fun luhikawaTheme(
    appTheme: AppTheme = AppTheme.BEIGE,
    content: @Composable () -> Unit
) {
    val systemIsDark = isSystemInDarkTheme()

    val colorScheme: ColorScheme = when (appTheme) {
        AppTheme.BEIGE -> BeigeColorScheme
        AppTheme.DORADO -> DoradoColorScheme
        AppTheme.MARRON -> MarronColorScheme
        AppTheme.BLANCO -> BlancoColorScheme
        AppTheme.VERDE_ESMERALDA -> VerdeEsmeraldaColorScheme
        AppTheme.VERDE_AMARILLITO -> VerdeAmarillitoColorScheme
        AppTheme.AZUL_MARINO -> AzulMarinoColorScheme
        AppTheme.VINO_TINTO -> VinoTintoColorScheme
        AppTheme.GRIS_TITANIO -> GrisTitanioColorScheme
        AppTheme.GRIS_HIELO -> GrisHieloColorScheme
        AppTheme.PLOMO_CLARO -> PlomoClaroColorScheme
        AppTheme.BLANCO_HUESO -> BlancoHuesoColorScheme
        AppTheme.CHAMPANA_CLARO -> ChampanaClaroColorScheme
        AppTheme.GRIS_TAUPE -> GrisTaupeColorScheme
        AppTheme.SYSTEM -> if (systemIsDark) BeigeColorScheme else BlancoColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

fun String.toColor(): Color {
    val colorInt = android.graphics.Color.parseColor(this.replace("0xFF", "#"))
    return Color(colorInt.toLong() and 0xFFFFFFFFL)
}

@Composable
fun MiAppTheme(
    selectedTheme: AppThemeColor = AppThemeColor.BEIGE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val primaryColor = selectedTheme.hex.toColor()

    val colors = if (darkTheme) {
        darkColorScheme(
            primary = primaryColor,
            background = Color(0xFF1E1E1E),
            surface = Color(0xFF1E1E1E),
            onPrimary = Color.Black
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            background = Color(0xFFF5F5F5),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}