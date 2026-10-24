package com.eufymake.real.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun rememberAppImage(name: String): ImageBitmap?
