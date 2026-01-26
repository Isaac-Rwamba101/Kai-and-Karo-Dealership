package com.kaiandkaro.dealership.repositories.messaging

import com.kaiandkaro.dealership.models.Conversation
import com.kaiandkaro.dealership.models.Message
import kotlinx.coroutines.flow.Flow

interface MessagingRepository {
    fun getConversation(conversationId: String): Flow<Conversation>
    suspend fun sendMessage(conversationId: String, message: Message)
}
