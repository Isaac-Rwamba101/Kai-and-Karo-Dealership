package com.kaiandkaro.dealership.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaiandkaro.dealership.models.Message
import com.kaiandkaro.dealership.repositories.AuthRepository
import com.kaiandkaro.dealership.repositories.messaging.MessagingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessageDisplay(
    val message: Message,
    val isFromCurrentUser: Boolean
)

@HiltViewModel
class MessagingViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageDisplay>>(emptyList())
    val messages: StateFlow<List<MessageDisplay>> = _messages.asStateFlow()
    private var conversationId: String? = null

    fun loadMessages(conversationId: String) {
        this.conversationId = conversationId
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            messagingRepository.getConversation(conversationId).collect { conversation ->
                val messageDisplays = conversation.messages.map { message ->
                    MessageDisplay(
                        message = message,
                        isFromCurrentUser = message.senderId == currentUser?.uid
                    )
                }
                _messages.value = messageDisplays
            }
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser() ?: return@launch
            val conversation = messagingRepository.getConversation(conversationId ?: return@launch).first()
            val otherParticipantId = conversation.participants.firstOrNull { it != currentUser.uid } ?: return@launch

            val message = Message(
                senderId = currentUser.uid,
                receiverId = otherParticipantId,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            messagingRepository.sendMessage(conversationId ?: return@launch, message)
        }
    }
}
