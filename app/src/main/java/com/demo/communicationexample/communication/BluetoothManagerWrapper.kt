package com.demo.communicationexample.communication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver

interface BluetoothManagerWrapper {

    val bluetoothAdapter: BluetoothAdapter?

    fun isBluetoothEnabled(): Boolean
}
