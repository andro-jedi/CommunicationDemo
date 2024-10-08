package com.demo.communicationexample.domain

import com.demo.communicationexample.domain.entities.Message

data class BluetoothControllerState(
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val isScanning: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<Message> = emptyList(),
)
