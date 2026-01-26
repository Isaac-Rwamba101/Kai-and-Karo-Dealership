package com.kaiandkaro.dealership.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaiandkaro.dealership.data.AuthRepositoryImpl
import com.kaiandkaro.dealership.data.VehicleRepositoryImpl
import com.kaiandkaro.dealership.data.conversation.ConversationRepositoryImpl
import com.kaiandkaro.dealership.data.messaging.MessagingRepositoryImpl
import com.kaiandkaro.dealership.repositories.AuthRepository
import com.kaiandkaro.dealership.repositories.VehicleRepository
import com.kaiandkaro.dealership.repositories.conversation.ConversationRepository
import com.kaiandkaro.dealership.repositories.messaging.MessagingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideAuthRepositoryImpl(auth: FirebaseAuth, firestore: FirebaseFirestore): AuthRepositoryImpl {
        return AuthRepositoryImpl(auth, firestore)
    }

    @Provides
    @Singleton
    fun provideVehicleRepository(impl: VehicleRepositoryImpl): VehicleRepository = impl

    @Provides
    @Singleton
    fun provideVehicleRepositoryImpl(firestore: FirebaseFirestore): VehicleRepositoryImpl {
        return VehicleRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideMessagingRepository(impl: MessagingRepositoryImpl): MessagingRepository = impl

    @Provides
    @Singleton
    fun provideMessagingRepositoryImpl(firestore: FirebaseFirestore): MessagingRepositoryImpl {
        return MessagingRepositoryImpl(firestore)
    }

    @Provides
    @Singleton

    fun provideConversationRepository(impl: ConversationRepositoryImpl): ConversationRepository = impl

    @Provides
    @Singleton
    fun provideConversationRepositoryImpl(firestore: FirebaseFirestore, authRepository: AuthRepository): ConversationRepositoryImpl {
        return ConversationRepositoryImpl(firestore, authRepository)
    }
}
