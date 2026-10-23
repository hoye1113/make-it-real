package com.eufymake.real

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.eufymake.real.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "eufyMake - MakeItReal Mobile App",
        state = WindowState(width = 420.dp, height = 880.dp),
        resizable = true
    ) {
        App()
    }
}
