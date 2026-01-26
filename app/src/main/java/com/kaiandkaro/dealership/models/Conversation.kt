package com.kaiandkaro.dealership.models

import com.google.firebase.firestore.DocumentId

data class Conversation(
    @DocumentId
    val id: String = "",
    val participants: List<String> = emptyList(),
    val messages: List<Message> = emptyList()
)
