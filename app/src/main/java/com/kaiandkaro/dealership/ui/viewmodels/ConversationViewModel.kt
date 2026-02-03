package com.kaiandkaro.dealership.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kaiandkaro.dealership.models.Conversation
import com.kaiandkaro.dealership.repositories.AuthRepository
import com.kaiandkaro.dealership.repositories.conversation.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConversationDisplay(
    val conversationId: String,
    val otherParticipantName: String,
    val lastMessage: String
)

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<ConversationDisplay>>(emptyList())
    val conversations: StateFlow<List<ConversationDisplay>> = _conversations.asStateFlow()

    init {
        viewModelScope.launch {
            conversationRepository.getConversations().collect { conversations ->
                val currentUserId = firebaseAuth.currentUser?.uid ?: return@collect
                val displayList = conversations.mapNotNull { conversation ->
                    val otherParticipantId = conversation.participants.firstOrNull { it != currentUserId } ?: return@mapNotNull null
                    val otherParticipant = authRepository.getUserData(otherParticipantId)
                    val lastMessage = conversation.messages.lastOrNull()?.text ?: "No messages yet"

                    ConversationDisplay(
                        conversationId = conversation.id,
                        otherParticipantName = otherParticipant?.name ?: "Unknown User",
                        lastMessage = lastMessage
                    )
                }
                _conversations.value = displayList
            }
        }
    }
}
