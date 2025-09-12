package com.example.expirybuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expirybuddy.data.FoodItem
import com.example.expirybuddy.data.local.FoodItemDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class FoodViewModel(private val dao: FoodItemDao) : ViewModel() {

    // Semua item makanan
    val allFoodItems: StateFlow<List<FoodItem>> = dao.getAllFoodItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // Jumlah item makanan (buat ProfileScreen)
    val foodCount: StateFlow<Int> = allFoodItems
        .map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = 0
        )

    // Item yang SUDAH expired
    val expiredItems: StateFlow<List<FoodItem>> = allFoodItems.map { items ->
        items.filter {
            val daysLeft = TimeUnit.MILLISECONDS.toDays(it.expiryDate - System.currentTimeMillis())
            daysLeft < 0
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )
    // Item yang kadaluarsa hari ini
    val reminderTodayItems: StateFlow<List<FoodItem>> = allFoodItems.map { items ->
        items.filter {
            val daysLeft = TimeUnit.MILLISECONDS.toDays(it.expiryDate - System.currentTimeMillis())
            daysLeft == 0L
        }.sortedBy { it.expiryDate }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    // Item yang kadaluarsa dalam ≤ 3 hari
    val expiringItems: StateFlow<List<FoodItem>> = allFoodItems.map { items ->
        items.filter {
            val daysLeft = TimeUnit.MILLISECONDS.toDays(it.expiryDate - System.currentTimeMillis())
            daysLeft in 0..2
        }.sortedBy { it.expiryDate }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    fun addFoodItem(name: String, expiryDate: Long, category: String) {
        viewModelScope.launch {
            val newItem = FoodItem(name = name, expiryDate = expiryDate, category = category)
            dao.insertFoodItem(newItem)
        }
    }

    fun updateFoodItem(item: FoodItem) {
        viewModelScope.launch {
            dao.updateFoodItem(item)
        }
    }

    fun deleteFoodItem(item: FoodItem) {
        viewModelScope.launch {
            dao.deleteFoodItem(item)
        }
    }
}
