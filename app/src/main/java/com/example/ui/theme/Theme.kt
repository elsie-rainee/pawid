package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PawDarkColorScheme = darkColorScheme(
    primary = PawAmberPrimary,
    onPrimary = PawOnPrimary,
    primaryContainer = PawAmberContainer,
    onPrimaryContainer = PawAmberOnContainer,
    secondary = PawAmberLight,
    onSecondary = PawOnPrimary,
    tertiary = PawOrangeAccent,
    background = PawBackground,
    onBackground = PawTextPrimary,
    surface = PawSurface,
    onSurface = PawTextPrimary,
    surfaceVariant = PawSurfaceCard,
    onSurfaceVariant = PawTextSecondary,
    outline = PawSurfaceBorder,
    error = PawErrorRed
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PawDarkColorScheme,
        typography = Typography,
        content = content
    )
}
