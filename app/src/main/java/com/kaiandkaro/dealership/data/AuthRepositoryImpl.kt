package com.kaiandkaro.dealership.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.kaiandkaro.dealership.models.User
import com.kaiandkaro.dealership.repositories.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun getCurrentUser(): FirebaseUser? = auth.currentUser

    override suspend fun signIn(email: String, password: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    override suspend fun signUp(name: String, email: String, password: String): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user
        if (firebaseUser != null) {
            val user = User(uid = firebaseUser.uid, name = name, email = email)
            firestore.collection("users").document(firebaseUser.uid).set(user).await()
        }
        return firebaseUser
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun getUserData(uid: String): User? {
        return firestore.collection("users").document(uid).get().await().toObject(User::class.java)
    }

    override suspend fun getUserRole(uid: String): String? {
        val document = firestore.collection("users").document(uid).get().await()
        return if (document.exists()) {
            document.getString("role") ?: "buyer"
        } else {
            "buyer"
        }
    }
}
