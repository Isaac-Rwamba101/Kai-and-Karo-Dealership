package com.kaiandkaro.dealership.repositories.messaging

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.kaiandkaro.dealership.models.Conversation
import com.kaiandkaro.dealership.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessagingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MessagingRepository {

    override fun getConversation(conversationId: String): Flow<Conversation> {
        return firestore.collection("conversations").document(conversationId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(Conversation::class.java) ?: Conversation()
            }
    }

    override suspend fun sendMessage(conversationId: String, message: Message) {
        try {
            firestore.collection("conversations").document(conversationId)
                .collection("messages")
                .add(message)
                .await()
        } catch (e: Exception) {
            // Handle error
        }
    }
}
