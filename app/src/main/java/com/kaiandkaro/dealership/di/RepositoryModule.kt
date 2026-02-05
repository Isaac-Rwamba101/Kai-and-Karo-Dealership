package com.kaiandkaro.dealership.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaiandkaro.dealership.repositories.AuthRepository
import com.kaiandkaro.dealership.repositories.AuthRepositoryImpl
import com.kaiandkaro.dealership.repositories.VehicleRepository
import com.kaiandkaro.dealership.repositories.VehicleRepositoryImpl
import com.kaiandkaro.dealership.repositories.conversation.ConversationRepository
import com.kaiandkaro.dealership.repositories.conversation.ConversationRepositoryImpl
import com.kaiandkaro.dealership.repositories.messaging.MessagingRepository
import com.kaiandkaro.dealership.repositories.messaging.MessagingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth, firestore: FirebaseFirestore): AuthRepository {
        return AuthRepositoryImpl(auth, firestore)
    }

    @Provides
    @Singleton
    fun provideVehicleRepository(firestore: FirebaseFirestore): VehicleRepository {
        return VehicleRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideConversationRepository(firestore: FirebaseFirestore): ConversationRepository {
        return ConversationRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideMessagingRepository(firestore: FirebaseFirestore): MessagingRepository {
        return MessagingRepositoryImpl(firestore)
    }
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}
