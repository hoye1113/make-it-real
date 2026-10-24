package com.eufymake.real

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.eufymake.real.ui.App
import java.awt.GraphicsEnvironment

fun main() = application {
    // Automatically calculate optimal window size based on physical screen bounds (e.g. 1280x800)
    val maxBounds = try {
        GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
    } catch (_: Throwable) {
        null
    }
    
    val screenHeight = maxBounds?.height ?: 800
    // Usable window height: reserve 40px for desktop taskbar/borders, capped at 720dp
    val windowHeightDp = minOf(720, (screenHeight - 50)).coerceAtLeast(600).dp
    val windowWidthDp = 380.dp // Standard mobile phone viewport width

    Window(
        onCloseRequest = ::exitApplication,
        title = "eufyMake - MakeItReal Mobile App",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(windowWidthDp, windowHeightDp)
        ),
        resizable = true
    ) {
        App()
    }
}

