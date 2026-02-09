package com.kaiandkaro.dealership.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.kaiandkaro.dealership.models.Vehicle
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : VehicleRepository {

    private val vehicleCollection = firestore.collection("vehicles")

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
            // Handle error
        }
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        try {
            vehicleCollection.document(vehicle.id).set(vehicle).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    override suspend fun deleteVehicle(vehicleId: String) {
        try {
            vehicleCollection.document(vehicleId).delete().await()
        } catch (e: Exception) {
            // Handle error
        }
    }
}
