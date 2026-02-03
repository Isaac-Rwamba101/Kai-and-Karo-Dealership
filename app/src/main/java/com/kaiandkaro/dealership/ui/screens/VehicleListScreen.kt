package com.kaiandkaro.dealership.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kaiandkaro.dealership.models.Vehicle
import com.kaiandkaro.dealership.ui.viewmodels.VehicleViewModel

@Composable
fun VehicleListScreen(
    navController: NavController,
    vehicleViewModel: VehicleViewModel = hiltViewModel()
) {
    val vehicles by vehicleViewModel.vehicles.collectAsState()
    val error by vehicleViewModel.error.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_vehicle") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Vehicle")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            LazyColumn {
                items(vehicles) { vehicle ->
                    VehicleListItem(vehicle = vehicle) {
                        navController.navigate("vehicle_detail/${vehicle.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleListItem(vehicle: Vehicle, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column {
            AsyncImage(
                model = vehicle.imageUrl,
                contentDescription = vehicle.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = vehicle.name, style = MaterialTheme.typography.headlineSmall)
                Text(text = "$${vehicle.price}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
