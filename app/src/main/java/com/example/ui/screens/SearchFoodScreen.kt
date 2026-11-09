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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import com.example.data.datasource.BangladeshiFoodDatabase
import com.example.data.model.FavoriteFood
import com.example.data.model.FoodEntry
import com.example.data.model.FoodItem
import com.example.data.model.User
import com.example.data.repository.FoodRepository
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkEmeraldSurface
import com.example.ui.theme.DarkEmeraldSurfaceElevated
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MintEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.LanguageManager
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SearchFoodScreen(
    user: User?,
    preselectedMealType: String = "breakfast",
    foodRepository: FoodRepository,
    favoriteFoods: List<FavoriteFood>,
    onBack: () -> Unit,
    onFoodAdded: () -> Unit,
    onRequestSignIn: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val lang = user?.language ?: "en"
    val isBn = lang == "bn"
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedFoodForAdd by remember { mutableStateOf<FoodItem?>(null) }
    var targetMealType by remember { mutableStateOf(preselectedMealType) }

    val categories = listOf("All", "Rice", "Traditional", "Protein", "Vegetables", "Snacks", "Fruits/Dairy")

    val searchResults = remember(searchQuery, selectedCategory) {
        BangladeshiFoodDatabase.search(searchQuery, selectedCategory)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 90.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite
                )
            }
            Text(
                text = LanguageManager.getText("search_food", lang),
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = LanguageManager.getText("search_placeholder", lang),
                    color = TextMuted,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = ElectricEmerald
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = TextMuted
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = ElectricEmerald,
                unfocusedBorderColor = GlassBorder,
                focusedContainerColor = DarkEmeraldSurface,
                unfocusedContainerColor = DarkEmeraldSurface
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("food_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                val catLabel = when (cat) {
                    "All" -> if (isBn) "সব" else "All"
                    "Rice" -> if (isBn) "ভাত" else "Rice"
                    "Traditional" -> if (isBn) "ঐতিহ্যবাহী" else "Traditional"
                    "Protein" -> if (isBn) "মাছ-মাংস" else "Protein"
                    "Vegetables" -> if (isBn) "সবজি" else "Vegetables"
                    "Snacks" -> if (isBn) "স্ন্যাক্স" else "Snacks"
                    "Fruits/Dairy" -> if (isBn) "দুধ ও ফল" else "Fruits/Dairy"
                    else -> cat
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) ElectricEmerald else DarkEmeraldSurfaceElevated)
                        .border(1.dp, if (isSelected) ElectricEmerald else GlassBorder, RoundedCornerShape(16.dp))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = catLabel,
                        color = if (isSelected) Color.Black else TextWhite,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Results List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (searchResults.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBn) "কোনো খাবার পাওয়া যায়নি" else "No matching foods found",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(searchResults) { food ->
                    val isFavorite = favoriteFoods.any { it.foodId == food.id || it.foodName == food.nameEn }

                    FoodSearchCard(
                        food = food,
                        isBn = isBn,
                        isFavorite = isFavorite,
                        onFavoriteClick = {
                            if (user == null) {
                                onRequestSignIn?.invoke()
                            } else {
                                scope.launch {
                                    val existingFav = favoriteFoods.firstOrNull { it.foodId == food.id || it.foodName == food.nameEn }
                                    if (existingFav != null) {
                                        foodRepository.removeFavoriteFood(existingFav.id)
                                    } else {
                                        foodRepository.addFavoriteFood(food)
                                    }
                                }
                            }
                        },
                        onAddClick = {
                            if (user == null) {
                                onRequestSignIn?.invoke()
                            } else {
                                selectedFoodForAdd = food
                            }
                        }
                    )
                }
            }
        }
    }

    // Add Food Dialog
    selectedFoodForAdd?.let { food ->
        var portionMultiplier by remember { mutableStateOf("1.0") }

        AlertDialog(
            onDismissRequest = { selectedFoodForAdd = null },
            containerColor = DarkEmeraldSurface,
            title = {
                Text(
                    text = if (isBn && food.nameBn.isNotBlank()) food.nameBn else food.nameEn,
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "${food.calories.roundToInt()} kcal • ${food.servingUnit}",
                        color = ElectricEmerald,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isBn) "কোন বেলার খাবার?" else "Select Meal",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("breakfast", "lunch", "dinner", "snack").forEach { type ->
                            val isSel = targetMealType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ElectricEmerald else DarkEmeraldSurfaceElevated)
                                    .clickable { targetMealType = type }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = LanguageManager.getText(type, lang),
                                    color = if (isSel) Color.Black else TextWhite,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = portionMultiplier,
                        onValueChange = { portionMultiplier = it },
                        label = { Text(if (isBn) "পরিমাণ গুণক" else "Serving Multiplier") },
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
                        val mult = portionMultiplier.toDoubleOrNull() ?: 1.0
                        scope.launch {
                            val entry = FoodEntry(
                                foodName = food.nameEn,
                                foodNameBn = food.nameBn,
                                mealType = targetMealType,
                                servingUnit = food.servingUnit,
                                servingSize = mult,
                                calories = food.calories * mult,
                                protein = food.protein * mult,
                                carbs = food.carbs * mult,
                                fat = food.fat * mult,
                                mealDate = foodRepository.getTodayDate(),
                                mealTime = foodRepository.getCurrentTime(),
                                isAiEstimated = false
                            )
                            foodRepository.addFoodEntry(entry)
                            selectedFoodForAdd = null
                            onFoodAdded()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald, contentColor = Color.Black)
                ) {
                    Text(if (isBn) "ডায়েরিতে যোগ করুন" else "Add to Diary")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedFoodForAdd = null }) {
                    Text(if (isBn) "বাতিল" else "Cancel", color = TextMuted)
                }
            }
        )
    }
}

@Composable
fun FoodSearchCard(
    food: FoodItem,
    isBn: Boolean,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onAddClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isBn && food.nameBn.isNotBlank()) food.nameBn else food.nameEn,
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (isBn && food.nameEn.isNotBlank()) {
                    Text(text = food.nameEn, color = TextMuted, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${food.servingUnit} • ${food.category}",
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Text(
                    text = "P: ${food.protein.roundToInt()}g  C: ${food.carbs.roundToInt()}g  F: ${food.fat.roundToInt()}g",
                    color = ElectricEmerald.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${food.calories.roundToInt()} kcal",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 6.dp)
                )

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) ElectricEmerald else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x2600E676))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Food",
                        tint = ElectricEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
