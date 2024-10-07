package com.demo.communicationexample.domain

import com.demo.communicationexample.domain.entities.Message
import kotlinx.coroutines.flow.Flow

interface BluetoothConnectionUseCase {
    suspend fun connect(): Boolean
    suspend fun sendMessage(message: Message)
    suspend fun receiveMessage(): Flow<Message>
}
