package com.demo.communicationexample.presentation.bt

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BluetoothScreen(
    modifier: Modifier = Modifier,
    viewModel: BluetoothViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BluetoothScreen(
        uiState = uiState,
        connect = viewModel::connectToDevice,
        sendMessage = viewModel::sendMessage,
        startScanning = viewModel::startScanning,
        onPermissionsGranted = viewModel::setPermissionGranted,
        modifier = modifier
    )
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BluetoothScreen(
    uiState: BluetoothUiState,
    connect: (device: BluetoothDevice) -> Unit,
    sendMessage: (String) -> Unit,
    startScanning: () -> Unit,
    onPermissionsGranted: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12 and above
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE
        )
    } else {
        // Pre-Android 12 (API level 31)
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )
    }

    // Launcher for permission requests
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        onPermissionsGranted(permissionsMap.values.all { it })
    }

    LaunchedEffect(Unit) {
        if (!uiState.isPermissionGranted) {
            requestPermissionLauncher.launch(permissions)
        } else {
            startScanning()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Connection Demo",
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets.systemBars,
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (uiState.isPermissionGranted) {
                if (uiState.isConnected) {
                    Text("Connected to device")
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(uiState.messages) { message ->
                            Text(if (message.isSent) "Sent: ${message.content}" else "Received: ${message.content}")
                        }
                    }
                    TextField(
                        value = "",
                        onValueChange = sendMessage,
                        label = { Text("Send Message") }
                    )
                } else {
                    Text("Nearby devices:")
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(uiState.discoveredDevices) { device ->
                            Text(
                                text = device.name ?: "Unknown device",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { connect(device) }
                                    .padding(8.dp)
                            )
                        }
                    }

                    Button(onClick = { startScanning() }) {
                        Text("Rescan")
                    }
                }
            } else {
                Text("Bluetooth permissions are required.")
                Button(onClick = { requestPermissionLauncher.launch(permissions) }) {
                    Text("Request Permissions")
                }
            }
        }
    }
}
