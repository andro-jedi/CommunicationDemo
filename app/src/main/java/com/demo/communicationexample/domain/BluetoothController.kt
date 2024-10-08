package com.demo.communicationexample.domain

import com.demo.communicationexample.domain.entities.BluetoothDeviceDomain
import com.demo.communicationexample.domain.entities.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {

    val scannedDevices: StateFlow<Set<BluetoothDeviceDomain>>
    val pairedDevices: StateFlow<Set<BluetoothDeviceDomain>>
    val state: StateFlow<BluetoothControllerState>

    fun startDiscovery()
    fun stopDiscovery()

    suspend fun sendMessage(message: String): Message?

    fun startBluetoothServer(): Flow<ConnectionResult>
    fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult>
    fun closeConnection()

    fun release()
}
