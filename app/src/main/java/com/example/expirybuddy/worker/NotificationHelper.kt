package com.example.expirybuddy.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.expirybuddy.R // Pastikan R di-import dari package aplikasimu

class NotificationHelper(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "expiry_reminder_channel"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Buat channel notifikasi (hanya untuk Android Oreo ke atas)
        val channel = NotificationChannel(
            channelId,
            "Expiry Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifikasi untuk item yang akan kadaluarsa"
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(title: String, content: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ganti dengan ikon notifikasimu
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Tampilkan notifikasi dengan ID unik
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}