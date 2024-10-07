package com.demo.communicationexample.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.demo.communicationexample.core.design.ConnectionTheme
import com.demo.communicationexample.presentation.bt.BluetoothScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ConnectionTheme {
                BluetoothScreen()
            }
        }
    }
}
