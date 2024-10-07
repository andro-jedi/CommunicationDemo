package com.demo.communicationexample.data

import android.bluetooth.BluetoothDevice
import com.demo.communicationexample.domain.entities.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {

    val discoveredDevices: StateFlow<Set<BluetoothDevice>>
    suspend fun startDeviceScan()
    fun stopDeviceScan()
    suspend fun connectToDevice(device: BluetoothDevice): Boolean
    suspend fun sendMessage(message: Message)
    fun receiveMessages(): Flow<Message>
}
