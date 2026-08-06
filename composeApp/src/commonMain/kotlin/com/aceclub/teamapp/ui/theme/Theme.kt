package com.aceclub.teamapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val AceLightColors = lightColorScheme(
    primary = AceColors.court,
    onPrimary = Color(0xFFFFFFFF),
    secondary = AceColors.volley,
    onSecondary = Color(0xFFFFFFFF),
    background = AceColors.bg,
    onBackground = AceColors.ink,
    surface = AceColors.surface,
    onSurface = AceColors.ink,
    error = AceColors.danger
)

private val AceTypography = Typography(
    titleLarge = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.3).sp),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.ExtraBold),
    bodyLarge = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    labelSmall = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)
)

@Composable
fun AceVolleyballTheme(content: @Composable () -> Unit) {
    // Light theme only for now — the club look is a deliberate warm/navy
    // palette rather than something that should invert to dark mode.
    MaterialTheme(
        colorScheme = AceLightColors,
        typography = AceTypography,
        content = content
    )
}
