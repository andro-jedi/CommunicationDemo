package com.demo.communicationexample.data

import android.bluetooth.BluetoothManager
import com.demo.communicationexample.domain.entities.Message
import kotlinx.coroutines.flow.Flow

class BluetoothRepositoryImpl(
    private val bluetoothManager: BluetoothManager
) : BluetoothRepository {
    override suspend fun connect(): Boolean {
        TODO("Implementation for connecting via Bluetooth")
    }
    override suspend fun sendMessage(message: Message) {
        TODO("Implementation for sending message")
    }
    override fun receiveMessages(): Flow<Message> {
        TODO("Flow emitting received messages")
    }
}
