package com.kaiandkaro.dealership

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kaiandkaro.dealership.ui.screens.*

@Composable
fun DealershipApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("vehicle_list") { VehicleListScreen(navController) }
        composable("add_vehicle") { AddVehicleScreen(navController) }
        composable(
            "vehicle_detail/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            VehicleDetailScreen(navController, vehicleId)
        }
    }
}
