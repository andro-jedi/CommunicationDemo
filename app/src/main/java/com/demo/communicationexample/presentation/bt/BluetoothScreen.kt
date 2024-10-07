package com.demo.communicationexample.presentation.bt

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
        connect = viewModel::connect,
        sendMessage = viewModel::sendMessage,
        onPermissionsGranted = viewModel::setPermissionGranted,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BluetoothScreen(
    uiState: BluetoothUiState,
    connect: () -> Unit,
    sendMessage: (String) -> Unit,
    onPermissionsGranted: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12 and above
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE
        )
    } else {
        // Pre-Android 12 (API level 31)
        arrayOf(
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

    // Automatically check for permissions
    LaunchedEffect(Unit) {
        if (!uiState.isPermissionGranted) {
            requestPermissionLauncher.launch(permissions)
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
        Row(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
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
                    Button(onClick = connect) {
                        Text("Connect")
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
