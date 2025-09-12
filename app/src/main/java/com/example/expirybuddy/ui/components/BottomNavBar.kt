package com.example.expirybuddy.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.expirybuddy.navigation.BottomNavItem

// Warna hijau utama
val GreenColor = Color(0xFF4CAF50)

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar(
        containerColor = GreenColor, // Background hijau
        contentColor = Color.White   // Warna default
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        BottomNavItem.items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = MaterialTheme.typography.labelSmall.fontSize
                    )
                },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color(0xFFE8F5E9),
                    unselectedTextColor = Color(0xFFE8F5E9),
                    indicatorColor = Color(0xFF388E3C)
                )
            )
        }
    }
}
