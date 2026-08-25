package com.dailypoem.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    primaryContainer = AccentLight,
    onPrimaryContainer = Ink,
    secondary = Ink,
    onSecondary = Color.White,
    background = Beige,
    onBackground = Ink,
    surface = CardBeige,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEDE6D8),
    onSurfaceVariant = InkMuted,
    outline = Divider
)

@Composable
fun DailyPoemTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
