package com.demo.communicationexample.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.demo.communicationexample.core.hasPermission
import com.demo.communicationexample.domain.BluetoothController
import com.demo.communicationexample.domain.BluetoothControllerState
import com.demo.communicationexample.domain.ConnectionResult
import com.demo.communicationexample.domain.entities.BluetoothDeviceDomain
import com.demo.communicationexample.domain.entities.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

private const val TAG = "AndroidBluetoothController"

class AndroidBluetoothController @Inject constructor(
    private val context: Context,
) : BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val _state = MutableStateFlow(BluetoothControllerState())
    override val state: StateFlow<BluetoothControllerState>
        get() = _state.asStateFlow()

    private val _pairedDevices = MutableStateFlow<Set<BluetoothDeviceDomain>>(emptySet())
    override val pairedDevices: StateFlow<Set<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()

    private val _scannedDevices = MutableStateFlow<Set<BluetoothDeviceDomain>>(emptySet())
    override val scannedDevices: StateFlow<Set<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()

    private var dataTransferService: BluetoothDataTransferService? = null

    @SuppressLint("MissingPermission")
    private val foundDeviceReceiver = FoundDeviceReceiver(
        onDeviceFound = { foundDevice ->
            _scannedDevices.update { scannedDevices ->
                val newFoundDevice = foundDevice.toBluetoothDeviceDomain()
                scannedDevices + newFoundDevice
            }
        },
        onDiscoveryStarted = {
            _state.update { it.copy(isScanning = bluetoothAdapter?.isDiscovering == true) }
        },
        onDiscoveryFinished = {
            _state.update { it.copy(isScanning = bluetoothAdapter?.isDiscovering == false) }
        }
    )

    @SuppressLint("MissingPermission")
    private val bluetoothStateReceiver = BluetoothStateReceiver(
        onStateChanged = { isConnected, bluetoothDevice ->
            if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
                _state.update { it.copy(isConnected = isConnected) }
            } else {
                _state.update { it.copy(errorMessage = "Can't connect to non-paired device") }
            }
        }
    )

    private var clientSocket: BluetoothSocket? = null
    private var serverSocket: BluetoothServerSocket? = null

    init {
        updatePairedDevices()
        context.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
        )
    }

    @SuppressLint("MissingPermission")
    override fun startDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !context.hasPermission(Manifest.permission.BLUETOOTH_SCAN)
        ) return

        _scannedDevices.update {
            emptySet()
        }

        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            },
        )

        updatePairedDevices()

        if (bluetoothAdapter?.isDiscovering == true) {
            stopDiscovery()
        }

        bluetoothAdapter?.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    fun updatePairedDevices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !context.hasPermission(Manifest.permission.BLUETOOTH_SCAN)
        ) return

        bluetoothAdapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceDomain() }
            ?.also { devices ->
                _pairedDevices.update { devices.toSet() }
            }
    }

    @SuppressLint("MissingPermission")
    override fun stopDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !context.hasPermission(Manifest.permission.BLUETOOTH_SCAN)
        ) return

        _state.update { it.copy(isScanning = bluetoothAdapter?.isDiscovering == false )}

        bluetoothAdapter?.cancelDiscovery()
    }

    @SuppressLint("MissingPermission")
    override fun startBluetoothServer(): Flow<ConnectionResult> {
        return flow {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                && !context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
            ) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }

            serverSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                "communication_service",
                UUID.fromString(SPP_UUID)
            )

            var shouldLoop = true
            while (shouldLoop) {
                clientSocket = try {
                    serverSocket?.accept()
                } catch (e: IOException) {
                    shouldLoop = false
                    null
                }

                emit(ConnectionResult.Connected)

                clientSocket?.let {
                    serverSocket?.close()
                    val service = BluetoothDataTransferService(it)
                    dataTransferService = service

                    emitAll(
                        service
                            .listenForIncomingData()
                            .map {
                                ConnectionResult.Transferred(it)
                            }
                    )
                }
            }

        }.flowOn(Dispatchers.IO)
    }

    @SuppressLint("MissingPermission")
    override fun connectToDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {
        return flow {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                && !context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
            ) {
                throw SecurityException("No BLUETOOTH_CONNECT permission")
            }

            val bluetoothDevice: BluetoothDevice? by lazy {
                bluetoothAdapter?.getRemoteDevice(device.address)
            }
            Log.d(TAG, "uuid: ${device.uuid}")
            clientSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(
                UUID.fromString(SPP_UUID)
            )

            stopDiscovery()

            clientSocket?.let { socket ->
                try {
                    socket.connect()
                    Log.d(TAG, "Connection established")
                    emit(ConnectionResult.Connected)
                    BluetoothDataTransferService(socket).also {
                        dataTransferService = it
                        emitAll(it.listenForIncomingData().map { data ->
                            ConnectionResult.Transferred(data)
                        })
                    }
                } catch (e: IOException) {
                    socket.close()
                    clientSocket = null
                    e.printStackTrace()
                    Log.d(TAG, "Connection error: ${e.localizedMessage}")
                    emit(ConnectionResult.Error("Connection was interrupted \n " + e.localizedMessage))
                }
            }
        }
            .flowOn(Dispatchers.IO)
    }

    override fun closeConnection() {
        clientSocket?.close()
        clientSocket = null
        serverSocket?.close()
        serverSocket = null
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
        context.unregisterReceiver(bluetoothStateReceiver)
        closeConnection()
    }

    @SuppressLint("MissingPermission")
    override suspend fun sendMessage(message: String): Message? {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            && !context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
        ) {
            return null
        }

        if (dataTransferService == null) {
            return null
        }

        val bluetoothMessage = Message(
            content = message,
            timestamp = System.currentTimeMillis(),
            isSent = true
        )

        dataTransferService?.sendMessage(message.toByteArray())

        return bluetoothMessage
    }

    companion object {

        const val SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB"
    }
}
