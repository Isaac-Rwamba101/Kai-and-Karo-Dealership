package com.kaiandkaro.dealership

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kaiandkaro.dealership.ui.screens.*
import com.kaiandkaro.dealership.ui.screens.admin.AdminDashboardScreen
import com.kaiandkaro.dealership.ui.screens.login.LoginScreen
import com.kaiandkaro.dealership.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun DealershipApp(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val user by authViewModel.user.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()
    val isNewUser by authViewModel.isNewUser.collectAsState()

    val showDrawerAndBar = currentRoute !in listOf("login", "signup", "role_selection")

    LaunchedEffect(user, userRole, isNewUser) {
        if (user != null) {
            if (isNewUser) {
                if (currentRoute != "role_selection") {
                    navController.navigate("role_selection") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            } else if (userRole != null) {
                if (currentRoute == "login" || currentRoute == "signup" || currentRoute == "role_selection") {
                    navController.navigate("home") {
                        popUpTo(0)
                    }
                }
            }
        }
    }

    if (showDrawerAndBar) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Kai & Karo",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Home") },
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home")
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.DirectionsCar, contentDescription = null) },
                        label = { Text("Inventory") },
                        selected = currentRoute == "vehicle_list",
                        onClick = {
                            navController.navigate("vehicle_list")
                            scope.launch { drawerState.close() }
                        }
                    )
                    
                    if (userRole == "seller" || userRole == "admin") {
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                            label = { Text("Sell Car") },
                            selected = currentRoute == "add_vehicle",
                            onClick = {
                                navController.navigate("add_vehicle")
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                    
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                        label = { Text("Conversations") },
                        selected = currentRoute == "conversations",
                        onClick = {
                            navController.navigate("conversations")
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Language, contentDescription = null) },
                        label = { Text("Website") },
                        selected = currentRoute == "website",
                        onClick = {
                            navController.navigate("website")
                            scope.launch { drawerState.close() }
                        }
                    )
                    
                    if (userRole == "admin") {
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                            label = { Text("Admin Panel") },
                            selected = currentRoute == "admin",
                            onClick = {
                                navController.navigate("admin")
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                        label = { Text("Logout") },
                        selected = false,
                        onClick = {
                            authViewModel.signOut()
                            navController.navigate("login") {
                                popUpTo(0)
                            }
                            scope.launch { drawerState.close() }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        ) {
            AppNavigation(navController) {
                scope.launch { drawerState.open() }
            }
        }
    } else {
        AppNavigation(navController) {}
    }
}

@Composable
fun AppNavigation(
    navController: androidx.navigation.NavHostController, 
    onMenuClick: () -> Unit
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("role_selection") { 
            RoleSelectionScreen(onRoleSelected = { role ->
                authViewModel.setRole(role)
            }) 
        }
        composable("home") { HomeScreen(navController, onMenuClick) }
        composable("vehicle_list") { VehicleListScreen(navController) }
        composable("add_vehicle") { AddVehicleScreen(navController) }
        composable("conversations") { ConversationListScreen(navController) }
        composable("website") { WebsiteScreen(onBackClick = { navController.popBackStack() }) }
        composable("admin") { AdminDashboardScreen(navController) }
        composable(
            "messaging/{conversationId}",
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            MessagingScreen(navController, conversationId = conversationId)
        }
        composable(
            "vehicle_detail/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            VehicleDetailScreen(navController, vehicleId)
        }
    }
}
