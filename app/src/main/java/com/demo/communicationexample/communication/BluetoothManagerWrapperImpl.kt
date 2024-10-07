package com.demo.communicationexample.communication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter

class BluetoothManagerWrapperImpl(
    private val context: Context
) : BluetoothManagerWrapper {

    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    override val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    override fun isBluetoothEnabled(): Boolean {
        return bluetoothManager.adapter?.isEnabled == true
    }

    @SuppressLint("MissingPermission")
    override fun getConnectedDevices(profile: Int): List<BluetoothDevice> {
        return bluetoothManager.getConnectedDevices(profile)
    }

    @SuppressLint("MissingPermission")
    override suspend fun startDeviceScan(bluetoothReceiver: BroadcastReceiver) {
        val adapter = bluetoothAdapter ?: return

        // Register the receiver for Bluetooth device discovery
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(bluetoothReceiver, filter)

        // Start discovery
        adapter.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    override fun stopDeviceScan(bluetoothReceiver: BroadcastReceiver) {
        bluetoothAdapter?.cancelDiscovery()
        context.unregisterReceiver(bluetoothReceiver)
    }
}
