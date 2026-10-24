package com.eufymake.real.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.io.File
import java.io.InputStream

@Composable
actual fun rememberAppImage(name: String): ImageBitmap? {
    return remember(name) {
        AppImageLoader.loadBitmap(name)
    }
}

object AppImageLoader {
    private val memoryCache = mutableMapOf<String, ImageBitmap>()

    fun loadBitmap(pathOrResource: String): ImageBitmap? {
        memoryCache[pathOrResource]?.let { return it }

        // 1. Try class loader resource (images/xxx.webp)
        val resourceStream: InputStream? = AppImageLoader::class.java.classLoader?.getResourceAsStream(pathOrResource)
            ?: AppImageLoader::class.java.classLoader?.getResourceAsStream("images/$pathOrResource")
        if (resourceStream != null) {
            try {
                val bytes = resourceStream.readBytes()
                val skiaImage = Image.makeFromEncoded(bytes)
                val bitmap = skiaImage.toComposeImageBitmap()
                memoryCache[pathOrResource] = bitmap
                return bitmap
            } catch (_: Exception) {
            } finally {
                resourceStream.close()
            }
        }

        // 2. Try direct file path
        val candidates = listOf(
            File(pathOrResource),
            File("assets/community/$pathOrResource"),
            File("../assets/community/$pathOrResource"),
            File("makeitreal-app/assets/community/$pathOrResource"),
            File("composeApp/src/commonMain/resources/images/$pathOrResource")
        )

        for (file in candidates) {
            if (file.exists() && file.isFile) {
                try {
                    val bytes = file.readBytes()
                    val skiaImage = Image.makeFromEncoded(bytes)
                    val bitmap = skiaImage.toComposeImageBitmap()
                    memoryCache[pathOrResource] = bitmap
                    return bitmap
                } catch (_: Exception) {
                }
            }
        }

        return null
    }
}
