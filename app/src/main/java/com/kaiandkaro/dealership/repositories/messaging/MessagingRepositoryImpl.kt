package com.kaiandkaro.dealership.repositories.messaging

import com.google.firebase.firestore.FirebaseFirestore
import com.kaiandkaro.dealership.models.Message
import kotlinx.coroutines.tasks.await

class MessagingRepositoryImpl(private val firestore: FirebaseFirestore) : MessagingRepository {
    override suspend fun getMessages(conversationId: String): List<Message> {
        val snapshot = firestore.collection("conversations").document(conversationId).collection("messages")
            .orderBy("timestamp")
            .get()
            .await()
        return snapshot.toObjects(Message::class.java)
    }

    override suspend fun sendMessage(conversationId: String, message: Message) {
        firestore.collection("conversations").document(conversationId).collection("messages")
            .add(message)
            .await()
    }
}
