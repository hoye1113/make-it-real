package com.eufymake.real

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eufymake.real.ota.OtaController
import com.eufymake.real.ui.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OtaController.init(this)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}
