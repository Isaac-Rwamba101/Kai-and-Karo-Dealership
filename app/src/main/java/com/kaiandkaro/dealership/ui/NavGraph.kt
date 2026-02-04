package com.kaiandkaro.dealership.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaiandkaro.dealership.ui.screens.ConversationListScreen
import com.kaiandkaro.dealership.ui.screens.MessagingScreen
import com.kaiandkaro.dealership.ui.screens.login.LoginScreen
import com.kaiandkaro.dealership.ui.screens.signup.SignupScreen
import com.kaiandkaro.dealership.ui.screens.vehicles.VehiclesScreen
import com.kaiandkaro.dealership.ui.screens.admin.AdminDashboardScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("vehicles") { VehiclesScreen(navController) }
        composable("admin") { AdminDashboardScreen(navController) }
        composable("conversations") { ConversationListScreen(navController) }
        composable("messaging/{conversationId}") { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            MessagingScreen(navController, conversationId = conversationId)
        }
    }
}