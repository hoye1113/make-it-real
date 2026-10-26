package com.eufymake.real.ui.util

actual fun openImageFilePicker(): String? {
    // On Android, file picking is typically handled asynchronously via ActivityResultContracts.
    // Returning null satisfies the synchronous Multiplatform expect/actual signature.
    return null
}
