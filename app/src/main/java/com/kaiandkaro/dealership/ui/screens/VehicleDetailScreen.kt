package com.kaiandkaro.dealership.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kaiandkaro.dealership.ui.viewmodels.AuthViewModel
import com.kaiandkaro.dealership.ui.viewmodels.VehicleViewModel

@Composable
fun VehicleDetailScreen(
    navController: NavController,
    vehicleId: String,
    vehicleViewModel: VehicleViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val vehicle by vehicleViewModel.vehicles.collectAsState()
        .let { vehiclesState -> vehiclesState.value.find { it.id == vehicleId } }
    val userRole by authViewModel.userRole.collectAsState()

    vehicle?.let {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = it.name, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Price: $${it.price}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            if (userRole == "seller" && !it.isSold) {
                Button(onClick = {
                    val updatedVehicle = it.copy(isSold = true)
                    vehicleViewModel.updateVehicle(updatedVehicle)
                }) {
                    Text("Mark as Sold")
                }
            }
        }
    }
}
