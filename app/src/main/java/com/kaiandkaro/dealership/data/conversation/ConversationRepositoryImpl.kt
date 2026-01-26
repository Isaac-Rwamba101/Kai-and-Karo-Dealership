package com.kaiandkaro.dealership.data.conversation

import com.google.firebase.firestore.FirebaseFirestore
import com.kaiandkaro.dealership.models.Conversation
import com.kaiandkaro.dealership.repositories.AuthRepository
import com.kaiandkaro.dealership.repositories.conversation.ConversationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : ConversationRepository {

    override fun getConversations(): Flow<List<Conversation>> = callbackFlow {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser == null) {
            close(Exception("User not logged in"))
            return@callbackFlow
        }

        val subscription = firestore.collection("conversations")
            .whereArrayContains("participants", currentUser.uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val conversations = snapshot.toObjects(Conversation::class.java)
                    trySend(conversations).isSuccess
                }
            }

        awaitClose { subscription.remove() }
    }
}
