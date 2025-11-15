package com.miempresa.ecocoinscampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores inspirados en sostenibilidad
private val EcoGreen = Color(0xFF2E7D32)
private val EcoLightGreen = Color(0xFF66BB6A)
private val EcoEmerald = Color(0xFF00897B)
private val EcoAmber = Color(0xFFFFA726)
private val EcoBrown = Color(0xFF5D4037)

private val LightColorScheme = lightColorScheme(
    primary = EcoGreen,
    secondary = EcoEmerald,
    tertiary = EcoAmber,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    primaryContainer = EcoLightGreen,
    secondaryContainer = Color(0xFFB2DFDB)
)

private val DarkColorScheme = darkColorScheme(
    primary = EcoLightGreen,
    secondary = Color(0xFF80CBC4),
    tertiary = EcoAmber,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF2D2D2D),
    onPrimary = Color(0xFF003300),
    onSecondary = Color(0xFF003737),
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5)
)

@Composable
fun EcoCoinsCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}