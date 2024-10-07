package com.demo.communicationexample.data

import com.demo.communicationexample.domain.entities.Message
import kotlinx.coroutines.flow.Flow

interface BluetoothRepository {
    suspend fun connect(): Boolean
    suspend fun sendMessage(message: Message)
    fun receiveMessages(): Flow<Message>
}
