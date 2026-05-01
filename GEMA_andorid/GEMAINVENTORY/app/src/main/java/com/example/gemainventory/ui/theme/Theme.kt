package com.example.gemainventory.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA), // Azul claro para modo noche
    secondary = Color(0xFF93C5FD),
    tertiary = Color(0xFFA5F3FC),
    background = Color(0xFF0F172A), // Fondo azul oscuro profundo
    surface = Color(0xFF1E293B),
    onPrimary = Color(0xFF0D2558),
    onSecondary = Color(0xFF0D2558),
    onTertiary = Color(0xFF0D2558),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    primaryContainer = Color(0xFF1E40AF),
    onPrimaryContainer = Color(0xFFDBEAFE)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0D2558), // Azul GEMA oficial
    secondary = Color(0xFF1E3A8A),
    tertiary = Color(0xFF3B82F6),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0D2558),
    onSurface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF0D2558)
)

@Composable
fun GemaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
