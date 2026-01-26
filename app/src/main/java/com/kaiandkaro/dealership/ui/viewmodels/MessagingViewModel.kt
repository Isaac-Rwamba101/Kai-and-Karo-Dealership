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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagingViewModel @Inject constructor(
    private val messagingRepository: MessagingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            messagingRepository.getConversation(conversationId).collect { conversation ->
                _messages.value = conversation.messages
            }
        }
    }

    fun sendMessage(conversationId: String, text: String) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            val message = Message(
                senderId = currentUser?.uid ?: "",
                receiverId = "", // TODO: Get other user ID
                text = text,
                timestamp = System.currentTimeMillis()
            )
            messagingRepository.sendMessage(conversationId, message)
        }
    }
}
