package com.example.expirybuddy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expirybuddy.MyApplication
import com.example.expirybuddy.R
import com.example.expirybuddy.data.FoodItem
import com.example.expirybuddy.viewmodel.FoodViewModel
import com.example.expirybuddy.viewmodel.ViewModelFactory
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as? MyApplication

    if (application == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val viewModel: FoodViewModel = viewModel(
        factory = ViewModelFactory(application.database.foodItemDao())
    )

    val allItems by viewModel.allFoodItems.collectAsState(initial = emptyList())
    val reminderItems by viewModel.reminderTodayItems.collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = allItems.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        bottomBar = {
            // Placeholder untuk bottom navigation jika diperlukan
            // Bisa kamu tambah sesuai kebutuhan
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            WelcomeHeader(name = "Ael")
            Spacer(modifier = Modifier.height(8.dp))
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            CheckExpiryCard()
            Spacer(modifier = Modifier.height(16.dp))
            CategoriesRow()
            Spacer(modifier = Modifier.height(16.dp))

            if (searchQuery.isNotBlank()) {
                Text(
                    text = "Search Result :",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                if (filteredItems.isEmpty()) {
                    Text(
                        "Item not found.",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                } else {
                    filteredItems.forEach { item ->
                        FoodSearchItem(item = item)
                    }
                }
            } else {
                Text(
                    text = "Reminder Today",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                if (reminderItems.isEmpty()) {
                    Text(
                        text = "There are no items expiring tomorrow.",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                } else {
                    reminderItems.forEach { item ->
                        FoodSearchItem(item = item)
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun WelcomeHeader(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Welcome Back!", fontSize = 14.sp, color = Color.Gray)
            Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Image(
            painter = painterResource(id = R.drawable.pfp),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    val SearchBarBgColor = Color.White

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search your food",
                color = Color.Gray,
                fontSize = 14.sp
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = SearchBarBgColor,
            unfocusedContainerColor = SearchBarBgColor,
            cursorColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray
        ),
        singleLine = true
    )
}

@Composable
fun CheckExpiryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB7CC92)), // Sesuai gambar
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Check Expiry Now\n& Save Your Meal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO: Add action */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E5939), // Hijau gelap mirip gambar
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Check Now",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.mangkoklogo),
                contentDescription = "Food Image",
                modifier = Modifier.size(90.dp)
            )
        }
    }
}

@Composable
fun CategoriesRow() {
    val categories = listOf(
        Triple("Meat & Fish", R.drawable.meatnfish, Color(0xFFFFA000)), // Lebih oranye sesuai gambar
        Triple("Fruit & Veggies", R.drawable.fruit, Color(0xFFFFA000)),
        Triple("Dairy Food", R.drawable.dairy, Color(0xFFFFA000)),
        Triple("Grains", R.drawable.grain, Color(0xFFFFA000)),
        Triple("Snack", R.drawable.snack, Color(0xFFFFA000)),
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { (name, imageRes, bgColor) ->
            CategoryCard(name, imageRes, bgColor)
        }
    }
}

@Composable
fun CategoryCard(name: String, imageRes: Int, bgColor: Color) {
    Card(
        modifier = Modifier
            .width(90.dp)
            .height(90.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun FoodSearchItem(item: FoodItem) {
    val hariIni = LocalDate.now()
    val tanggalKadaluwarsa = Instant.ofEpochMilli(item.expiryDate)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val sisaHari = ChronoUnit.DAYS.between(hariIni, tanggalKadaluwarsa)

    val (warnaLatar, statusText) = when {
        sisaHari < 0 -> Color(0xFFB71C1C) to "Besok kadaluarsa\nSegera habiskan hari ini!"
        sisaHari == 0L -> Color(0xFFD32F2F) to "Besok kadaluarsa\nSegera habiskan hari ini!"
        sisaHari == 1L -> Color(0xFFFFCDD2) to "Kadaluarsa besok"
        sisaHari <= 3 -> Color(0xFFFFF59D) to "Kadaluarsa dalam $sisaHari hari"
        else -> Color(0xFF81C784) to "Masih aman ($sisaHari hari lagi)"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = warnaLatar),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(item.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
            Text(statusText, fontSize = 14.sp, color = Color.White)
        }
    }
}
