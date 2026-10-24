package com.eufymake.real.ui.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.eufymake.real.model.Substrate
import com.eufymake.real.model.SubstrateShape

/**
 * Tactile spring press effect for interactive components.
 * Shrinks slightly on touch down with natural bounce on release.
 */
fun Modifier.tactilePress(scaleDown: Float = 0.95f): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessHigh
        ),
        label = "tactileScale"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Bouncy clickable with spring release and no default gray box ripple.
 */
fun Modifier.bounceClick(
    scaleDown: Float = 0.95f,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessHigh
        ),
        label = "bounceClickScale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}

/**
 * Shimmer skeleton loading brush effect for image placeholders and loading states.
 */
fun Modifier.shimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    this.drawBehind {
        val brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFE2E8F0),
                Color(0xFFF8FAFC),
                Color(0xFFE2E8F0)
            ),
            start = Offset(translateAnim - 500f, 0f),
            end = Offset(translateAnim, size.height)
        )
        drawRect(brush = brush)
    }
}

/**
 * Material brush factory for authentic physical substrate bed rendering in 3D studio.
 */
object SubstrateMaterials {
    fun getBrush(substrate: Substrate): Brush {
        val id = substrate.id.lowercase()
        val name = substrate.name.lowercase()
        val material = substrate.defaultMaterial.lowercase()

        return when {
            id.contains("wood") || name.contains("wood") || material.contains("木") -> {
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFEFE3D0),
                        Color(0xFFE5D5BC),
                        Color(0xFFDCC6A8)
                    )
                )
            }
            id.contains("acrylic") || name.contains("acrylic") || id.contains("magnet") || material.contains("亚克力") -> {
                Brush.linearGradient(
                    listOf(
                        Color(0xFFF8FCFF),
                        Color(0xFFE8F3FA),
                        Color(0xFFDBECF8)
                    )
                )
            }
            id.contains("metal") || name.contains("metal") || id.contains("phone") || material.contains("金属") -> {
                Brush.linearGradient(
                    listOf(
                        Color(0xFFEAECEF),
                        Color(0xFFD0D5DD),
                        Color(0xFFF2F4F7),
                        Color(0xFFC7CBD3)
                    )
                )
            }
            id.contains("leather") || name.contains("leather") || material.contains("皮") -> {
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF7A583A),
                        Color(0xFF5D4025)
                    )
                )
            }
            id.contains("canvas") || name.contains("canvas") || material.contains("画布") -> {
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFF9F6F0),
                        Color(0xFFEDE8DD)
                    )
                )
            }
            else -> {
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF1F5F9)
                    )
                )
            }
        }
    }

    fun getSlabDepthColor(substrate: Substrate): Color {
        val id = substrate.id.lowercase()
        val material = substrate.defaultMaterial.lowercase()
        return when {
            id.contains("wood") || material.contains("木") -> Color(0xFFB89B77)
            id.contains("acrylic") || id.contains("magnet") || material.contains("亚克力") -> Color(0xFFBFDDF2)
            id.contains("metal") || material.contains("金属") -> Color(0xFF98A2B3)
            id.contains("leather") || material.contains("皮") -> Color(0xFF432C19)
            else -> Color(0xFFCBD5E1)
        }
    }
}
