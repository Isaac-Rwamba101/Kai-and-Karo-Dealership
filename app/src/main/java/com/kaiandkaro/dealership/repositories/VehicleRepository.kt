package com.kaiandkaro.dealership.repositories

import com.kaiandkaro.dealership.models.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    suspend fun getVehicles(): List<Vehicle>
    suspend fun addVehicle(vehicle: Vehicle)
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteVehicle(vehicleId: String)
    suspend fun uploadImage(bytes: ByteArray, fileName: String): String
    suspend fun uploadDocument(bytes: ByteArray, fileName: String): String
    
    // Favorites
    suspend fun toggleFavorite(userId: String, vehicleId: String)
    fun isFavorited(userId: String, vehicleId: String): Flow<Boolean>
}
