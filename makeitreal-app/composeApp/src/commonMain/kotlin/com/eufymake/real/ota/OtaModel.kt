package com.eufymake.real.ota

import kotlinx.serialization.Serializable

@Serializable
data class OtaManifest(
    val code: Int = 0,
    val updateStatus: Int = 1,
    val versionCode: Int = 1,
    val versionName: String = "1.0.0",
    val modifyContent: String = "",
    val downloadUrl: String = "",
    val apkSize: Long = 0L,
    val apkMd5: String = "",
    val publishTime: String = ""
)

sealed interface OtaState {
    data object Idle : OtaState
    data object Checking : OtaState
    data class UpdateAvailable(val manifest: OtaManifest) : OtaState
    data class Downloading(val progress: Float, val currentBytes: Long, val totalBytes: Long) : OtaState
    data class ReadyToInstall(val filePath: String, val manifest: OtaManifest) : OtaState
    data object UpToDate : OtaState
    data class Error(val message: String) : OtaState
}
