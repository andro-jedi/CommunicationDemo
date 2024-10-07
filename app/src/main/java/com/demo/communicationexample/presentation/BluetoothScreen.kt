package com.demo.communicationexample.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.demo.communicationexample.domain.entities.Message

@Composable
fun BluetoothScreen(
    viewModel: BluetoothViewModel = hiltViewModel(),
) {
    val messages by viewModel.messages.collectAsState()

    BluetoothScreen(
        messages = messages,
        connect = viewModel::connect,
        sendMessage = viewModel::sendMessage,
    )
}

@Composable
private fun BluetoothScreen(
    messages: List<Message>,
    connect: () -> Unit,
    sendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Button(onClick = connect) {
            Text("Connect")
        }
        LazyColumn {
            items(messages) { message ->
                Text(if (message.isSent) "Sent: ${message.content}" else "Received: ${message.content}")
            }
        }
        TextField(
            value = "",
            onValueChange = sendMessage,
            label = { Text("Send Message") }
        )
    }
}
