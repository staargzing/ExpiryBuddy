package com.example.expirybuddy.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expirybuddy.data.FoodItem
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Composable
fun FoodSearchItem(item: FoodItem) {
    val hariIni = LocalDate.now()
    val tanggalKadaluwarsa = Instant.ofEpochMilli(item.expiryDate)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val sisaHari = ChronoUnit.DAYS.between(hariIni, tanggalKadaluwarsa)

    val (warnaLatar, status) = when {
        sisaHari < 0 -> Color(0xFFBDBDBD) to "Sudah kadaluarsa"
        sisaHari == 0L -> Color(0xFFE57373) to "Kadaluarsa hari ini"
        sisaHari == 1L -> Color(0xFFFFCDD2) to "Kadaluarsa besok"
        sisaHari <= 3 -> Color(0xFFFFEB3B) to "Kadaluarsa dalam $sisaHari hari"
        else -> Color(0xFF81C784) to "Masih aman ($sisaHari hari lagi)"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = warnaLatar)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(item.name, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(status, fontSize = 14.sp, color = Color.Black)
        }
    }
}
