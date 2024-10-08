package com.demo.communicationexample.presentation.bt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.communicationexample.domain.BluetoothController
import com.demo.communicationexample.domain.ConnectionResult
import com.demo.communicationexample.domain.entities.BluetoothDeviceDomain
import com.demo.communicationexample.domain.entities.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothController
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices.toList(),
            pairedDevices = pairedDevices.toList()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        _state.value
    )

    private var deviceConnectionJob: Job? = null

    init {
        bluetoothController.state.onEach { state ->
            _state.update {
                it.copy(
                    isScanning = state.isScanning,
                    errorMessage = state.errorMessage,
                    isConnected = state.isConnected
                )
            }
        }.launchIn(viewModelScope)
    }

    fun connectToDevice(device: BluetoothDeviceDomain) {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController.connectToDevice(device).listenForResult()
    }

    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        bluetoothController.closeConnection()
        _state.update {
            it.copy(
                isConnecting = false,
                isConnected = false
            )
        }
    }

    fun waitForIncomingConnections() {
        _state.update { it.copy(isConnected = true) }
        deviceConnectionJob = bluetoothController
            .startBluetoothServer()
            .listenForResult()
    }

    fun startScan() {
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
    }

    fun sendMessage(messages: String) {
        viewModelScope.launch {
            val bluetoothMessage = bluetoothController.sendMessage(messages)
            if (bluetoothMessage != null) {
                _state.update {
                    it.copy(
                        messages = it.messages + bluetoothMessage
                    )
                }
            }
        }
    }

    /**
     * listen is an extension function that listens to the Flow<ConnectionResult> and updates the state accordingly
     */
    private fun Flow<ConnectionResult>.listenForResult(): Job {
        return onEach { result ->
            when (result) {
                ConnectionResult.Connected -> {
                    _state.update {
                        it.copy(
                            isConnected = true,
                            isConnecting = false,
                            errorMessage = null
                        )
                    }
                }

                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errorMessage = result.message
                        )
                    }
                }

                is ConnectionResult.Transferred -> {
                    Log.d("BluetoothViewModel", "Transferred message: ${result.message}")
                    val message = Message(content = result.message, isSent = false, timestamp = System.currentTimeMillis())
                    _state.update {
                        it.copy(messages = it.messages + message)
                    }
                }
            }
        }
            .catch { throwable ->
                Log.e("BluetoothViewModel", "Error: ${throwable.message}")
                bluetoothController.closeConnection()
                _state.update {
                    it.copy(
                        isConnected = false,
                        isConnecting = false,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        disconnectFromDevice()
        bluetoothController.release()
    }
}
