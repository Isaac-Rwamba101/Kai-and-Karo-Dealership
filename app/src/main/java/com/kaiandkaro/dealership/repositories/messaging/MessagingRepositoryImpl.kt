package com.kaiandkaro.dealership.repositories.messaging

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.kaiandkaro.dealership.models.Conversation
import com.kaiandkaro.dealership.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessagingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MessagingRepository {

    override fun getConversation(conversationId: String): Flow<Conversation> {
        val conversationFlow = firestore.collection("conversations").document(conversationId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(Conversation::class.java) ?: Conversation(id = conversationId)
            }

        val messagesFlow = firestore.collection("conversations").document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Message::class.java)
            }

        return combine(conversationFlow, messagesFlow) { conversation, messages ->
            conversation.copy(messages = messages)
        }
    }

    override suspend fun sendMessage(conversationId: String, message: Message) {
        try {
            // Ensure the conversation document exists and has participants
            val conversationDoc = firestore.collection("conversations").document(conversationId)
            val snapshot = conversationDoc.get().await()
            
            if (!snapshot.exists()) {
                val participants = listOf(message.senderId, message.receiverId)
                conversationDoc.set(mapOf("participants" to participants, "id" to conversationId)).await()
            }

            conversationDoc.collection("messages")
                .add(message)
                .await()
        } catch (e: Exception) {
            // Log error
        }
    }
}
