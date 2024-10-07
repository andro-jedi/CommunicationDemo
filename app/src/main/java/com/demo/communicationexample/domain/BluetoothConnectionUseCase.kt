package com.demo.communicationexample.domain

import com.demo.communicationexample.data.BluetoothRepository
import com.demo.communicationexample.domain.entities.Message
import kotlinx.coroutines.flow.Flow

class BluetoothConnectionUseCase(
    private val bluetoothRepository: BluetoothRepository
) {

    suspend fun connect(): Boolean {
        return bluetoothRepository.connect()
    }

    suspend fun sendMessage(message: Message) {
        bluetoothRepository.sendMessage(message)
    }

    fun receiveMessage(): Flow<Message> {
        return bluetoothRepository.receiveMessages()
    }
}
