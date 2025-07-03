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


private val LightColorScheme = lightColorScheme(

    primary = Color(0xFF2E7D32),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA8F5A8),
    onPrimaryContainer = Color(0xFF0A4E0A),

    secondary = Color(0xFFFFC107),
    onSecondary = Color(0xFF2E2E2E),
    secondaryContainer = Color(0xFFFFF8E1),
    onSecondaryContainer = Color(0xFF5D4037),

    tertiary = Color(0xFF8BC34A),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE8F5E8),
    onTertiaryContainer = Color(0xFF2E4A2E),

    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFF5D1A1A),

    background = Color(0xFFFFFDF7),
    onBackground = Color(0xFF1C1B1A),

    surface = Color(0xFFFFFDF7),
    onSurface = Color(0xFF1C1B1A),
    surfaceVariant = Color(0xFFF5F5F0),
    onSurfaceVariant = Color(0xFF4A4A47),

    outline = Color(0xFF7A7A77),
    outlineVariant = Color(0xFFCACAC7),

    surfaceTint = Color(0xFF2E7D32),
    inverseSurface = Color(0xFF313030),
    inverseOnSurface = Color(0xFFF4F4F1),
    inversePrimary = Color(0xFF7CB342),
    surfaceDim = Color(0xFFDEDDD8),
    surfaceBright = Color(0xFFFFFDF7),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8F7F2),
    surfaceContainer = Color(0xFFF2F1EC),
    surfaceContainerHigh = Color(0xFFECECE6),
    surfaceContainerHighest = Color(0xFFE6E6E1)
)


private val DarkColorScheme = darkColorScheme(

    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF0A4E0A),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFA8F5A8),

    secondary = Color(0xFFFFD54F),
    onSecondary = Color(0xFF3E2723),
    secondaryContainer = Color(0xFF6D4C41),
    onSecondaryContainer = Color(0xFFFFF8E1),

    tertiary = Color(0xFFAED581),
    onTertiary = Color(0xFF2E4A2E),
    tertiaryContainer = Color(0xFF4A6741),
    onTertiaryContainer = Color(0xFFE8F5E8),

    error = Color(0xFFEF5350),
    onError = Color(0xFF5D1A1A),
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color(0xFFFFEBEE),

    background = Color(0xFF1A1A17),
    onBackground = Color(0xFFE6E6E1),

    surface = Color(0xFF1A1A17),
    onSurface = Color(0xFFE6E6E1),
    surfaceVariant = Color(0xFF4A4A47),
    onSurfaceVariant = Color(0xFFCACAC7),

    outline = Color(0xFF949491),
    outlineVariant = Color(0xFF4A4A47),

    surfaceTint = Color(0xFF81C784),
    inverseSurface = Color(0xFFE6E6E1),
    inverseOnSurface = Color(0xFF313030),
    inversePrimary = Color(0xFF2E7D32),
    surfaceDim = Color(0xFF1A1A17),
    surfaceBright = Color(0xFF403F3D),
    surfaceContainerLowest = Color(0xFF0F0F0C),
    surfaceContainerLow = Color(0xFF1C1B1A),
    surfaceContainer = Color(0xFF201F1E),
    surfaceContainerHigh = Color(0xFF2B2A28),
    surfaceContainerHighest = Color(0xFF363533)
)


object JewelryColors {

    val Gold = Color(0xFFFFD700)
    val Silver = Color(0xFFC0C0C0)
    val Platinum = Color(0xFFE5E4E2)
    val RoseGold = Color(0xFFE8B4A0)


    val Diamond = Color(0xFFFAFAFA)
    val Ruby = Color(0xFFE0115F)
    val Sapphire = Color(0xFF0F52BA)
    val Emerald = Color(0xFF50C878)
    val Topaz = Color(0xFFFFCC00)


    val GoldGradient = listOf(
        Color(0xFFFFD700),
        Color(0xFFFFA500)
    )

    val EmeraldGradient = listOf(
        Color(0xFF2E7D32),
        Color(0xFF4CAF50)
    )
}

@Composable
fun JewelryWorkshopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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