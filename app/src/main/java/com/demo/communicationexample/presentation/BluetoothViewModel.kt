package com.demo.communicationexample.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.communicationexample.domain.BluetoothConnectionUseCase
import com.demo.communicationexample.domain.entities.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothConnectionUseCase: BluetoothConnectionUseCase
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    init {
        viewModelScope.launch {
            bluetoothConnectionUseCase.receiveMessage().collect { message ->
                _messages.value = _messages.value + message
            }
        }
    }

    fun connect() {
        viewModelScope.launch {
            bluetoothConnectionUseCase.connect()
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            val msg = Message(message, System.currentTimeMillis(), isSent = true)
            bluetoothConnectionUseCase.sendMessage(msg)
        }
    }
}
