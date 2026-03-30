package com.kaiandkaro.dealership.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kaiandkaro.dealership.models.Vehicle
import com.kaiandkaro.dealership.ui.theme.DealershipTheme
import com.kaiandkaro.dealership.ui.viewmodels.AuthViewModel
import com.kaiandkaro.dealership.ui.viewmodels.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListScreen(
    navController: NavController,
    vehicleViewModel: VehicleViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val vehicles by vehicleViewModel.vehicles.collectAsState()
    val error by vehicleViewModel.error.collectAsState()
    val currentUser by authViewModel.user.collectAsState()

    VehicleListContent(
        vehicles = vehicles,
        error = error,
        currentUserId = currentUser?.uid ?: "",
        onAddVehicleClick = { navController.navigate("add_vehicle") },
        onVehicleClick = { vehicleId -> navController.navigate("vehicle_detail/$vehicleId") },
        onMessageClick = { sellerId -> 
            // In a real app, you'd create a conversation ID first.
            // For now, we'll navigate to messaging with the sellerId.
            navController.navigate("messaging/$sellerId") 
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListContent(
    vehicles: List<Vehicle>,
    error: String?,
    currentUserId: String,
    onAddVehicleClick: () -> Unit,
    onVehicleClick: (String) -> Unit,
    onMessageClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Inventory", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddVehicleClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Vehicle") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            error?.let {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (vehicles.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No vehicles found", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(vehicles) { vehicle ->
                        VehicleListItem(
                            vehicle = vehicle,
                            showChatIcon = vehicle.sellerId != currentUserId,
                            onMessageClick = { onMessageClick(vehicle.sellerId) }
                        ) {
                            onVehicleClick(vehicle.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleListItem(
    vehicle: Vehicle, 
    showChatIcon: Boolean,
    onMessageClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = vehicle.imageUrl,
                    contentDescription = vehicle.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                if (showChatIcon) {
                    FilledIconButton(
                        onClick = onMessageClick,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Message Seller")
                    }
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = vehicle.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${vehicle.price}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(onClick = { }, label = { Text(vehicle.year) })
                    SuggestionChip(onClick = { }, label = { Text(vehicle.type) })
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = vehicle.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VehicleListScreenPreview() {
    val sampleVehicles = listOf(
        Vehicle(
            id = "1", 
            name = "Toyota Camry 2024", 
            price = 25000.0, 
            description = "Reliable and fuel-efficient sedan.", 
            imageUrl = "https://example.com/camry.jpg",
            year = "2024",
            type = "Sedan"
        ),
        Vehicle(
            id = "2", 
            name = "Ford F-150", 
            price = 45000.0, 
            description = "Powerful pickup truck for all your needs.", 
            imageUrl = "https://example.com/f150.jpg",
            year = "2023",
            type = "Truck"
        )
    )
    DealershipTheme {
        VehicleListContent(
            vehicles = sampleVehicles,
            error = null,
            currentUserId = "user123",
            onAddVehicleClick = {},
            onVehicleClick = {},
            onMessageClick = {}
        )
    }
}
