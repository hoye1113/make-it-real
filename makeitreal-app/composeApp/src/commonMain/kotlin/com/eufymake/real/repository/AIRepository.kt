package com.eufymake.real.repository

import com.eufymake.real.model.CanvasLayer
import com.eufymake.real.model.GlossMode
import com.eufymake.real.model.LayerType
import com.eufymake.real.model.PrintMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class AIGenerationState {
    data object Idle : AIGenerationState()
    data class Progress(val percent: Int, val stepDescription: String) : AIGenerationState()
    data class Success(val generatedLayer: CanvasLayer) : AIGenerationState()
    data class Error(val message: String) : AIGenerationState()
}

interface AIRepository {
    fun generate3DRelief(prompt: String, style: String = "Realistic Relief"): Flow<AIGenerationState>
}

class MockAIRepository : AIRepository {
    override fun generate3DRelief(prompt: String, style: String): Flow<AIGenerationState> = flow {
        emit(AIGenerationState.Progress(10, "Connecting to eufyMake AI Cloud Engine..."))
        delay(400)
        emit(AIGenerationState.Progress(28, "Analyzing prompt semantic structure & lighting..."))
        delay(500)
        emit(AIGenerationState.Progress(52, "Synthesizing 16-bit displacement heightmap..."))
        delay(600)
        emit(AIGenerationState.Progress(78, "Calculating 6-layer white ink UV accumulation passes..."))
        delay(500)
        emit(AIGenerationState.Progress(95, "Generating specular gloss varnish highlights..."))
        delay(400)

        val layer = CanvasLayer(
            id = "ai_layer_${System.currentTimeMillis()}",
            name = "AI: ${prompt.take(16)}...",
            type = LayerType.IMAGE,
            xOffsetMm = 0f,
            yOffsetMm = 0f,
            widthMm = 70f,
            heightMm = 70f,
            printMode = PrintMode.RELIEF_3D,
            reliefDepthMm = 1.8f,
            glossMode = GlossMode.SPOT_GLOSS,
            imagePrompt = prompt
        )
        emit(AIGenerationState.Success(layer))
    }
}
