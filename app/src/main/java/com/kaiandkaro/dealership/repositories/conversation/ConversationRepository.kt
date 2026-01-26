package com.kaiandkaro.dealership.repositories.conversation

import com.kaiandkaro.dealership.models.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getConversations(): Flow<List<Conversation>>
}
