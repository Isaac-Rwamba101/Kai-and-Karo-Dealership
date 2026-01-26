package com.kaiandkaro.dealership.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaiandkaro.dealership.models.Vehicle
import com.kaiandkaro.dealership.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    init {
        loadVehicles()
    }

    fun loadVehicles() {
        viewModelScope.launch {
            try {
                _vehicles.value = vehicleRepository.getVehicles()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun addVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            try {
                vehicleRepository.addVehicle(vehicle)
                loadVehicles()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
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
