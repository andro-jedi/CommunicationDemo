package com.demo.communicationexample.domain.entities

import java.util.UUID

/**
 * Data class representing a Bluetooth device.
 *
 * @param name The name of the Bluetooth device.
 * @param address The MAC address of the Bluetooth device.
 */
data class BluetoothDeviceDomain(
    val name: String?,
    val address: String,
    val uuid: UUID?,
)
