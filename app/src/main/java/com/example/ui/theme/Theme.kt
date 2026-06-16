package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JarvisColorScheme = darkColorScheme(
    primary = StarkCyberBlue,
    secondary = StarkBrightCyan,
    tertiary = StarkHoloTeal,
    background = StarkDeepBlack,
    surface = StarkSlateDark,
    onPrimary = TextPrimaryWhite,
    onBackground = TextPrimaryWhite,
    onSurface = TextPrimaryWhite,
    error = StarkBurnOrange
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JarvisColorScheme,
        typography = Typography,
        content = content
    )
}
