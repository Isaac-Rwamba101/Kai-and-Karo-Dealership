package com.kaiandkaro.dealership.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kaiandkaro.dealership.ui.theme.DealershipTheme
import com.kaiandkaro.dealership.ui.viewmodels.MessageDisplay
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
                modifier = Modifier.weight(1f),
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    MessageItem(message = message)
                }
            }
            MessageInput(onSendMessage = { message ->
                messagingViewModel.sendMessage(message)
            })
        }
    }
}

@Composable
fun MessageItem(message: MessageDisplay) {
    val alignment = if (message.isFromCurrentUser) Alignment.End else Alignment.Start
    val backgroundColor = if (message.isFromCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val textColor = if (message.isFromCurrentUser) Color.White else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!message.isFromCurrentUser) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .padding(8.dp)
            ) {
                Text(text = message.message.text, color = textColor)
            }
            Spacer(modifier = Modifier.weight(1f))
        } else {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .padding(8.dp)
            ) {
                Text(text = message.message.text, color = textColor)
            }
        }
    }
}

@Composable
fun MessageInput(onSendMessage: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Row(modifier = Modifier.padding(8.dp)) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message") }
        )
        Spacer(modifier = Modifier.width(8.dp))
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