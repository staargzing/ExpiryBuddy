package com.example.expirybuddy.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.room.Update
import com.example.expirybuddy.data.FoodItem


@Dao
interface FoodItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(item: FoodItem)

    @Delete
    suspend fun deleteFoodItem(item: FoodItem)

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getFoodItemById(id: Int): FoodItem?

    @Query("SELECT * FROM food_items ORDER BY expiryDate ASC")
    fun getAllFoodItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE expiryDate <= :date ORDER BY expiryDate ASC")
    fun getExpiringItems(date: Long): Flow<List<FoodItem>>

    @Update
    suspend fun updateFoodItem(item: FoodItem)
}