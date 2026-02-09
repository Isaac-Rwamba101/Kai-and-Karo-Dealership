package com.kaiandkaro.dealership.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kaiandkaro.dealership.models.Vehicle
import com.kaiandkaro.dealership.ui.theme.DealershipTheme
import com.kaiandkaro.dealership.ui.viewmodels.AuthViewModel
import com.kaiandkaro.dealership.ui.viewmodels.VehicleViewModel

@Composable
fun VehicleDetailScreen(
    navController: NavController,
    vehicleId: String,
    vehicleViewModel: VehicleViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val vehiclesState by vehicleViewModel.vehicles.collectAsState()
    val vehicle = vehiclesState.find { it.id == vehicleId }
    val userRole by authViewModel.userRole.collectAsState()

    VehicleDetailContent(
        vehicle = vehicle,
        userRole = userRole,
        onMarkAsSoldClick = {
            vehicle?.let {
                val updatedVehicle = it.copy(isSold = true)
                vehicleViewModel.updateVehicle(updatedVehicle)
            }
        },
        onBackClick = { navController.popBackStack() },
        onContactSellerClick = { /* Handle navigation to chat */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailContent(
    vehicle: Vehicle?,
    userRole: String?,
    onMarkAsSoldClick: () -> Unit,
    onBackClick: () -> Unit,
    onContactSellerClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            if (vehicle != null && !vehicle.isSold) {
                Surface(
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (userRole == "seller") {
                            Button(
                                onClick = onMarkAsSoldClick,
                                modifier = Modifier.weight(1f).height(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Mark as Sold")
                            }
                        } else {
                            OutlinedIconButton(
                                onClick = { /* Toggle Favorite */ },
                                modifier = Modifier.size(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                            }
                            Button(
                                onClick = onContactSellerClick,
                                modifier = Modifier.weight(1f).height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Contact Seller")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (vehicle == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Box {
                    AsyncImage(
                        model = vehicle.imageUrl,
                        contentDescription = vehicle.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (vehicle.isSold) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ) {
                                Text("SOLD", modifier = Modifier.padding(4.dp))
                            }
                        }
                    }
                    
                    Text(
                        text = "$${vehicle.price}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = vehicle.description,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (vehicle.documentUrl.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Documents",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(vehicle.documentUrl))
                                    context.startActivity(intent)
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = vehicle.documentName.ifEmpty { "View Vehicle Document" },
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.OpenInNew, contentDescription = null)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            DetailItem(label = "Year", value = "2024")
                            DetailItem(label = "Type", value = "Sedan")
                            DetailItem(label = "Fuel", value = "Hybrid")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun VehicleDetailScreenPreview() {
    val sampleVehicle = Vehicle(
        id = "1",
        name = "Tesla Model S",
        price = 89990.0,
        description = "A high-performance electric sedan with cutting-edge technology and exceptional range.",
        imageUrl = "https://example.com/tesla.jpg",
        isSold = false,
        documentUrl = "https://example.com/doc.pdf",
        documentName = "Registration_Docs.pdf"
    )
    DealershipTheme {
        VehicleDetailContent(
            vehicle = sampleVehicle,
            userRole = "buyer",
            onMarkAsSoldClick = {},
            onBackClick = {},
            onContactSellerClick = {}
        )
    }
}
