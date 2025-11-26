package com.ecocoins.campus.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = EcoGreenPrimary,
    onPrimary = BackgroundWhite,
    primaryContainer = EcoGreenLight,
    onPrimaryContainer = EcoGreenDark,

    secondary = EcoOrange,
    onSecondary = BackgroundWhite,
    secondaryContainer = EcoOrangeLight,
    onSecondaryContainer = TextPrimary,

    tertiary = PlasticBlue,
    onTertiary = BackgroundWhite,

    background = BackgroundLight,
    onBackground = TextPrimary,

    surface = BackgroundWhite,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextSecondary,

    error = StatusRejected,
    onError = BackgroundWhite,

    outline = DividerGray
)

private val DarkColorScheme = darkColorScheme(
    primary = EcoGreenPrimary,
    onPrimary = BackgroundWhite,
    primaryContainer = EcoGreenDark,
    onPrimaryContainer = EcoGreenLight,

    secondary = EcoOrange,
    onSecondary = DarkBackground,
    secondaryContainer = EcoOrangeLight,
    onSecondaryContainer = TextPrimary,

    background = DarkBackground,
    onBackground = BackgroundWhite,

    surface = DarkSurface,
    onSurface = BackgroundWhite,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = TextSecondary,

    error = StatusRejected,
    onError = BackgroundWhite
)

@Composable
fun EcoCoinsCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}