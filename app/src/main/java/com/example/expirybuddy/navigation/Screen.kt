package com.example.expirybuddy.navigation

// Menyimpan semua route screen dalam bentuk sealed class untuk navigasi yang aman dan terstruktur
sealed class Screen(val route: String) {

    // Screen untuk beranda/home
    object Home : Screen("home_screen")

    // Screen untuk daftar makanan
    object FoodList : Screen("food_list_screen")

    // Screen untuk pengingat kedaluwarsa
    object Reminders : Screen("reminders_screen")

    // Screen untuk profil pengguna
    object Profile : Screen("profile_screen")
}
