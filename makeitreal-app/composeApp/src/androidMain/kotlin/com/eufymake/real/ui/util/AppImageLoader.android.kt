package com.eufymake.real.ui.util

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.InputStream

@Composable
actual fun rememberAppImage(name: String): ImageBitmap? {
    val context = LocalContext.current
    return remember(name) {
        AndroidAppImageLoader.loadBitmap(context, name)
    }
}

object AndroidAppImageLoader {
    private val memoryCache = mutableMapOf<String, ImageBitmap>()

    fun loadBitmap(context: Context, pathOrResource: String): ImageBitmap? {
        memoryCache[pathOrResource]?.let { return it }

        // 1. Try Android assets (mapped from src/commonMain/resources)
        val cleanName = pathOrResource.trim()
        val assetCandidates = listOf(
            cleanName,
            "images/$cleanName",
            cleanName.removePrefix("images/")
        )

        for (candidate in assetCandidates) {
            try {
                context.assets.open(candidate).use { stream ->
                    val androidBitmap = BitmapFactory.decodeStream(stream)
                    if (androidBitmap != null) {
                        val composeBitmap = androidBitmap.asImageBitmap()
                        memoryCache[pathOrResource] = composeBitmap
                        return composeBitmap
                    }
                }
            } catch (_: Throwable) {
            }
        }

        // 2. Try class loader resource (fallback)
        val cl = AndroidAppImageLoader::class.java.classLoader
        val classLoaderCandidates = listOf(
            cleanName,
            "images/$cleanName"
        )
        for (candidate in classLoaderCandidates) {
            val stream: InputStream? = cl?.getResourceAsStream(candidate)
            if (stream != null) {
                try {
                    stream.use { s ->
                        val bytes = s.readBytes()
                        val androidBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        if (androidBitmap != null) {
                            val composeBitmap = androidBitmap.asImageBitmap()
                            memoryCache[pathOrResource] = composeBitmap
                            return composeBitmap
                        }
                    }
                } catch (_: Throwable) {
                }
            }
        }

        // 3. Try direct file path (for user imported photos/drafts)
        try {
            val file = File(cleanName)
            if (file.exists() && file.isFile) {
                val androidBitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (androidBitmap != null) {
                    val composeBitmap = androidBitmap.asImageBitmap()
                    memoryCache[pathOrResource] = composeBitmap
                    return composeBitmap
                }
            }
        } catch (_: Throwable) {
        }

        return null
    }
}
