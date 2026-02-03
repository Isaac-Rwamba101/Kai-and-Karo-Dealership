package com.kaiandkaro.dealership.data.messaging

import com.google.firebase.firestore.FirebaseFirestore
import com.kaiandkaro.dealership.models.Conversation
import com.kaiandkaro.dealership.models.Message
import com.kaiandkaro.dealership.repositories.messaging.MessagingRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessagingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MessagingRepository {

    override fun getConversation(conversationId: String): Flow<Conversation> = callbackFlow {
        val docRef = firestore.collection("conversations").document(conversationId)

        val subscription = docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val conversation = snapshot.toObject(Conversation::class.java)
                if (conversation != null) {
                    trySend(conversation).isSuccess
                }
            }
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun sendMessage(conversationId: String, message: Message) {
        firestore.collection("conversations").document(conversationId)
            .collection("messages").add(message).await()
    }
}
