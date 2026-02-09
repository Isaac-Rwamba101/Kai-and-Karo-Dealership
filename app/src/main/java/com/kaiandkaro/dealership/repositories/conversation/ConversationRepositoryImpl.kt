package com.kaiandkaro.dealership.repositories.conversation

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.kaiandkaro.dealership.models.Conversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ConversationRepository {
    override fun getConversations(): Flow<List<Conversation>> {
        return firestore.collection("conversations")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Conversation::class.java)
            }
    }
}
