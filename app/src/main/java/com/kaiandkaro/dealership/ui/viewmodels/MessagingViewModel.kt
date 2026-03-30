package com.kaiandkaro.dealership.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaiandkaro.dealership.models.Message
import com.kaiandkaro.dealership.models.User
import com.kaiandkaro.dealership.repositories.AuthRepository
import com.kaiandkaro.dealership.repositories.messaging.MessagingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    
    private val _otherUser = MutableStateFlow<User?>(null)
    val otherUser: StateFlow<User?> = _otherUser.asStateFlow()
    
    private var conversationId: String? = null

    fun loadMessages(conversationId: String, otherUserId: String? = null) {
        this.conversationId = conversationId
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            
            // If we have otherUserId upfront (e.g. from nav), fetch it immediately
            if (otherUserId != null) {
                if (otherUserId == "admin") {
                    // Specific case for support
                    _otherUser.value = User(name = "Customer Support", role = "admin")
                } else {
                    _otherUser.value = authRepository.getUserData(otherUserId)
                }
            }
            
            messagingRepository.getConversation(conversationId).collect { conversation ->
                // If we didn't have otherUserId or it's a different one, update it
                val participantId = conversation.participants.firstOrNull { it != currentUser?.uid }
                if (participantId != null && (_otherUser.value == null || _otherUser.value?.uid != participantId)) {
                    if (participantId != "admin") {
                        _otherUser.value = authRepository.getUserData(participantId)
                    }
                }

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
            val currentConvId = conversationId ?: return@launch
            
            // Determine other participant
            var otherParticipantId = _otherUser.value?.uid
            if (otherParticipantId == null) {
                val conversation = messagingRepository.getConversation(currentConvId).first()
                otherParticipantId = conversation.participants.firstOrNull { it != currentUser.uid }
            }
            
            if (otherParticipantId == null && currentConvId.startsWith("support_")) {
                otherParticipantId = "admin"
            }

            if (otherParticipantId == null) return@launch

            val message = Message(
                senderId = currentUser.uid,
                receiverId = otherParticipantId,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            messagingRepository.sendMessage(currentConvId, message)
        }
    }
}
