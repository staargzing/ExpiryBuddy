package com.example.expirybuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expirybuddy.MyApplication
import com.example.expirybuddy.data.FoodItem
import com.example.expirybuddy.viewmodel.FoodViewModel
import com.example.expirybuddy.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen() {
    val application = LocalContext.current.applicationContext as MyApplication
    val viewModel: FoodViewModel = viewModel(
        factory = ViewModelFactory(application.database.foodItemDao())
    )

    val allItems by viewModel.allFoodItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = {
                Text(
                    "REMINDERS !",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black
            )
        )

        if (allItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada makanan yang ditambahkan.")
            }
        } else {
            ReminderStatusLegend()
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)

            ) {
                items(allItems, key = { it.id }) { item ->
                    ReminderItemCard(
                        item = item,
                        onDeleteClick = { viewModel.deleteFoodItem(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReminderStatusLegend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusIndicator(color = Color(0xFF4CAF50), label = "Aman")
        StatusIndicator(color = Color(0xFFFFC107), label = "<7 Hari")
        StatusIndicator(color = Color(0xFFD32F2F), label = "<3 Hari")
    }
}

@Composable
fun StatusIndicator(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Text(text = label, fontSize = 14.sp)
    }
}

@Composable
fun ReminderItemCard(item: FoodItem, onDeleteClick: () -> Unit) {
    val currentTime = System.currentTimeMillis()
    val daysLeft = TimeUnit.MILLISECONDS.toDays(item.expiryDate - currentTime)

    val (statusText, bgColor, textColor) = when {
        daysLeft < 0 -> Triple("Already Expired", Color(0xFFFFCDD2), Color(0xFFD32F2F)) // merah muda
        daysLeft == 0L -> Triple("Expires Today", Color(0xFFFFCDD2), Color(0xFFD32F2F)) // merah muda
        daysLeft in 1..2 -> Triple("Expires in ${daysLeft} days", Color(0xFFFFF9C4), Color(0xFFF57C00)) // kuning pastel
        else -> Triple("More than 2 days", Color(0xFFC8E6C9), Color(0xFF388E3C)) // hijau pastel
    }


    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val expiryDateText = dateFormat.format(Date(item.expiryDate))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.TopStart)) {
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Expired: $expiryDateText",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = statusText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Gray
                )
            }
        }
    }
}

