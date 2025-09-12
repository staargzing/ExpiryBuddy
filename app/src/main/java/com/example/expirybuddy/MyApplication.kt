package com.example.expirybuddy

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.expirybuddy.data.local.AppDatabase
import com.example.expirybuddy.worker.ExpiryCheckWorker
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    // Properti untuk mengakses database dari seluruh aplikasi
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val dao by lazy { database.foodItemDao() }

    // Fungsi onCreate() akan dipanggil saat aplikasi pertama kali dibuat
    override fun onCreate() {
        super.onCreate()
        // Panggil fungsi untuk menjadwalkan pekerjaan background harian
        scheduleDailyWorker()
    }

    private fun scheduleDailyWorker() {
        // Membuat aturan/batasan untuk pekerjaan
        val constraints = Constraints.Builder()
            // Contoh: hanya berjalan jika ada koneksi internet
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Membuat permintaan pekerjaan yang akan berulang setiap 24 jam
        val periodicRequest = PeriodicWorkRequestBuilder<ExpiryCheckWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        // Mendaftarkan pekerjaan ke WorkManager sistem Android
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_expiry_check", // Nama unik untuk pekerjaan ini
            ExistingPeriodicWorkPolicy.KEEP, // Jika sudah ada, jangan buat yang baru
            periodicRequest
        )
    }
}