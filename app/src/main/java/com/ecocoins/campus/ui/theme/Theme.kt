package com.ecocoins.campus.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = EcoGreen,
    onPrimary = Color.White,
    primaryContainer = EcoGreenLight,
    onPrimaryContainer = EcoGreenDark,

    secondary = EcoTeal,
    onSecondary = Color.White,
    secondaryContainer = EcoTealLight,
    onSecondaryContainer = EcoTealDark,

    tertiary = EcoAmber,
    onTertiary = Color.White,
    tertiaryContainer = EcoAmberLight,
    onTertiaryContainer = EcoAmberDark,

    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1A1C1E),

    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = EcoGrayLight,
    onSurfaceVariant = EcoGray,

    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),

    scrim = Color(0xFF000000),
)

private val DarkColorScheme = darkColorScheme(
    primary = EcoGreenLight,
    onPrimary = EcoGreenDark,
    primaryContainer = EcoGreen,
    onPrimaryContainer = EcoGreenLight,

    secondary = EcoTealLight,
    onSecondary = EcoTealDark,
    secondaryContainer = EcoTeal,
    onSecondaryContainer = EcoTealLight,

    tertiary = EcoAmberLight,
    onTertiary = EcoAmberDark,
    tertiaryContainer = EcoAmber,
    onTertiaryContainer = EcoAmberLight,

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),

    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC3C7CF),

    outline = Color(0xFF8D9199),
    outlineVariant = Color(0xFF43474E),

    scrim = Color(0xFF000000),
)

@Composable
fun EcoCoinsCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
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