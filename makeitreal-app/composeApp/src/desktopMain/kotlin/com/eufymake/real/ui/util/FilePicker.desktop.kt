package com.eufymake.real.ui.util

import java.awt.FileDialog
import java.awt.Frame
import java.io.File

actual fun openImageFilePicker(): String? {
    return try {
        val dialog = FileDialog(null as Frame?, "选择导入图片 (PNG / JPG / WebP)", FileDialog.LOAD)
        dialog.setFilenameFilter { _, name ->
            val lower = name.lowercase()
            lower.endsWith(".png") || lower.endsWith(".jpg") ||
                    lower.endsWith(".jpeg") || lower.endsWith(".webp")
        }
        dialog.isVisible = true
        val file = dialog.file ?: return null
        val dir = dialog.directory ?: return null
        File(dir, file).absolutePath
    } catch (_: Throwable) {
        null
    }
}
