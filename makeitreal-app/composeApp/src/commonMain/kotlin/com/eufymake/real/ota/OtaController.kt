package com.eufymake.real.ota

import kotlinx.coroutines.flow.StateFlow

expect object OtaController {
    val state: StateFlow<OtaState>
    fun checkForUpdate(manual: Boolean = false)
    fun downloadUpdate()
    fun installUpdate()
    fun dismiss()
    fun getCurrentVersionName(): String
}
