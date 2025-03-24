package com.example.smartglassapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AlabamaWhite = Color(0xFFFFFFFF)
private val OnCrimson = Color(0xFFFFFFFF)
private val OnWhite = Color(0xFF000000)

private val LightColorScheme = lightColorScheme(
    primary = Crimson,
    onPrimary = OnCrimson,
    secondary = AlabamaWhite,
    onSecondary = OnWhite,
    background = AlabamaWhite,
    onBackground = OnWhite,
    surface = AlabamaWhite,
    onSurface = OnWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = Crimson,
    onPrimary = OnCrimson,
    secondary = AlabamaWhite,
    onSecondary = OnWhite,
    background = Color(0xFF121212),
    onBackground = AlabamaWhite,
    surface = Color(0xFF1E1E1E),
    onSurface = AlabamaWhite
)

@Composable
fun SmartGlassApplicationTheme(
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
