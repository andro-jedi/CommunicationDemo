package com.demo.communicationexample.presentation.bt

import com.demo.communicationexample.domain.entities.BluetoothDeviceDomain
import com.demo.communicationexample.domain.entities.Message

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDeviceDomain> = emptyList(),
    val pairedDevices: List<BluetoothDeviceDomain> = emptyList(),
    val messages: List<Message> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val isScanning: Boolean = false,
    val errorMessage: String? = null
)
