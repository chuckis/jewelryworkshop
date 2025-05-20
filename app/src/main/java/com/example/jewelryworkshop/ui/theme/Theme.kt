package com.example.jewelryworkshop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Основные цвета светлой темы
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF625B71),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8DEF8),
    onPrimaryContainer = Color(0xFF1D192B),
    secondary = Color(0xFF7D5260),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD8E4),
    onSecondaryContainer = Color(0xFF31111D),
    tertiary = Color(0xFF4A6690),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD3E3FF),
    onTertiaryContainer = Color(0xFF001C3A),
    error = Color(0xFFB3261E),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E)
)

// Основные цвета темной темы
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFFFB0C8),
    onSecondary = Color(0xFF492532),
    secondaryContainer = Color(0xFF633B48),
    onSecondaryContainer = Color(0xFFFFD8E4),
    tertiary = Color(0xFFB1C5FF),
    onTertiary = Color(0xFF002962),
    tertiaryContainer = Color(0xFF324679),
    onTertiaryContainer = Color(0xFFD3E3FF),
    error = Color(0xFFF2B8B5),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99)
)

/**
 * Тема приложения ювелирной мастерской
 */
@Composable
fun JewelryWorkshopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Динамические цвета доступны только на Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}