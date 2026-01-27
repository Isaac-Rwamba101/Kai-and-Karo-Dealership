package com.kaiandkaro.dealership.repositories

import com.google.firebase.auth.FirebaseUser
import com.kaiandkaro.dealership.models.User

interface AuthRepository {
    suspend fun getCurrentUser(): FirebaseUser?
    suspend fun signIn(email: String, password: String): FirebaseUser?
    suspend fun signUp(name: String, email: String, password: String): FirebaseUser?
    suspend fun signOut()
    suspend fun getUserData(uid: String): User?
    suspend fun getUserRole(uid: String): String?
    suspend fun updateUserFCMToken(uid: String, token: String)
}
