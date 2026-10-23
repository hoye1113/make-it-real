package com.eufymake.real.api

import com.eufymake.real.model.*
import kotlinx.serialization.Serializable

/**
 * Standard API Envelope for all backend JSON responses.
 */
@Serializable
data class ApiResponse<T>(
    val code: Int = 200,
    val message: String = "SUCCESS",
    val data: T? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// Request & Response DTOs

@Serializable
data class PublishProjectRequest(
    val title: String,
    val description: String,
    val substrateId: String,
    val substrateName: String,
    val tags: List<String> = emptyList(),
    val layers: List<CanvasLayer> = emptyList()
)

@Serializable
data class AIReliefGenerateRequest(
    val prompt: String,
    val style: String = "Realistic Relief",
    val substrateId: String,
    val maxDepthMm: Float = 2.0f
)

@Serializable
data class AIProgressStreamDto(
    val percentage: Int,
    val stepDescription: String,
    val previewHeightmapUrl: String? = null
)

@Serializable
data class DeviceControlRequest(
    val deviceId: String,
    val chamberLightOn: Boolean? = null,
    val airFiltrationActive: Boolean? = null
)

@Serializable
data class StartPrintJobRequest(
    val deviceId: String,
    val draftId: String,
    val printMode: PrintMode,
    val reliefDepthMm: Float,
    val glossMode: GlossMode
)

@Serializable
data class RedeemMallItemRequest(
    val itemId: String,
    val shippingAddressId: String? = null
)

@Serializable
data class CheckInResponse(
    val totalPoints: Int,
    val consecutiveDays: Int,
    val pointsAwarded: Int = 15
)
