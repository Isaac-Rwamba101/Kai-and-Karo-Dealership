package com.kaiandkaro.dealership.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kaiandkaro.dealership.models.Message
import com.kaiandkaro.dealership.ui.theme.DealershipTheme
import com.kaiandkaro.dealership.ui.viewmodels.MessagingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
    navController: NavController,
    messagingViewModel: MessagingViewModel = hiltViewModel(),
    conversationId: String
) {
    val messages by messagingViewModel.messages.collectAsState()

    LaunchedEffect(conversationId) {
        messagingViewModel.loadMessages(conversationId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Conversation") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(messages) { message ->
                    MessageItem(message = message)
                }
            }
            MessageInput(onSendMessage = { message ->
                messagingViewModel.sendMessage(conversationId, message)
            })
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    Text(text = message.text)
}

@Composable
fun MessageInput(onSendMessage: (String) -> Unit) {
    var text by remember { mutableState of("") }

    Column {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.padding(),
            placeholder = { Text("Type a message") }
        )
        Button(onClick = {
            onSendMessage(text)
            text = ""
        }) {
            Text("Send")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessagingScreenPreview() {
    DealershipTheme {
        // MessagingScreen(navController = NavController(context), conversationId = "123")
    }
}