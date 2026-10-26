package com.eufymake.real.ota

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

actual object OtaController {
    private val _state = MutableStateFlow<OtaState>(OtaState.Idle)
    actual val state: StateFlow<OtaState> = _state.asStateFlow()

    actual fun checkForUpdate(manual: Boolean) {
        // Desktop OTA is optional/no-op or points to web releases
        if (manual) {
            _state.value = OtaState.UpToDate
        }
    }

    actual fun downloadUpdate() {}

    actual fun installUpdate() {}

    actual fun dismiss() {
        _state.value = OtaState.Idle
    }

    actual fun getCurrentVersionName(): String = "1.0.1"
}
