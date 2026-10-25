package com.eufymake.real.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// App Design System Tokens
object AppDimens {
    val sectionGap = 12.dp
    val cardPadding = 16.dp
    val touchTarget = 44.dp
    val iconSizeSm = 16.dp
    val iconSizeMd = 20.dp
    val iconSizeLg = 24.dp
    val capsuleHeight = 32.dp
    val pillHeight = 28.dp
}

/**
 * Optical centering helper for CJK & Latin text.
 * Applies LineHeightStyle(Alignment.Center, Trim.Both) with explicit line height,
 * eliminating platform font padding and visual descent sinking in pills, badges, and tabs.
 */
fun TextStyle.tight(lineHeight: TextUnit = TextUnit.Unspecified): TextStyle = copy(
    lineHeight = if (lineHeight != TextUnit.Unspecified) lineHeight else this.fontSize,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.Both
    )
)

val MakeItRealTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both
        )
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both
        )
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 10.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both
        )
    )
)

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

// Unified Studio Dark Design Tokens
val StudioDarkStatusBar = Color(0xFF1A1A1A)
val StudioDarkTopBar = Color(0xFF1E2024)
val StudioDarkWorkspace = Color(0xFF25262A)
val StudioDarkBottomBar = Color(0xFF1A1A1A)
val StudioDarkCard = Color(0xFF202226)

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
        typography = MakeItRealTypography,
        content = content
    )
}
