package com.kaiandkaro.dealership.repositories.conversation

import com.google.firebase.firestore.FirebaseFirestore
import com.kaiandkaro.dealership.models.Conversation
import kotlinx.coroutines.tasks.await

class ConversationRepositoryImpl(private val firestore: FirebaseFirestore) : ConversationRepository {
    override suspend fun getConversations(userId: String): List<Conversation> {
        val snapshot = firestore.collection("conversations")
            .whereArrayContains("participants", userId)
            .get()
            .await()
        return snapshot.toObjects(Conversation::class.java)
    }

    override suspend fun createConversation(conversation: Conversation) {
        firestore.collection("conversations").add(conversation).await()
    }
}
