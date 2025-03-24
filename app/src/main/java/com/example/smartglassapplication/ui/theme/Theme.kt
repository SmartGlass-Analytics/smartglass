package com.example.smartglassapplication.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Alabama Crimson and White
val Crimson = Color(0xFF9E1B32)
val White = Color(0xFFFFFFFF)
val DarkBackground = Color(0xFF121212)

private val AlabamaColorScheme = darkColorScheme(
    primary = Crimson,
    onPrimary = White,
    secondary = Crimson,
    onSecondary = White,
    background = DarkBackground,
    onBackground = White,
    surface = Color(0xFF1E1E1E),
    onSurface = White
)

@Composable
fun SmartglassApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AlabamaColorScheme,
        typography = Typography,
        content = content
    )
}
