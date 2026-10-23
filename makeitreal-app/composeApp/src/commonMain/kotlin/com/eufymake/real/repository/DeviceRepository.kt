package com.eufymake.real.repository

import com.eufymake.real.model.DeviceInfo
import com.eufymake.real.model.DeviceStatus
import com.eufymake.real.model.Draft
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface DeviceRepository {
    val deviceInfo: StateFlow<DeviceInfo>
    fun toggleChamberLight()
    fun toggleAirFiltration()
    fun startPrintJob(draft: Draft)
    fun cancelPrintJob()
}

class MockDeviceRepository : DeviceRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var printJob: Job? = null

    private val _deviceInfo = MutableStateFlow(DeviceInfo())
    override val deviceInfo: StateFlow<DeviceInfo> = _deviceInfo.asStateFlow()

    override fun toggleChamberLight() {
        _deviceInfo.value = _deviceInfo.value.copy(
            isChamberLightOn = !_deviceInfo.value.isChamberLightOn
        )
    }

    override fun toggleAirFiltration() {
        _deviceInfo.value = _deviceInfo.value.copy(
            isAirFiltrationActive = !_deviceInfo.value.isAirFiltrationActive
        )
    }

    override fun startPrintJob(draft: Draft) {
        printJob?.cancel()
        _deviceInfo.value = _deviceInfo.value.copy(
            status = DeviceStatus.PREPARING,
            currentJobName = draft.title,
            currentJobProgress = 0f,
            timeRemainingSeconds = 45
        )

        printJob = scope.launch {
            delay(1200) // Warming up UV lamp
            _deviceInfo.value = _deviceInfo.value.copy(
                status = DeviceStatus.PRINTING,
                uvLampTemperatureC = 58.5f
            )

            for (step in 1..100) {
                delay(300)
                val remaining = ((100 - step) * 0.45).toInt()
                _deviceInfo.value = _deviceInfo.value.copy(
                    currentJobProgress = step.toFloat(),
                    timeRemainingSeconds = remaining
                )
            }

            // Post-print UV curing pass
            _deviceInfo.value = _deviceInfo.value.copy(
                status = DeviceStatus.UV_CURING,
                timeRemainingSeconds = 5
            )
            delay(1500)

            // Complete and consume a small ink percentage
            val currentInk = _deviceInfo.value.inkLevels
            val updatedInk = currentInk.copy(
                white = currentInk.white.copy(percentage = (currentInk.white.percentage - 3f).coerceAtLeast(5f)),
                gloss = currentInk.gloss.copy(percentage = (currentInk.gloss.percentage - 1.5f).coerceAtLeast(5f)),
                cyan = currentInk.cyan.copy(percentage = (currentInk.cyan.percentage - 0.8f).coerceAtLeast(5f)),
                magenta = currentInk.magenta.copy(percentage = (currentInk.magenta.percentage - 0.8f).coerceAtLeast(5f))
            )

            _deviceInfo.value = _deviceInfo.value.copy(
                status = DeviceStatus.COMPLETED,
                currentJobProgress = 100f,
                timeRemainingSeconds = 0,
                inkLevels = updatedInk,
                uvLampTemperatureC = 42f
            )

            delay(3000)
            _deviceInfo.value = _deviceInfo.value.copy(
                status = DeviceStatus.IDLE,
                currentJobName = null,
                currentJobProgress = 0f
            )
        }
    }

    override fun cancelPrintJob() {
        printJob?.cancel()
        _deviceInfo.value = _deviceInfo.value.copy(
            status = DeviceStatus.IDLE,
            currentJobName = null,
            currentJobProgress = 0f,
            timeRemainingSeconds = 0
        )
    }
}
