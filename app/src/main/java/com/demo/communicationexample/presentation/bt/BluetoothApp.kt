package com.demo.communicationexample.presentation.bt

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothApp(
    viewModel: BluetoothViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(key1 = state.isConnected) {
        if (state.isConnected) {
            snackbarHostState.showSnackbar(
                message = "Connected",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.systemBars,
    ) { innerPadding ->
        when {
            state.isConnecting -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Text(text = "Connecting...")
                }
            }

            else -> {
                BluetoothScreen(
                    state = state,
                    onStartScan = viewModel::startScan,
                    onStopScan = viewModel::stopScan,
                    onSendMessage = viewModel::sendMessage,
                    onDeviceClick = viewModel::connectToDevice,
                    onStartServer = viewModel::waitForIncomingConnections,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
