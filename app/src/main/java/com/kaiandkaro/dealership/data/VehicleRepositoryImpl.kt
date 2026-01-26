package com.kaiandkaro.dealership.data

import com.google.firebase.firestore.FirebaseFirestore
import com.kaiandkaro.dealership.models.Vehicle
import com.kaiandkaro.dealership.repositories.VehicleRepository
import kotlinx.coroutines.tasks.await

class VehicleRepositoryImpl(
    private val firestore: FirebaseFirestore
) : VehicleRepository {

    override suspend fun getVehicles(): List<Vehicle> {
        return firestore.collection("vehicles").get().await().toObjects(Vehicle::class.java)
    }

    override suspend fun addVehicle(vehicle: Vehicle) {
        firestore.collection("vehicles").add(vehicle).await()
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        firestore.collection("vehicles").document(vehicle.id).set(vehicle).await()
    }

    override suspend fun deleteVehicle(vehicleId: String) {
        firestore.collection("vehicles").document(vehicleId).delete().await()
    }
}
