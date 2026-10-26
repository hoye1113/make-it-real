package com.eufymake.real.ota

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

actual object OtaController {
    private const val MANIFEST_URL = "https://www.modelscope.cn/models/HoyeWork/makeitreal-app/resolve/master/latest.json"

    private val json = Json { ignoreUnknownKeys = true }
    private val scope = CoroutineScope(Dispatchers.IO)
    private var downloadJob: Job? = null

    private val _state = MutableStateFlow<OtaState>(OtaState.Idle)
    actual val state: StateFlow<OtaState> = _state.asStateFlow()

    private var appContext: Context? = null
    private var currentManifest: OtaManifest? = null
    private var currentApkFile: File? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun getContext(): Context {
        return appContext ?: throw IllegalStateException("OtaController.init(context) must be called first")
    }

    private fun getApkDir(): File {
        val dir = File(getContext().getExternalFilesDir(null), "apk")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    actual fun getCurrentVersionName(): String {
        val ctx = appContext ?: return "1.0.1"
        return try {
            ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName ?: "1.0.1"
        } catch (_: Throwable) {
            "1.0.1"
        }
    }

    fun getCurrentVersionCode(): Int {
        val ctx = appContext ?: return 1
        return try {
            val pInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
        } catch (_: Throwable) {
            1
        }
    }

    actual fun checkForUpdate(manual: Boolean) {
        if (_state.value is OtaState.Checking || _state.value is OtaState.Downloading) return

        _state.value = OtaState.Checking
        scope.launch {
            try {
                val conn = openConnectionWithRedirects(MANIFEST_URL)
                val jsonString = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()

                val manifest = json.decodeFromString<OtaManifest>(jsonString)
                currentManifest = manifest

                val localCode = getCurrentVersionCode()
                if (manifest.versionCode > localCode && manifest.downloadUrl.isNotBlank()) {
                    // Check if already downloaded
                    val targetApk = File(getApkDir(), "MakeItReal-v${manifest.versionName}.apk")
                    if (targetApk.exists() && checkMd5(targetApk, manifest.apkMd5)) {
                        currentApkFile = targetApk
                        _state.value = OtaState.ReadyToInstall(targetApk.absolutePath, manifest)
                    } else {
                        _state.value = OtaState.UpdateAvailable(manifest)
                    }
                } else {
                    _state.value = if (manual) OtaState.UpToDate else OtaState.Idle
                }
            } catch (e: Throwable) {
                _state.value = if (manual) {
                    OtaState.Error(
                        message = e.message ?: "检查更新失败",
                        step = OtaErrorStep.CHECK,
                        manifest = null
                    )
                } else {
                    OtaState.Idle
                }
            }
        }
    }

    actual fun downloadUpdate() {
        val manifest = currentManifest ?: return
        if (_state.value is OtaState.Downloading || downloadJob?.isActive == true) return

        val targetApk = File(getApkDir(), "MakeItReal-v${manifest.versionName}.apk")
        val tmpApk = File(getApkDir(), "MakeItReal-v${manifest.versionName}.apk.tmp")

        _state.value = OtaState.Downloading(0f, 0L, manifest.apkSize, manifest)

        downloadJob = scope.launch {
            try {
                val conn = openConnectionWithRedirects(manifest.downloadUrl)
                val totalLength = conn.contentLengthLong.takeIf { it > 0 } ?: manifest.apkSize

                val digest = MessageDigest.getInstance("MD5")
                conn.inputStream.use { input ->
                    FileOutputStream(tmpApk).use { output ->
                        val buffer = ByteArray(8192)
                        var downloaded = 0L
                        var lastReported = 0L

                        while (true) {
                            val bytesRead = input.read(buffer)
                            if (bytesRead == -1) break
                            output.write(buffer, 0, bytesRead)
                            digest.update(buffer, 0, bytesRead)
                            downloaded += bytesRead

                            if (downloaded - lastReported > 131072 || downloaded == totalLength) {
                                lastReported = downloaded
                                val progress = if (totalLength > 0) (downloaded.toFloat() / totalLength).coerceIn(0f, 1f) else 0f
                                _state.value = OtaState.Downloading(progress, downloaded, totalLength, manifest)
                            }
                        }
                        output.flush()
                    }
                }
                conn.disconnect()

                // Check MD5 if provided
                if (manifest.apkMd5.isNotBlank()) {
                    val computedMd5 = digest.digest().joinToString("") { "%02x".format(it) }
                    if (!computedMd5.equals(manifest.apkMd5.trim(), ignoreCase = true)) {
                        tmpApk.delete()
                        throw IOException("APK 完整性校验失败 (MD5 不匹配)")
                    }
                }

                if (targetApk.exists()) targetApk.delete()
                if (!tmpApk.renameTo(targetApk)) {
                    throw IOException("临时文件重命名失败")
                }

                currentApkFile = targetApk
                _state.value = OtaState.ReadyToInstall(targetApk.absolutePath, manifest)

                // Trigger install
                installUpdate()
            } catch (e: CancellationException) {
                tmpApk.delete()
            } catch (e: Throwable) {
                tmpApk.delete()
                _state.value = OtaState.Error(
                    message = e.message ?: "下载安装包失败",
                    step = OtaErrorStep.DOWNLOAD,
                    manifest = manifest
                )
            } finally {
                downloadJob = null
            }
        }
    }

    actual fun cancelDownload() {
        downloadJob?.cancel()
        downloadJob = null
        val manifest = currentManifest
        if (manifest != null) {
            val tmpApk = File(getApkDir(), "MakeItReal-v${manifest.versionName}.apk.tmp")
            if (tmpApk.exists()) tmpApk.delete()
        }
        _state.value = OtaState.Idle
    }

    actual fun installUpdate() {
        val ctx = appContext ?: return
        val apkFile = currentApkFile ?: run {
            val manifest = currentManifest ?: return
            val file = File(getApkDir(), "MakeItReal-v${manifest.versionName}.apk")
            if (file.exists()) file else return
        }

        try {
            // 1. Verify APK file existence and readability
            if (!apkFile.exists() || !apkFile.canRead()) {
                _state.value = OtaState.Error(
                    message = "安装包文件不存在或不可读",
                    step = OtaErrorStep.INSTALL,
                    manifest = currentManifest
                )
                return
            }

            // 2. Verify APK package archive integrity and matching packageName
            val archiveInfo = ctx.packageManager.getPackageArchiveInfo(apkFile.absolutePath, 0)
            if (archiveInfo == null || archiveInfo.packageName != ctx.packageName) {
                apkFile.delete()
                _state.value = OtaState.Error(
                    message = "安装包解析失败或包名不匹配",
                    step = OtaErrorStep.INSTALL,
                    manifest = currentManifest
                )
                return
            }

            // 3. Check unknown sources installation permission on Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!ctx.packageManager.canRequestPackageInstalls()) {
                    val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${ctx.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    ctx.startActivity(settingsIntent)
                    return
                }
            }

            val apkUri = FileProvider.getUriForFile(
                ctx,
                "${ctx.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            ctx.startActivity(installIntent)
        } catch (e: Throwable) {
            _state.value = OtaState.Error(
                message = "调起安装程序失败: ${e.message}",
                step = OtaErrorStep.INSTALL,
                manifest = currentManifest
            )
        }
    }

    actual fun dismiss() {
        if (downloadJob?.isActive == true) {
            cancelDownload()
        } else {
            _state.value = OtaState.Idle
        }
    }

    private fun checkMd5(file: File, expectedMd5: String): Boolean {
        if (expectedMd5.isBlank()) return true
        return try {
            val digest = MessageDigest.getInstance("MD5")
            file.inputStream().use { stream ->
                val buffer = ByteArray(8192)
                var read: Int
                while (stream.read(buffer).also { read = it } != -1) {
                    digest.update(buffer, 0, read)
                }
            }
            val computed = digest.digest().joinToString("") { "%02x".format(it) }
            computed.equals(expectedMd5.trim(), ignoreCase = true)
        } catch (_: Throwable) {
            false
        }
    }

    private fun openConnectionWithRedirects(urlString: String, maxRedirects: Int = 6): HttpURLConnection {
        var currentUrl = urlString
        var redirects = 0
        while (redirects < maxRedirects) {
            val url = URL(currentUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.instanceFollowRedirects = false
            conn.connectTimeout = 15000
            conn.readTimeout = 30000
            conn.setRequestProperty("User-Agent", "MakeItReal-Android/${getCurrentVersionName()}")

            val status = conn.responseCode
            if (status == HttpURLConnection.HTTP_MOVED_TEMP ||
                status == HttpURLConnection.HTTP_MOVED_PERM ||
                status == 307 || status == 308) {
                val newUrl = conn.getHeaderField("Location") ?: throw IOException("Redirect without Location header")
                conn.disconnect()
                currentUrl = if (newUrl.startsWith("http")) newUrl else URL(url, newUrl).toString()
                redirects++
            } else {
                return conn
            }
        }
        throw IOException("Too many redirects: $redirects")
    }
}
