package com.demo.communicationexample.communication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context

class BluetoothManagerWrapperImpl(
    context: Context
) : BluetoothManagerWrapper {

    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    override val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    override fun isBluetoothEnabled(): Boolean {
        return bluetoothManager.adapter?.isEnabled == true
    }
}
