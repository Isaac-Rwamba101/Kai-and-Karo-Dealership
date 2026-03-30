package com.kaiandkaro.dealership.di

import com.google.firebase.firestore.FirebaseFirestore
import com.kaiandkaro.dealership.data.VehicleRepositoryImpl
import com.kaiandkaro.dealership.repositories.VehicleRepository
import com.kaiandkaro.dealership.repositories.conversation.ConversationRepository
import com.kaiandkaro.dealership.repositories.conversation.ConversationRepositoryImpl
import com.kaiandkaro.dealership.repositories.messaging.MessagingRepository
import com.kaiandkaro.dealership.repositories.messaging.MessagingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideVehicleRepository(
        firestore: FirebaseFirestore,
        storage: Storage
    ): VehicleRepository {
        return VehicleRepositoryImpl(firestore, storage)
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
}
