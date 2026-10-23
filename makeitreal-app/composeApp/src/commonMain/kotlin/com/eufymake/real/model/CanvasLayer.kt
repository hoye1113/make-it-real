package com.eufymake.real.model

import kotlinx.serialization.Serializable

enum class GlossMode {
    NONE,
    SPOT_GLOSS,
    FULL_GLOSS,
    MATTE_VARNISH
}

enum class PrintMode {
    COLOR_ONLY,
    WHITE_ONLY,
    COLOR_AND_WHITE,
    RELIEF_3D
}

enum class LayerType {
    IMAGE,
    TEXT,
    SHAPE
}

@Serializable
data class CanvasLayer(
    val id: String,
    val name: String,
    val type: LayerType,
    val xOffsetMm: Float = 0f,
    val yOffsetMm: Float = 0f,
    val widthMm: Float = 60f,
    val heightMm: Float = 60f,
    val rotationDegrees: Float = 0f,
    val scaleX: Float = 1.0f,
    val scaleY: Float = 1.0f,
    val opacity: Float = 1.0f,
    val isLocked: Boolean = false,
    val isVisible: Boolean = true,
    val zIndex: Int = 0,
    
    // UV Craft parameters
    val printMode: PrintMode = PrintMode.RELIEF_3D,
    val reliefDepthMm: Float = 1.2f, // 0.1 - 3.0mm UV stacking
    val glossMode: GlossMode = GlossMode.SPOT_GLOSS,
    
    // Type specific content
    val textContent: String = "eufyMake",
    val textColor: Long = 0xFF1D2024,
    val fontSizeSp: Float = 28f,
    val imageUrl: String? = null,
    val imagePrompt: String? = null,
    val shapeType: String = "RECTANGLE", // "RECTANGLE", "CIRCLE", "STAR", "BADGE"
    val fillColor: Long = 0xFF2E7D32
)
