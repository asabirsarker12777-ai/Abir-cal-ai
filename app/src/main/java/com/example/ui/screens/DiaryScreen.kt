package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodEntry
import com.example.data.model.User
import com.example.data.repository.FoodRepository
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkEmeraldSurface
import com.example.ui.theme.DarkEmeraldSurfaceElevated
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.LanguageManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DiaryScreen(
    user: User?,
    selectedDate: String,
    onDateChange: (String) -> Unit,
    foodEntries: List<FoodEntry>,
    foodRepository: FoodRepository,
    onAddFoodClick: (mealType: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = user?.language ?: "en"
    val isBn = lang == "bn"
    val scope = rememberCoroutineScope()

    var editingEntry by remember { mutableStateOf<FoodEntry?>(null) }

    // Meal types
    val mealCategories = listOf("breakfast", "lunch", "dinner", "snack")

    val totalCalories = foodEntries.sumOf { it.calories }
    val totalProtein = foodEntries.sumOf { it.protein }
    val totalCarbs = foodEntries.sumOf { it.carbs }
    val totalFat = foodEntries.sumOf { it.fat }

    // Date navigation helpers
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displaySdf = SimpleDateFormat("EEEE, MMM dd", if (isBn) Locale("bn", "BD") else Locale.ENGLISH)

    val currentDateObj = try {
        sdf.parse(selectedDate) ?: Date()
    } catch (e: Exception) {
        Date()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Date Selector Bar
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            cal.time = currentDateObj
                            cal.add(Calendar.DAY_OF_YEAR, -1)
                            onDateChange(sdf.format(cal.time))
                        },
                        modifier = Modifier.testTag("prev_date_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Day",
                            tint = ElectricEmerald
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = displaySdf.format(currentDateObj),
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        val isToday = selectedDate == sdf.format(Date())
                        if (isToday) {
                            Text(
                                text = if (isBn) "আজকের দিন" else "Today",
                                color = ElectricEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            cal.time = currentDateObj
                            cal.add(Calendar.DAY_OF_YEAR, 1)
                            onDateChange(sdf.format(cal.time))
                        },
                        modifier = Modifier.testTag("next_date_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Day",
                            tint = ElectricEmerald
                        )
                    }
                }
            }
        }

        // 2. Daily Total Banner
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${totalCalories.roundToInt()}",
                            color = ElectricEmerald,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBn) "মোট ক্যালোরি" else "Total Kcal",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${totalProtein.roundToInt()}g",
                            color = Color(0xFF818CF8),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBn) "প্রোটিন" else "Protein",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${totalCarbs.roundToInt()}g",
                            color = Color(0xFFFBBF24),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBn) "কার্বস" else "Carbs",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${totalFat.roundToInt()}g",
                            color = Color(0xFFFB7185),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBn) "ফ্যাট" else "Fat",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 3. Four Meal Sections: Breakfast, Lunch, Dinner, Snack
        mealCategories.forEach { category ->
            val entriesForCategory = foodEntries.filter { it.mealType.equals(category, ignoreCase = true) }
            val categoryKcal = entriesForCategory.sumOf { it.calories }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = LanguageManager.getText(category, lang),
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${categoryKcal.roundToInt()} kcal",
                            color = ElectricEmerald,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Quick Add button for this meal
                    IconButton(
                        onClick = { onAddFoodClick(category) },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x2600E676))
                            .testTag("add_meal_$category")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Food to $category",
                            tint = ElectricEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (entriesForCategory.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1A0D1910))
                            .border(1.dp, Color(0x1500E676), RoundedCornerShape(12.dp))
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = if (isBn) "কোনো খাবার যোগ করা হয়নি" else "No foods added yet",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                items(entriesForCategory) { entry ->
                    DiaryItemCard(
                        entry = entry,
                        isBn = isBn,
                        onEdit = { editingEntry = entry },
                        onDelete = {
                            scope.launch {
                                foodRepository.deleteFoodEntry(entry.id)
                            }
                        }
                    )
                }
            }
        }
    }

    // Edit Serving Size Dialog
    editingEntry?.let { entry ->
        EditServingDialog(
            entry = entry,
            isBn = isBn,
            onDismiss = { editingEntry = null },
            onSave = { updatedEntry ->
                scope.launch {
                    foodRepository.updateFoodEntry(updatedEntry)
                    editingEntry = null
                }
            }
        )
    }
}

@Composable
fun DiaryItemCard(
    entry: FoodEntry,
    isBn: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 14.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isBn && entry.foodNameBn.isNotBlank()) entry.foodNameBn else entry.foodName,
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${entry.servingSize} ${entry.servingUnit} • ${entry.mealTime}",
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Text(
                    text = "P: ${entry.protein.roundToInt()}g  C: ${entry.carbs.roundToInt()}g  F: ${entry.fat.roundToInt()}g",
                    color = ElectricEmerald.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${entry.calories.roundToInt()} kcal",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Portion",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Entry",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EditServingDialog(
    entry: FoodEntry,
    isBn: Boolean,
    onDismiss: () -> Unit,
    onSave: (FoodEntry) -> Unit
) {
    var sizeText by remember { mutableStateOf(entry.servingSize.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkEmeraldSurface,
        title = {
            Text(
                text = if (isBn) "পরিমাণ পরিবর্তন করুন" else "Change Portion Size",
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = if (isBn && entry.foodNameBn.isNotBlank()) entry.foodNameBn else entry.foodName,
                    color = ElectricEmerald,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = sizeText,
                    onValueChange = { sizeText = it },
                    label = { Text("Serving Multiplier (${entry.servingUnit})") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = ElectricEmerald,
                        unfocusedBorderColor = GlassBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val multiplier = sizeText.toDoubleOrNull() ?: 1.0
                    val ratio = if (entry.servingSize > 0) multiplier / entry.servingSize else multiplier
                    val updated = entry.copy(
                        servingSize = multiplier,
                        calories = entry.calories * ratio,
                        protein = entry.protein * ratio,
                        carbs = entry.carbs * ratio,
                        fat = entry.fat * ratio
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald, contentColor = Color.Black)
            ) {
                Text(if (isBn) "সংরক্ষণ" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBn) "বাতিল" else "Cancel", color = TextMuted)
            }
        }
    )
}
