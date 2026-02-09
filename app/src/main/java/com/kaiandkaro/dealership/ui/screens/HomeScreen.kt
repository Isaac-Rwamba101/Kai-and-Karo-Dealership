package com.kaiandkaro.dealership.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kaiandkaro.dealership.ui.theme.DealershipTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onMenuClick: () -> Unit
) {
    HomeContent(
        onMenuClick = onMenuClick,
        onNavigateToVehicles = { navController.navigate("vehicle_list") },
        onNavigateToAddVehicle = { navController.navigate("add_vehicle") },
        onNavigateToConversations = { navController.navigate("conversations") },
        onNavigateToWebsite = { navController.navigate("website") },
        onNavigateToAdmin = { navController.navigate("admin") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    onMenuClick: () -> Unit,
    onNavigateToVehicles: () -> Unit,
    onNavigateToAddVehicle: () -> Unit,
    onNavigateToConversations: () -> Unit,
    onNavigateToWebsite: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kai & Karo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Welcome to the Dealership",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            val homeItems = listOf(
                HomeWidget("Browse Cars", Icons.Default.DirectionsCar, onNavigateToVehicles),
                HomeWidget("Sell Your Car", Icons.Default.AddCircle, onNavigateToAddVehicle),
                HomeWidget("Messages", Icons.Default.Chat, onNavigateToConversations),
                HomeWidget("Our Website", Icons.Default.Language, onNavigateToWebsite),
                HomeWidget("Admin Panel", Icons.Default.AdminPanelSettings, onNavigateToAdmin),
                HomeWidget("Profile", Icons.Default.Person, {})
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(homeItems) { item ->
                    HomeCard(item)
                }
            }
        }
    }
}

data class HomeWidget(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun HomeCard(item: HomeWidget) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .clickable(onClick = item.onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DealershipTheme {
        HomeContent({}, {}, {}, {}, {}, {})
    }
}
