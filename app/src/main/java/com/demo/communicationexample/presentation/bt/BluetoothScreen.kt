package com.demo.communicationexample.presentation.bt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.communicationexample.core.design.ConnectionTheme
import com.demo.communicationexample.domain.entities.BluetoothDeviceDomain
import com.demo.communicationexample.domain.entities.Message

@Composable
fun BluetoothScreen(
    state: BluetoothUiState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onStartServer: () -> Unit,
    onDeviceClick: (BluetoothDeviceDomain) -> Unit,
    onSendMessage: (message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        if (state.isConnected) {
            ConnectedScreen(
                state = state,
                onSendMessage = onSendMessage
            )
        } else {
            BluetoothDeviceList(
                isScanning = state.isScanning,
                pairedDevices = state.pairedDevices,
                scannedDevices = state.scannedDevices,
                onClick = onDeviceClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {
                        if (state.isScanning) {
                            onStopScan()
                        } else {
                            onStartScan()
                        }
                    }
                ) {
                    Text(text = "${if (state.isScanning) "Stop" else "Start"} scan")
                }
                Button(onClick = onStartServer) {
                    Text(text = "Start Server")
                }
            }
        }
    }
}

@Composable
fun ConnectedScreen(
    state: BluetoothUiState,
    onSendMessage: (message: String) -> Unit
) {
    var message by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(items = state.messages) { message ->
                Text(
                    text = message.content,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(if (message.isSent) Alignment.End else Alignment.Start)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(text = "Message")
                },
            )
            IconButton(onClick = {
                onSendMessage(message)
                // reset messaging field
                message = ""
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.Send,
                    contentDescription = "Send message",
                    modifier = Modifier.size(56.dp),
                )
            }
        }
    }
}

@Composable
fun BluetoothDeviceList(
    isScanning: Boolean,
    pairedDevices: List<BluetoothDeviceDomain>,
    scannedDevices: List<BluetoothDeviceDomain>,
    onClick: (BluetoothDeviceDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            Text(
                text = "Paired Devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(pairedDevices) { device ->
            Text(
                text = device.name ?: "(No name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
        }

        item {
            Row {
                Text(
                    text = "Scanned Devices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .weight(1f)
                )
                if (isScanning) {
                    CircularProgressIndicator()
                }
            }
        }
        items(scannedDevices) { device ->
            Text(
                text = device.name ?: "(No name)",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(device) }
                    .padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectedScreenPreview() {
    ConnectionTheme {
        ConnectedScreen(
            state = BluetoothUiState(
                messages = listOf(
                    Message(content = "Demo message", timestamp = 0L, isSent = true),
                    Message(content = "Demo message2", timestamp = 0L, isSent = false)
                )
            ),
            onSendMessage = {}
        )
    }
}
