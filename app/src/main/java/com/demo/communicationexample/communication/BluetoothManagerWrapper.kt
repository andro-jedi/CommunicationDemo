package com.demo.communicationexample.communication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver

interface BluetoothManagerWrapper {

    val bluetoothAdapter: BluetoothAdapter?

    fun isBluetoothEnabled(): Boolean
    fun getConnectedDevices(profile: Int): List<BluetoothDevice>

    fun stopDeviceScan(bluetoothReceiver: BroadcastReceiver)
    suspend fun startDeviceScan(bluetoothReceiver: BroadcastReceiver)
}
