package com.demo.communicationexample.communication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket

class BluetoothManager {

    fun getBondedDevices(): Set<BluetoothDevice>? {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter?.bondedDevices
    }

    fun getBluetoothSocket(device: BluetoothDevice): BluetoothSocket? {
        return device.createRfcommSocketToServiceRecord(device.uuids.first().uuid)
    }
}
