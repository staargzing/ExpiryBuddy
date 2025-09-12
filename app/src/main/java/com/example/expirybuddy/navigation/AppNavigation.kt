package com.example.expirybuddy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.expirybuddy.ui.screens.MainScreen
import com.example.expirybuddy.ui.screens.WelcomingScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomingScreen(
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                onLogout = {
                    navController.navigate("welcome") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
