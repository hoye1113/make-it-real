package com.eufymake.real.ui.util

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.io.InputStream

@Composable
actual fun rememberAppImage(name: String): ImageBitmap? {
    return remember(name) {
        AndroidAppImageLoader.loadBitmap(name)
    }
}

object AndroidAppImageLoader {
    private val memoryCache = mutableMapOf<String, ImageBitmap>()

    fun loadBitmap(pathOrResource: String): ImageBitmap? {
        memoryCache[pathOrResource]?.let { return it }

        // 1. Try class loader resource
        val cl = AndroidAppImageLoader::class.java.classLoader
        val stream: InputStream? = cl?.getResourceAsStream(pathOrResource)
            ?: cl?.getResourceAsStream("images/$pathOrResource")

        if (stream != null) {
            try {
                val bytes = stream.readBytes()
                val androidBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (androidBitmap != null) {
                    val composeBitmap = androidBitmap.asImageBitmap()
                    memoryCache[pathOrResource] = composeBitmap
                    return composeBitmap
                }
            } catch (_: Throwable) {
            } finally {
                try {
                    stream.close()
                } catch (_: Throwable) {
                }
            }
        }

        // 2. Try direct file path
        try {
            val file = File(pathOrResource)
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
