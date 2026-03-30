package com.kaiandkaro.dealership.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.kaiandkaro.dealership.models.Vehicle
import com.kaiandkaro.dealership.repositories.VehicleRepository
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: Storage
) : VehicleRepository {

    private val vehicleCollection = firestore.collection("vehicles")
    private val favoritesCollection = firestore.collection("favorites")

    override suspend fun getVehicles(): List<Vehicle> {
        return try {
            vehicleCollection.get().await().toObjects(Vehicle::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addVehicle(vehicle: Vehicle) {
        try {
            val documentReference = if (vehicle.id.isEmpty()) {
                vehicleCollection.document()
            } else {
                vehicleCollection.document(vehicle.id)
            }
            val vehicleWithId = vehicle.copy(id = documentReference.id)
            documentReference.set(vehicleWithId).await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        try {
            vehicleCollection.document(vehicle.id).set(vehicle).await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteVehicle(vehicleId: String) {
        try {
            vehicleCollection.document(vehicleId).delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun uploadImage(bytes: ByteArray, fileName: String): String {
        val bucket = storage.from("vehicle-images")
        bucket.upload(fileName, bytes) {
            upsert = true
        }
        return bucket.publicUrl(fileName)
    }

    override suspend fun uploadDocument(bytes: ByteArray, fileName: String): String {
        val bucket = storage.from("vehicle-documents")
        bucket.upload(fileName, bytes) {
            upsert = true
        }
        return bucket.publicUrl(fileName)
    }

    override suspend fun toggleFavorite(userId: String, vehicleId: String) {
        val query = favoritesCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("vehicleId", vehicleId)
            .get()
            .await()

        if (query.isEmpty) {
            favoritesCollection.add(mapOf("userId" to userId, "vehicleId" to vehicleId)).await()
        } else {
            for (doc in query.documents) {
                doc.reference.delete().await()
            }
        }
    }

    override fun isFavorited(userId: String, vehicleId: String): Flow<Boolean> {
        return favoritesCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("vehicleId", vehicleId)
            .snapshots()
            .map { !it.isEmpty }
    }
}
