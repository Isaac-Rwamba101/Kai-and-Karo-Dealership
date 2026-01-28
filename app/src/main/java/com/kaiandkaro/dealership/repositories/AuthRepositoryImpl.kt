package com.kaiandkaro.dealership.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.kaiandkaro.dealership.models.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun signIn(email: String, password: String): FirebaseUser? {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    override suspend fun signUp(name: String, email: String, password: String): FirebaseUser? {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user
        if (user != null) {
            val userProfile = User(
                uid = user.uid,
                name = name,
                email = email,
                role = "customer"
            )
            firestore.collection("users").document(user.uid).set(userProfile).await()
        }
        return user
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun getUserData(uid: String): User? {
        val document = firestore.collection("users").document(uid).get().await()
        return document.toObject(User::class.java)
    }

    override suspend fun getUserRole(uid: String): String? {
        val user = getUserData(uid)
        return user?.role
    }

    override suspend fun updateUserFCMToken(uid: String, token: String) {
        firestore.collection("users").document(uid).update("fcmToken", token).await()
    }
}
