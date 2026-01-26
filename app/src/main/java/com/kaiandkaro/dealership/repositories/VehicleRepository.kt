package com.kaiandkaro.dealership.repositories

import com.kaiandkaro.dealership.models.Vehicle

interface VehicleRepository {
    suspend fun getVehicles(): List<Vehicle>
    suspend fun addVehicle(vehicle: Vehicle)
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteVehicle(vehicleId: String)
}
