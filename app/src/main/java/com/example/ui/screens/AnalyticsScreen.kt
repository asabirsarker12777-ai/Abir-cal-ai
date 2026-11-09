package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodEntry
import com.example.data.model.User
import com.example.data.repository.FoodRepository
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkEmeraldSurface
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MacroCarbs
import com.example.ui.theme.MacroFat
import com.example.ui.theme.MacroProtein
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WaterBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun AnalyticsScreen(
    user: User?,
    foodRepository: FoodRepository,
    modifier: Modifier = Modifier
) {
    val lang = user?.language ?: "en"
    val isBn = lang == "bn"

    var allEntries by remember { mutableStateOf<List<FoodEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        allEntries = foodRepository.getPastDaysEntries(30)
        isLoading = false
    }

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val daySdf = SimpleDateFormat("EEE", if (isBn) Locale("bn", "BD") else Locale.ENGLISH)

    // Build 7-day stats
    val past7Days = remember(allEntries) {
        val list = mutableListOf<Pair<String, Double>>()
        val cal = Calendar.getInstance()
        for (i in 6 downTo 0) {
            val dCal = Calendar.getInstance()
            dCal.add(Calendar.DAY_OF_YEAR, -i)
            val dateStr = sdf.format(dCal.time)
            val dayName = daySdf.format(dCal.time)
            val dayTotal = allEntries.filter { it.mealDate == dateStr }.sumOf { it.calories }
            list.add(Pair(dayName, dayTotal))
        }
        list
    }

    val total7DayCalories = past7Days.sumOf { it.second }
    val avgCalories = if (past7Days.isNotEmpty()) (total7DayCalories / 7.0).roundToInt() else 0

    val totalProtein = allEntries.sumOf { it.protein }
    val totalCarbs = allEntries.sumOf { it.carbs }
    val totalFat = allEntries.sumOf { it.fat }
    val totalMacros = (totalProtein + totalCarbs + totalFat).coerceAtLeast(1.0)

    val proteinPct = ((totalProtein / totalMacros) * 100).roundToInt()
    val carbsPct = ((totalCarbs / totalMacros) * 100).roundToInt()
    val fatPct = (100 - proteinPct - carbsPct).coerceAtLeast(0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = if (isBn) "পুষ্টি বিশ্লেষণ ও অগ্রগতি" else "Nutrition Progress & Analytics",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBn) "গত ৭ দিন ও সামগ্রিক পুষ্টির ভারসাম্য" else "7-Day trends & overall dietary balance",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Weekly Calorie Bar Chart
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = ElectricEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn) "৭ দিনের ক্যালোরি ইতিহাস" else "7-Day Calorie Intake",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = if (isBn) "গড়: $avgCalories kcal" else "Avg: $avgCalories kcal",
                            color = ElectricEmerald,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 7-Bar Chart in Canvas
                    val maxVal = (past7Days.maxOfOrNull { it.second } ?: 2000.0).coerceAtLeast(2000.0).toFloat()

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        val barWidth = size.width / 9f
                        val spacing = (size.width - (barWidth * 7f)) / 8f

                        past7Days.forEachIndexed { index, pair ->
                            val x = spacing + index * (barWidth + spacing)
                            val barHeight = ((pair.second.toFloat() / maxVal) * size.height).coerceIn(4f, size.height)
                            val y = size.height - barHeight

                            // Background bar track
                            drawRoundRect(
                                color = Color(0x1A00E676),
                                topLeft = Offset(x, 0f),
                                size = Size(barWidth, size.height),
                                cornerRadius = CornerRadius(8f, 8f)
                            )

                            // Active bar
                            drawRoundRect(
                                color = if (pair.second > 0) ElectricEmerald else Color(0x3300E676),
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Day labels below bars
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        past7Days.forEach { pair ->
                            Text(
                                text = pair.first,
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Overall Macronutrient Ratio Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = if (isBn) "ম্যাক্রোনিউট্রিয়েন্ট বিভাজন" else "Macronutrient Split",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Combined macro progress bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                    ) {
                        if (allEntries.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize().background(Color(0x33FFFFFF)))
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(proteinPct.coerceAtLeast(1).toFloat())
                                    .fillMaxSize()
                                    .background(MacroProtein)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(carbsPct.coerceAtLeast(1).toFloat())
                                    .fillMaxSize()
                                    .background(MacroCarbs)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(fatPct.coerceAtLeast(1).toFloat())
                                    .fillMaxSize()
                                    .background(MacroFat)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacroLegendItem(
                            color = MacroProtein,
                            label = if (isBn) "প্রোটিন" else "Protein",
                            percentage = "$proteinPct%",
                            grams = "${totalProtein.roundToInt()}g"
                        )
                        MacroLegendItem(
                            color = MacroCarbs,
                            label = if (isBn) "কার্বস" else "Carbs",
                            percentage = "$carbsPct%",
                            grams = "${totalCarbs.roundToInt()}g"
                        )
                        MacroLegendItem(
                            color = MacroFat,
                            label = if (isBn) "ফ্যাট" else "Fat",
                            percentage = "$fatPct%",
                            grams = "${totalFat.roundToInt()}g"
                        )
                    }
                }
            }
        }

        // Healthy Nutrition Education Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = ElectricEmerald,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isBn) "পুষ্টি টিপস" else "Nutrition Guidance",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBn)
                                "সুস্থ থাকার মূল রহস্য হলো সুষম খাবার গ্রহণ করা। অপ্রয়োজনীয় কঠোর ডায়েট বা খাবার বাদ দেওয়া এড়িয়ে চলুন। পর্যাপ্ত পানি ও শাক-সবজি যোগ করুন।"
                            else
                                "Sustainable health comes from nourishing meals, not restrictive deficits. Enjoy whole foods, colorful vegetables, and stay adequately hydrated.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MacroLegendItem(
    color: Color,
    label: String,
    percentage: String,
    grams: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(text = label, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = "$percentage ($grams)", color = TextMuted, fontSize = 11.sp)
        }
    }
}
