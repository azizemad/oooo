package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class CyberThemeMode(
    val title: String,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color
) {
    CYBER_CYAN("Cyber Cyan", NeonCyan, NeonBlue, AmoledBlack, GlassSurface),
    CYBER_GREEN("Matrix Green", NeonGreen, NeonCyan, Color(0xFF021208), Color(0xCC052410)),
    CYBER_PURPLE("Cyber Purple", NeonPurple, NeonCyan, Color(0xFF09021A), Color(0xCC180838)),
    CYBER_RED("Neon Red", NeonRed, NeonYellow, Color(0xFF140208), Color(0xCC290510)),
    AMOLED_PURE("AMOLED Pure", Color.White, NeonCyan, Color.Black, Color(0xFF101010))
}

@Composable
fun CyberDialerTheme(
    themeMode: CyberThemeMode = CyberThemeMode.CYBER_CYAN,
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = themeMode.primary,
        secondary = themeMode.secondary,
        tertiary = NeonYellow,
        background = themeMode.background,
        surface = themeMode.surface,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = TextPrimary,
        onSurface = TextPrimary
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
