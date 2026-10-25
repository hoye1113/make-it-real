package com.eufymake.real.ui.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import com.eufymake.real.model.MaterialType
import com.eufymake.real.model.Substrate
import com.eufymake.real.model.SubstrateShape

/**
 * Modern tactile click feedback with spring bounce and subtle opacity.
 * Rooted in InteractionSource to naturally handle scroll cancellation, boundary exits,
 * and Desktop cursor hover affordances without ugly gray box ripples.
 */
fun Modifier.tactileClick(
    scaleDown: Float = 0.96f,
    alphaDown: Float = 0.88f,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessHigh
        ),
        label = "tactileClickScale"
    )
    val alpha by animateFloatAsState(
        targetValue = when {
            isPressed && enabled -> alphaDown
            isHovered && enabled -> 0.92f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 120),
        label = "tactileClickAlpha"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        }
        .pointerHoverIcon(PointerIcon.Hand)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = Role.Button,
            onClick = onClick
        )
}

/**
 * Tactile spring press effect for interactive components.
 * Detects touch down consuming unconsumed pointers, shrinking slightly with natural bounce.
 */
fun Modifier.tactilePress(scaleDown: Float = 0.95f): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = Spring.StiffnessHigh
        ),
        label = "tactileScale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = true)
                isPressed = true
                waitForUpOrCancellation()
                isPressed = false
            }
        }
}

/**
 * Tactile spring press effect wired to an existing InteractionSource.
 */
fun Modifier.tactilePress(
    interactionSource: MutableInteractionSource,
    scaleDown: Float = 0.95f
): Modifier = composed {
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
 * Delegates to the unified tactileClick standard.
 */
fun Modifier.bounceClick(
    scaleDown: Float = 0.96f,
    onClick: () -> Unit
): Modifier = tactileClick(scaleDown = scaleDown, onClick = onClick)

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
        val matType = MaterialType.fromString(substrate.defaultMaterial)

        return when {
            matType == MaterialType.WOOD || id.contains("wood") || name.contains("wood") -> {
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFEFE3D0),
                        Color(0xFFE5D5BC),
                        Color(0xFFDCC6A8)
                    )
                )
            }
            matType == MaterialType.ACRYLIC || id.contains("acrylic") || name.contains("acrylic") || id.contains("magnet") -> {
                Brush.linearGradient(
                    listOf(
                        Color(0xFFF8FCFF),
                        Color(0xFFE8F3FA),
                        Color(0xFFDBECF8)
                    )
                )
            }
            matType == MaterialType.METAL || id.contains("metal") || name.contains("metal") || id.contains("phone") -> {
                Brush.linearGradient(
                    listOf(
                        Color(0xFFEAECEF),
                        Color(0xFFD0D5DD),
                        Color(0xFFF2F4F7),
                        Color(0xFFC7CBD3)
                    )
                )
            }
            matType == MaterialType.LEATHER || id.contains("leather") || name.contains("leather") -> {
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF7A583A),
                        Color(0xFF5D4025)
                    )
                )
            }
            matType == MaterialType.CERAMIC || matType == MaterialType.GLASS || id.contains("canvas") || name.contains("canvas") || substrate.defaultMaterial.contains("画布") -> {
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
        val matType = MaterialType.fromString(substrate.defaultMaterial)
        return when {
            matType == MaterialType.WOOD || id.contains("wood") -> Color(0xFFB89B77)
            matType == MaterialType.ACRYLIC || id.contains("acrylic") || id.contains("magnet") -> Color(0xFFBFDDF2)
            matType == MaterialType.METAL || id.contains("metal") -> Color(0xFF98A2B3)
            matType == MaterialType.LEATHER || id.contains("leather") -> Color(0xFF432C19)
            else -> Color(0xFFCBD5E1)
        }
    }
}
