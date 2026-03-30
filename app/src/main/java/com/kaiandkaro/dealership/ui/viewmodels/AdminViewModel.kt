package com.kaiandkaro.dealership.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class AdminStats(
    val totalVehicles: Int = 0,
    val soldVehicles: Int = 0,
    val totalFavorites: Int = 0,
    val totalUsers: Int = 0
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _stats = MutableStateFlow(AdminStats())
    val stats: StateFlow<AdminStats> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val vehicles = firestore.collection("vehicles").get().await()
                val favorites = firestore.collection("favorites").get().await()
                val users = firestore.collection("users").get().await()

                val total = vehicles.size()
                val sold = vehicles.documents.count { it.getBoolean("isSold") == true }
                val favs = favorites.size()
                val userCount = users.size()

                _stats.value = AdminStats(
                    totalVehicles = total,
                    soldVehicles = sold,
                    totalFavorites = favs,
                    totalUsers = userCount
                )
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
