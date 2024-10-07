package com.demo.communicationexample.presentation.bt

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.communicationexample.data.BluetoothRepository
import com.demo.communicationexample.domain.entities.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothRepository: BluetoothRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BluetoothUiState())

    val uiState: StateFlow<BluetoothUiState> = combine(
        bluetoothRepository.discoveredDevices,
        _uiState
    ) { discoveredDevices, uiState ->
        uiState.copy(discoveredDevices = discoveredDevices.toList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, BluetoothUiState())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            bluetoothRepository.receiveMessages().collect { message ->
                _uiState.update { state ->
                    state.copy(messages = state.messages + message)
                }
            }
        }
    }

    fun startScanning() {
        viewModelScope.launch {
            bluetoothRepository.startDeviceScan()
        }
    }

    fun stopScanning() {
        bluetoothRepository.stopDeviceScan()
    }

    fun connectToDevice(device: BluetoothDevice) {
        viewModelScope.launch {
            val isConnected = bluetoothRepository.connectToDevice(device)
            _uiState.update { it.copy(isConnected = isConnected) }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val msg = Message(message, System.currentTimeMillis(), isSent = true)
            bluetoothRepository.sendMessage(msg)
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
