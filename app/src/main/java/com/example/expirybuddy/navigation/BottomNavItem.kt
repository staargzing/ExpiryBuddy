package com.example.expirybuddy.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Home : BottomNavItem("Home", Icons.Default.Home, "home")
    object FoodList : BottomNavItem("Food List", Icons.Default.List, "foodlist")
    object Reminders : BottomNavItem("Reminders", Icons.Default.Notifications, "reminders")
    object Profile : BottomNavItem("Profile", Icons.Default.AccountCircle, "profile")

    companion object {
        val items = listOf(
            Home,
            FoodList,
            Reminders,
            Profile
        )
    }
}
