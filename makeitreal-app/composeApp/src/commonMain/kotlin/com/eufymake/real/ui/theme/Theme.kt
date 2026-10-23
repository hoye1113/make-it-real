package com.eufymake.real.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// UV 6-channel inks
val InkCyan = Color(0xFF00A0E9)
val InkMagenta = Color(0xFFE4007F)
val InkYellow = Color(0xFFFFD100)
val InkBlack = Color(0xFF1D2024)
val InkWhite = Color(0xFFE2E8F0)
val InkGloss = Color(0xFF2DD4BF)

// Official AnkerMake / eufyMake Brand Design Tokens
val BrandGreen = Color(0xFF00C37A)        // Official active green accent
val BrandBlack = Color(0xFF1A1A1A)        // Official dark capsule CTA button
val BrandDarkCard = Color(0xFF2C2C2E)     // 3D Craft Mode studio backdrop
val BrandOrange = Color(0xFFFF7A00)       // Attention / badges
val BrandGold = Color(0xFFF59E0B)

val SurfaceLight = Color(0xFFFFFFFF)
val BackgroundLight = Color(0xFFF7F8FA)    // Soft clean workspace neutral
val BorderLight = Color(0xFFEEEEEE)
val BorderSubtle = Color(0xFFE5E7EB)
val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF8E8E93)
val TextMuted = Color(0xFFB0B0B5)

val BrandPrimary = BrandBlack
val BrandAccent = BrandGreen

private val LightColorScheme = lightColorScheme(
    primary = BrandGreen,
    onPrimary = Color.White,
    secondary = BrandBlack,
    onSecondary = Color.White,
    background = BackgroundLight,
    surface = SurfaceLight,
    onSurface = TextPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandGreen,
    onPrimary = Color.White,
    secondary = Color.White,
    onSecondary = BrandBlack,
    background = Color(0xFF18181A),
    surface = Color(0xFF242426),
    onSurface = Color(0xFFF8FAFC)
)

@Composable
fun MakeItRealTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
