package com.demo.communicationexample.presentation.bt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.communicationexample.domain.BluetoothConnectionUseCase
import com.demo.communicationexample.domain.entities.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothConnectionUseCase: BluetoothConnectionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BluetoothUiState())
    val uiState: StateFlow<BluetoothUiState> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            bluetoothConnectionUseCase.receiveMessage().collect { message ->
                _uiState.update { state ->
                    state.copy(messages = state.messages + message)
                }
            }
        }
    }

    fun connect() {
        viewModelScope.launch(Dispatchers.IO) {
            val isConnected = bluetoothConnectionUseCase.connect()
            _uiState.update { state ->
                state.copy(isConnected = isConnected, errorMessage = if (!isConnected) "Failed to connect" else null)
            }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val msg = Message(message, System.currentTimeMillis(), isSent = true)
            bluetoothConnectionUseCase.sendMessage(msg)
            _uiState.update { state ->
                state.copy(messages = state.messages + msg)
            }
        }
    }

    fun setPermissionGranted(granted: Boolean) {
        _uiState.update { state ->
            state.copy(isPermissionGranted = granted)
        }
    }
}
