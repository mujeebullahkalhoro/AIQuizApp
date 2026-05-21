package com.example.quizapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandPurpleLight,
    onPrimary = White,
    primaryContainer = BrandPurpleDark,
    onPrimaryContainer = White,
    secondary = AccentTeal,
    onSecondary = Black,
    secondaryContainer = Color(0xFF003F47),
    onSecondaryContainer = AccentTeal,
    tertiary = AccentOrange,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = ErrorRed,
    onError = White,
    outline = Color(0xFF6B6B8A)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandPurple,
    onPrimary = White,
    primaryContainer = Color(0xFFEDE0FF),
    onPrimaryContainer = BrandPurpleDark,
    secondary = AccentTeal,
    onSecondary = White,
    secondaryContainer = Color(0xFFB2EBF2),
    onSecondaryContainer = Color(0xFF003F47),
    tertiary = AccentOrange,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = ErrorRed,
    onError = White,
    outline = Color(0xFFB0B0CC)
)

@Composable
fun QuizAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
