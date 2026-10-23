package com.eufymake.real.model

import kotlinx.serialization.Serializable

@Serializable
data class InkChannel(
    val code: String,       // "C", "M", "Y", "K", "W", "G"
    val name: String,       // "Cyan", "Magenta", "Yellow", "Black", "White (Relief)", "Gloss (Varnish)"
    val hexColor: Long,     // Color value for UI rendering
    val percentage: Float   // 0.0 - 100.0
)

@Serializable
data class InkLevels(
    val cyan: InkChannel = InkChannel("C", "Cyan", 0xFF00A0E9, 82f),
    val magenta: InkChannel = InkChannel("M", "Magenta", 0xFFE4007F, 67f),
    val yellow: InkChannel = InkChannel("Y", "Yellow", 0xFFFFE600, 91f),
    val black: InkChannel = InkChannel("K", "Key Black", 0xFF1D2024, 74f),
    val white: InkChannel = InkChannel("W", "White UV", 0xFFE0E0E0, 48f),
    val gloss: InkChannel = InkChannel("G", "Gloss Varnish", 0xFF76D7C4, 62f)
) {
    val asList: List<InkChannel>
        get() = listOf(cyan, magenta, yellow, black, white, gloss)
}

enum class DeviceStatus {
    IDLE,
    PREPARING,
    PRINTING,
    UV_CURING,
    PAUSED,
    COMPLETED,
    OFFLINE
}

@Serializable
data class DeviceInfo(
    val id: String = "eufy-e1-8890",
    val modelName: String = "eufyMake UV E1 Pro",
    val serialNumber: String = "EM26A09439",
    val firmwareVersion: String = "v1.4.12",
    val isOnline: Boolean = true,
    val status: DeviceStatus = DeviceStatus.IDLE,
    val bedTemperatureC: Float = 38.5f,
    val uvLampTemperatureC: Float = 54.0f,
    val isChamberLightOn: Boolean = true,
    val isAirFiltrationActive: Boolean = true,
    val inkLevels: InkLevels = InkLevels(),
    val currentJobName: String? = null,
    val currentJobProgress: Float = 0f, // 0..100
    val timeRemainingSeconds: Int = 0
)
