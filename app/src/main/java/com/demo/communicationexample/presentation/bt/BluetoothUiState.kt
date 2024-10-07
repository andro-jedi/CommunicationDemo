package com.demo.communicationexample.presentation.bt

import com.demo.communicationexample.domain.entities.Message

data class BluetoothUiState(
    val isConnected: Boolean = false,
    val messages: List<Message> = emptyList(),
    val isPermissionGranted: Boolean = false,
    val errorMessage: String? = null
)
