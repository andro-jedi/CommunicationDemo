package com.demo.communicationexample.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.*
import com.demo.communicationexample.communication.BluetoothManagerWrapper
import com.demo.communicationexample.domain.entities.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.IOException

class BluetoothRepositoryImpl(
    private val bluetoothManager: BluetoothManagerWrapper
) : BluetoothRepository {

    private val bufferSize = 1024

    private var bluetoothSocket: BluetoothSocket? = null
    private val _discoveredDevices = MutableStateFlow<Set<BluetoothDevice>>(emptySet())

    // Flow to emit discovered devices
    override val discoveredDevices: StateFlow<Set<BluetoothDevice>> = _discoveredDevices.asStateFlow()

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothDevice.ACTION_FOUND) {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    _discoveredDevices.value += it
                }
            }
        }
    }

    override suspend fun startDeviceScan() {
        bluetoothManager.startDeviceScan(bluetoothReceiver)
    }

    override fun stopDeviceScan() {
        bluetoothManager.stopDeviceScan(bluetoothReceiver)
    }

    @SuppressLint("MissingPermission")
    override suspend fun connectToDevice(device: BluetoothDevice): Boolean {
        try {
            stopDeviceScan() // Stop discovery when connecting

            val uuid = device.uuids.firstOrNull()?.uuid ?: return false
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect() // Blocking call to connect
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun sendMessage(message: Message) {
        withContext(Dispatchers.IO) {
            try {
                bluetoothSocket?.let {
                    val outputStream = it.outputStream
                    val messageBytes = message.content.toByteArray()
                    outputStream.write(messageBytes)
                    outputStream.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun receiveMessages(): Flow<Message> = flow {
        try {
            val inputStream = bluetoothSocket?.inputStream
            val buffer = ByteArray(bufferSize)

            while (true) {
                val bytesRead = inputStream?.read(buffer)
                if (bytesRead != null && bytesRead > 0) {
                    val messageContent = String(buffer, 0, bytesRead)
                    val receivedMessage = Message(
                        content = messageContent,
                        timestamp = System.currentTimeMillis(),
                        isSent = false
                    )
                    emit(receivedMessage)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }.flowOn(Dispatchers.IO)
}
