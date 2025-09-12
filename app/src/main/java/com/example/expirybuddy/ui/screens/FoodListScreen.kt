package com.example.expirybuddy.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expirybuddy.MyApplication
import com.example.expirybuddy.data.FoodItem
import com.example.expirybuddy.ui.components.FoodListItem
import com.example.expirybuddy.viewmodel.FoodViewModel
import com.example.expirybuddy.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodListScreen() {
    val application = LocalContext.current.applicationContext as MyApplication
    val viewModel: FoodViewModel = viewModel(
        factory = ViewModelFactory(application.database.foodItemDao())
    )

    val allItems by viewModel.allFoodItems.collectAsState()
    val groupedItems = allItems.groupBy { it.category }

    var selectedItem by remember { mutableStateOf<FoodItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Text(
                        "FOOD LIST",
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                icon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Food",
                        tint = Color.White
                    )
                },
                text = { Text("Tambah Makanan", color = Color.White) },
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF4CAF50),  // Warna hijau
                contentColor = Color.White
            )
        },
            floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedItems.forEach { (category, itemsInCategory) ->
                    item {
                        Text(
                            text = category,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    items(itemsInCategory, key = { it.id }) { foodItem ->
                        FoodListItem(
                            item = foodItem,
                            onDeleteClick = { viewModel.deleteFoodItem(foodItem) },
                            onItemClick = { clickedItem -> selectedItem = clickedItem }
                        )
                        Divider()
                    }
                }
            }
        }

        // Dialog tetap
        selectedItem?.let { item ->
            FoodDetailDialog(
                foodItem = item,
                onDismiss = { selectedItem = null }
            )
        }

        if (showAddDialog) {
            FoodItemDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { newItem ->
                    viewModel.addFoodItem(newItem.name, newItem.expiryDate, newItem.category)
                    showAddDialog = false
                }
            )
        }
    }
}

    @Composable
fun FoodDetailDialog(foodItem: FoodItem, onDismiss: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val expiryDateText = dateFormat.format(Date(foodItem.expiryDate))

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("OK") }
        },
        title = { Text(foodItem.name) },
        text = {
            Column {
                Text("Kategori: ${foodItem.category}")
                Text("Expired: $expiryDateText")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (FoodItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedDate by remember { mutableLongStateOf(0L) }
    var selectedCategory by remember { mutableStateOf("Meat & Fish") }
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf("Meat & Fish", "Fruit & Veggies", "Dairy Food", "Grains", "Snack")

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth, 0, 0, 0)
            selectedDate = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && selectedDate != 0L) {
                        val foodItem = FoodItem(
                            id = 0,
                            name = name,
                            expiryDate = selectedDate,
                            category = selectedCategory
                        )
                        onConfirm(foodItem)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Add Food") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Makanan") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = if (selectedDate != 0L) dateFormatter.format(Date(selectedDate)) else "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tanggal Expired") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
