package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodEntry
import com.example.data.model.User
import com.example.data.model.WaterEntry
import com.example.data.repository.FoodRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.MacroRow
import com.example.ui.components.NavScreen
import com.example.ui.components.NutritionRing
import com.example.ui.theme.DarkEmeraldSurface
import com.example.ui.theme.DarkEmeraldSurfaceElevated
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MintEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterCardBorder
import com.example.util.LanguageManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    user: User?,
    foodEntries: List<FoodEntry>,
    waterEntries: List<WaterEntry>,
    foodRepository: FoodRepository,
    onNavigate: (NavScreen) -> Unit,
    onOpenSearch: () -> Unit,
    onSignInClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lang = user?.language ?: "en"
    val isBn = lang == "bn"
    val scope = rememberCoroutineScope()

    // Calculate actual consumed totals
    val consumedCalories = foodEntries.sumOf { it.calories }
    val consumedProtein = foodEntries.sumOf { it.protein }
    val consumedCarbs = foodEntries.sumOf { it.carbs }
    val consumedFat = foodEntries.sumOf { it.fat }
    val consumedWaterMl = waterEntries.sumOf { it.amountMl }

    val targetCalories = user?.targetCalories ?: 2000
    val targetProtein = user?.targetProtein ?: 100
    val targetCarbs = user?.targetCarbs ?: 250
    val targetFat = user?.targetFat ?: 60
    val targetWater = user?.targetWaterMl ?: 2500

    val dateFormat = SimpleDateFormat("EEEE, dd MMMM", if (isBn) Locale("bn", "BD") else Locale.ENGLISH)
    val formattedDate = dateFormat.format(Date())

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Greeting & Date Banner
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (user == null) {
                        if (isBn) "স্বাগতম, অতিথি!" else "Welcome to ABIR CAL AI!"
                    } else {
                        if (isBn) "স্বাগতম, ${user.name}" else "Hello, ${user.name}"
                    },
                    color = TextWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (user == null) {
                        if (isBn) "স্মার্ট পুষ্টি ট্র্যাকার ও খাদ্য নির্দেশিকা" else "Smart Nutrition Tracker & Food Guide"
                    } else {
                        formattedDate
                    },
                    color = if (user == null) ElectricEmerald else TextMuted,
                    fontSize = 13.sp,
                    fontWeight = if (user == null) FontWeight.Medium else FontWeight.Normal
                )
            }
        }

        // 1b. Guest Sign In Banner (shown when guest user is browsing)
        if (user == null) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = ElectricEmerald.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x2600E676))
                                    .border(1.dp, GlassBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Login,
                                    contentDescription = null,
                                    tint = ElectricEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBn) "সম্পূর্ণ সুবিধার জন্য সাইন ইন করুন" else "Sign In to Unlock Full Features",
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isBn) "খাবার সংরক্ষণ, এআই স্ক্যান এবং ব্যক্তিগত ডায়েরি সিঙ্ক করতে গুগল দিয়ে যুক্ত হোন।"
                                    else "Sign in with Google to save your daily meals, scan food photos, and sync nutrition history.",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onSignInClick,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricEmerald,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("home_guest_sign_in_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "গুগল দিয়ে সাইন ইন করুন" else "Sign In with Google",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. Daily Calorie & Macro Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = if (isBn) "আজকের পুষ্টি সারাংশ" else "Daily Nutrition Summary",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NutritionRing(
                            consumedCalories = consumedCalories,
                            targetCalories = targetCalories,
                            size = 140.dp,
                            strokeWidth = 14.dp,
                            labelConsumed = if (isBn) "ক্যালোরি" else "Consumed"
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        MacroRow(
                            proteinConsumed = consumedProtein,
                            proteinTarget = targetProtein,
                            carbsConsumed = consumedCarbs,
                            carbsTarget = targetCarbs,
                            fatConsumed = consumedFat,
                            fatTarget = targetFat,
                            proteinLabel = if (isBn) "প্রোটিন" else "Protein",
                            carbsLabel = if (isBn) "কার্বস" else "Carbs",
                            fatLabel = if (isBn) "ফ্যাট" else "Fat",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. Quick Action Buttons
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = LanguageManager.getText("quick_actions", lang),
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.CenterFocusStrong,
                        label = if (isBn) "স্ক্যান" else "Scan",
                        color = ElectricEmerald,
                        onClick = {
                            if (user == null) onSignInClick() else onNavigate(NavScreen.SCAN)
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "quick_action_scan"
                    )

                    QuickActionButton(
                        icon = Icons.Default.Search,
                        label = if (isBn) "খাবার খুঁজুন" else "Search",
                        color = MintEmerald,
                        onClick = onOpenSearch,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_action_search"
                    )

                    QuickActionButton(
                        icon = Icons.Default.LocalDrink,
                        label = if (isBn) "+২৫০ মিলি" else "+250 ml",
                        color = WaterBlue,
                        onClick = {
                            if (user == null) {
                                onSignInClick()
                            } else {
                                scope.launch {
                                    foodRepository.addWater(250)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "quick_action_water"
                    )

                    QuickActionButton(
                        icon = Icons.Default.AutoAwesome,
                        label = if (isBn) "এআই চ্যাট" else "Ask AI",
                        color = Color(0xFFA78BFA),
                        onClick = {
                            if (user == null) onSignInClick() else onNavigate(NavScreen.ASSISTANT)
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "quick_action_ai"
                    )
                }
            }
        }

        // 4. Water Intake Tracker Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = WaterCardBorder
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0x2638BDF8))
                                .border(1.dp, WaterCardBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalDrink,
                                contentDescription = "Water",
                                tint = WaterBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = LanguageManager.getText("water_tracker", lang),
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "$consumedWaterMl ml / $targetWater ml",
                                color = WaterBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                if (user == null) onSignInClick() else scope.launch { foodRepository.addWater(250) }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0x3338BDF8),
                                contentColor = WaterBlue
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("add_water_250")
                        ) {
                            Text("+250ml", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (user == null) onSignInClick() else scope.launch { foodRepository.addWater(500) }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0x3338BDF8),
                                contentColor = WaterBlue
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("add_water_500")
                        ) {
                            Text("+500ml", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 5. Recent Meals Header & List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LanguageManager.getText("recent_meals", lang),
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = if (isBn) "সম্পূর্ণ ডায়েরি দেখুন" else "View Diary",
                    color = ElectricEmerald,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        if (user == null) onSignInClick() else onNavigate(NavScreen.DIARY)
                    }
                )
            }
        }

        // Empty state or list of logged meals
        if (foodEntries.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = ElectricEmerald.copy(alpha = 0.7f),
                            modifier = Modifier.size(38.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (user == null) {
                                if (isBn) "খাবারের কোনো তথ্য নেই" else "No Meals Logged Yet"
                            } else {
                                LanguageManager.getText("empty_diary_msg", lang)
                            },
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (user == null) {
                                if (isBn) "আজকের খাবার ট্র্যাক করতে ও ক্যালোরি দেখতে সাইন ইন করুন।"
                                else "Sign in to track your meals, calculate accurate macros, and view your history."
                            } else {
                                LanguageManager.getText("empty_diary_sub", lang)
                            },
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        if (user == null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onSignInClick,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricEmerald,
                                    contentColor = Color.Black
                                ),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("empty_state_sign_in_button")
                            ) {
                                Text(
                                    text = if (isBn) "সাইন ইন করুন" else "Sign In to Start",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else {
            items(foodEntries.take(4)) { entry ->
                MealItemCard(entry = entry, isBn = isBn)
            }
        }
    }
}

@Composable
fun MealItemCard(
    entry: FoodEntry,
    isBn: Boolean,
    onDelete: (() -> Unit)? = null
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
                    text = if (isBn && entry.foodNameBn.isNotBlank()) entry.foodNameBn else entry.foodName,
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${entry.mealType.replaceFirstChar { it.uppercase() }} • ${entry.servingSize} ${entry.servingUnit} • ${entry.mealTime}",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${entry.calories.roundToInt()} kcal",
                    color = ElectricEmerald,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "P: ${entry.protein.roundToInt()}g  C: ${entry.carbs.roundToInt()}g  F: ${entry.fat.roundToInt()}g",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkEmeraldSurfaceElevated)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = TextWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}
