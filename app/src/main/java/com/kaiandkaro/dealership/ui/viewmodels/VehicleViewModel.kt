package com.kaiandkaro.dealership.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaiandkaro.dealership.models.Vehicle
import com.kaiandkaro.dealership.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isAdding = MutableStateFlow(false)
    val isAdding: StateFlow<Boolean> = _isAdding

    init {
        loadVehicles()
    }

    fun loadVehicles() {
        viewModelScope.launch {
            try {
                _vehicles.value = vehicleRepository.getVehicles()
            } catch (e: Exception) {
                _error.value = "Failed to load cars: ${e.localizedMessage}"
            }
        }
    }

    fun addVehicle(
        name: String,
        make: String,
        model: String,
        year: String,
        type: String,
        fuel: String,
        price: Double,
        description: String,
        imageUrl: String,
        documentUrl: String,
        documentName: String,
        sellerId: String
    ) {
        viewModelScope.launch {
            _isAdding.value = true
            _error.value = null
            try {
                val vehicle = Vehicle(
                    name = name,
                    make = make,
                    model = model,
                    year = year,
                    type = type,
                    fuel = fuel,
                    price = price,
                    description = description,
                    imageUrl = imageUrl,
                    documentUrl = documentUrl,
                    documentName = documentName,
                    sellerId = sellerId
                )

                vehicleRepository.addVehicle(vehicle)
                loadVehicles()
            } catch (e: Exception) {
                _error.value = "Failed to add car: ${e.localizedMessage}"
            } finally {
                _isAdding.value = false
            }
        }
    }

    suspend fun uploadImage(bytes: ByteArray, fileName: String): String? {
        return try {
            vehicleRepository.uploadImage(bytes, fileName)
        } catch (e: Exception) {
            _error.value = "Failed to upload image: ${e.localizedMessage}"
            null
        }
    }

    suspend fun uploadDocument(bytes: ByteArray, fileName: String): String? {
        return try {
            vehicleRepository.uploadDocument(bytes, fileName)
        } catch (e: Exception) {
            _error.value = "Failed to upload document: ${e.localizedMessage}"
            null
        }
    }

    fun toggleFavorite(userId: String, vehicleId: String) {
        viewModelScope.launch {
            try {
                vehicleRepository.toggleFavorite(userId, vehicleId)
            } catch (e: Exception) {
                _error.value = "Failed to update favorites"
            }
        }
    }

    fun isFavorited(userId: String, vehicleId: String): Flow<Boolean> {
        return vehicleRepository.isFavorited(userId, vehicleId)
    }

    fun clearError() {
        _error.value = null
    }

    fun updateVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            try {
                vehicleRepository.updateVehicle(vehicle)
                loadVehicles()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            try {
                vehicleRepository.deleteVehicle(vehicleId)
                loadVehicles()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}
