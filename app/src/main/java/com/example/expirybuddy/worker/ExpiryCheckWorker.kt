package com.example.expirybuddy.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.expirybuddy.MyApplication
import com.example.expirybuddy.data.FoodItem
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class ExpiryCheckWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Mengambil DAO dari Application class
        val application = applicationContext as MyApplication
        val dao = application.database.foodItemDao()

        return try {
            // Mengambil semua item dari database
            val allItems = dao.getAllFoodItems().first()

            // Filter item yang kadaluarsa TEPAT 3 hari dari sekarang
            val expiringSoonItems = allItems.filter {
                val daysLeft = TimeUnit.MILLISECONDS.toDays(it.expiryDate - System.currentTimeMillis())
                // Kita cari yang sisa harinya 2 (artinya H-3, karena hari ke-0 adalah besok)
                daysLeft == 2L
            }

            // Jika ada item yang memenuhi kriteria, tampilkan notifikasi
            if (expiringSoonItems.isNotEmpty()) {
                val foodNames = expiringSoonItems.joinToString(separator = ", ") { it.name }
                NotificationHelper(applicationContext).showNotification(
                    title = "Makanan Segera Kadaluarsa!",
                    content = "$foodNames akan kadaluarsa dalam 3 hari."
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}