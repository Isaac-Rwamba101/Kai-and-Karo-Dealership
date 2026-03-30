package com.kaiandkaro.dealership

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
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
import com.kaiandkaro.dealership.ui.screens.splash.SplashScreen
import com.kaiandkaro.dealership.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

// CHANGE THIS to your actual admin email address
const val ADMIN_EMAIL = "isaacrwamba019@gmail.com"

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
    val isInitDone by authViewModel.isInitDone.collectAsState()

    val isAdmin = user?.email?.lowercase() == ADMIN_EMAIL.lowercase()
    val showDrawerAndBar = currentRoute !in listOf("splash", "login", "signup", "role_selection")

    LaunchedEffect(user, userRole, isNewUser, isInitDone) {
        if (isInitDone) {
            if (user != null) {
                if (isAdmin) {
                    if (currentRoute == "login" || currentRoute == "signup" || currentRoute == "role_selection") {
                        navController.navigate("home") { popUpTo(0) { inclusive = true } }
                    }
                } else if (userRole == "" || userRole == null || isNewUser) {
                    if (currentRoute != "role_selection" && currentRoute != "splash") {
                        navController.navigate("role_selection") { popUpTo(0) { inclusive = true } }
                    }
                } else {
                    if (currentRoute == "login" || currentRoute == "signup" || currentRoute == "role_selection") {
                        navController.navigate("home") { popUpTo(0) { inclusive = true } }
                    }
                }
            } else {
                if (currentRoute != "login" && currentRoute != "signup" && currentRoute != "splash") {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
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
                    
                    if (userRole == "seller" || isAdmin) {
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
                        icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
                        label = { Text("Conversations") },
                        selected = currentRoute == "conversations",
                        onClick = {
                            navController.navigate("conversations")
                            scope.launch { drawerState.close() }
                        }
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.SupportAgent, contentDescription = null) },
                        label = { Text("Customer Support") },
                        selected = false,
                        onClick = {
                            user?.let {
                                navController.navigate("messaging/support_${it.uid}?otherUserId=admin")
                            }
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
                    
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Profile") },
                        selected = currentRoute == "profile",
                        onClick = {
                            navController.navigate("profile")
                            scope.launch { drawerState.close() }
                        }
                    )
                    
                    if (isAdmin) {
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
                                popUpTo(0) { inclusive = true }
                            }
                            scope.launch { drawerState.close() }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        ) {
            AppNavigation(navController, authViewModel, isAdmin) {
                scope.launch { drawerState.open() }
            }
        }
    } else {
        AppNavigation(navController, authViewModel, isAdmin) {}
    }
}

@Composable
fun AppNavigation(
    navController: androidx.navigation.NavHostController,
    authViewModel: AuthViewModel,
    isAdmin: Boolean,
    onMenuClick: () -> Unit
) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("signup") { SignupScreen(navController, authViewModel) }
        composable("role_selection") { 
            RoleSelectionScreen(onRoleSelected = { role ->
                authViewModel.setRole(role)
            }) 
        }
        composable("home") { HomeScreen(navController, isAdmin, onMenuClick) }
        composable("vehicle_list") { VehicleListScreen(navController) }
        composable("add_vehicle") { AddVehicleScreen(navController) }
        composable("conversations") { ConversationListScreen(navController) }
        composable("website") { WebsiteScreen(onBackClick = { navController.popBackStack() }) }
        composable("admin") { AdminDashboardScreen(navController) }
        composable("profile") { ProfileScreen(navController, authViewModel) }
        composable(
            "messaging/{conversationId}?otherUserId={otherUserId}",
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
                navArgument("otherUserId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            val otherUserId = backStackEntry.arguments?.getString("otherUserId")
            MessagingScreen(navController, conversationId = conversationId, otherUserId = otherUserId)
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
