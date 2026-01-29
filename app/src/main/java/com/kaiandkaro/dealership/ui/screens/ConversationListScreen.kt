package com.kaiandkaro.dealership.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kaiandkaro.dealership.ui.viewmodels.ConversationDisplay
import com.kaiandkaro.dealership.ui.viewmodels.ConversationViewModel

@Composable
fun ConversationListScreen(
    navController: NavController,
    conversationViewModel: ConversationViewModel = hiltViewModel()
) {
    val conversations by conversationViewModel.conversations.collectAsState()

    Scaffold {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding()
        ) {
            items(conversations) { conversation ->
                ConversationListItem(conversation = conversation) {
                    navController.navigate("messaging/${conversation.conversationId}")
                }
            }
        }
    }
}

@Composable
fun ConversationListItem(conversation: ConversationDisplay, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = conversation.otherParticipantName, style = MaterialTheme.typography.headlineSmall)
            Text(text = conversation.lastMessage, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
