package com.demo.communicationexample.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.demo.communicationexample.domain.entities.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address,
        uuid = this.uuids?.first()?.uuid,
    )
}
