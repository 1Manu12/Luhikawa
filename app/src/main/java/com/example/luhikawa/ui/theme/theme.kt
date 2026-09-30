package com.example.luhikawa.ui.theme

import android.app.Activity
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentColor32,
    onPrimary = BackgroundColor,
    background = BackgroundColor,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    secondary = AccentColor32,
    onSecondary = Color.Black
)

@Composable
fun luhikawaTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundColor.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

}