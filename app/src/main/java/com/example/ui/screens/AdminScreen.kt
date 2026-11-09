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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.Announcement
import com.example.data.model.FoodItem
import com.example.data.model.User
import com.example.data.repository.AdminRepository
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkEmeraldSurface
import com.example.ui.theme.DarkEmeraldSurfaceElevated
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(
    user: User?,
    adminRepository: AdminRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = user?.language ?: "en"
    val isBn = lang == "bn"
    val scope = rememberCoroutineScope()

    val isSuperAdmin = adminRepository.isSuperAdmin()

    var stats by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }

    // Add food form state
    var newFoodNameEn by remember { mutableStateOf("") }
    var newFoodNameBn by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("Traditional") }
    var newCalories by remember { mutableStateOf("") }
    var newProtein by remember { mutableStateOf("") }
    var newCarbs by remember { mutableStateOf("") }
    var newFat by remember { mutableStateOf("") }
    var isSavingFood by remember { mutableStateOf(false) }
    var foodSaveStatus by remember { mutableStateOf<String?>(null) }

    // New announcement state
    var announceTitle by remember { mutableStateOf("") }
    var announceContent by remember { mutableStateOf("") }
    var isPostingAnnounce by remember { mutableStateOf(false) }

    // Super Admin: change user role
    var targetUid by remember { mutableStateOf("") }
    var newRole by remember { mutableStateOf("admin") }
    var roleStatus by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        stats = adminRepository.getAppStatistics()
        adminRepository.observeAnnouncements().collect {
            announcements = it
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 90.dp)
    ) {
        // Header
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
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = ElectricEmerald,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isBn) "অ্যাডমিন কন্ট্রোল প্যানেল" else "Admin Control Panel",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Statistics Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                tint = ElectricEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "অ্যাপ পরিসংখ্যান" else "App Statistics",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${stats["total_users"] ?: 0}",
                                    color = ElectricEmerald,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isBn) "নিবন্ধিত ব্যবহারকারী" else "Total Users",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${stats["total_food_logs"] ?: 0}",
                                    color = Color(0xFF818CF8),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isBn) "মোট ফুড লগ" else "Food Logs",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${stats["custom_foods_count"] ?: 0}",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isBn) "ডাটাবেজ আইটেম" else "Verified Items",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Super Admin Card: User Role Assignment
            if (isSuperAdmin) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = Color(0xFFA78BFA)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color(0xFFA78BFA),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBn) "সুপার অ্যাডমিন: ভূমিকা নির্ধারণ" else "Super Admin: Manage Admin Roles",
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = targetUid,
                                onValueChange = { targetUid = it },
                                label = { Text("Target User UID") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = Color(0xFFA78BFA),
                                    unfocusedBorderColor = GlassBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        if (targetUid.isNotBlank()) {
                                            scope.launch {
                                                try {
                                                    adminRepository.setUserRole(targetUid, "admin")
                                                    roleStatus = "User promoted to Admin!"
                                                } catch (e: Exception) {
                                                    roleStatus = e.message
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA78BFA), contentColor = Color.Black)
                                ) {
                                    Text("Make Admin")
                                }

                                Button(
                                    onClick = {
                                        if (targetUid.isNotBlank()) {
                                            scope.launch {
                                                try {
                                                    adminRepository.setUserRole(targetUid, "user")
                                                    roleStatus = "Admin privileges revoked!"
                                                } catch (e: Exception) {
                                                    roleStatus = e.message
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White)
                                ) {
                                    Text("Revoke Admin")
                                }
                            }

                            roleStatus?.let {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = it, color = ElectricEmerald, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Add Food to Database Form
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = ElectricEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "ডাটাবেজে নতুন খাবার যোগ করুন" else "Add Food to Nutrition Database",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = newFoodNameEn,
                            onValueChange = { newFoodNameEn = it },
                            label = { Text("Food Name (English)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = ElectricEmerald,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newFoodNameBn,
                            onValueChange = { newFoodNameBn = it },
                            label = { Text("Food Name (Bengali)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = ElectricEmerald,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newCalories,
                                onValueChange = { newCalories = it },
                                label = { Text("Calories") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = ElectricEmerald,
                                    unfocusedBorderColor = GlassBorder
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = newProtein,
                                onValueChange = { newProtein = it },
                                label = { Text("Protein (g)") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = ElectricEmerald,
                                    unfocusedBorderColor = GlassBorder
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newCarbs,
                                onValueChange = { newCarbs = it },
                                label = { Text("Carbs (g)") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = ElectricEmerald,
                                    unfocusedBorderColor = GlassBorder
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = newFat,
                                onValueChange = { newFat = it },
                                label = { Text("Fat (g)") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = ElectricEmerald,
                                    unfocusedBorderColor = GlassBorder
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        foodSaveStatus?.let {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = it, color = ElectricEmerald, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (newFoodNameEn.isNotBlank() && newCalories.isNotBlank()) {
                                    isSavingFood = true
                                    scope.launch {
                                        try {
                                            val food = FoodItem(
                                                nameEn = newFoodNameEn,
                                                nameBn = newFoodNameBn,
                                                category = newCategory,
                                                calories = newCalories.toDoubleOrNull() ?: 0.0,
                                                protein = newProtein.toDoubleOrNull() ?: 0.0,
                                                carbs = newCarbs.toDoubleOrNull() ?: 0.0,
                                                fat = newFat.toDoubleOrNull() ?: 0.0,
                                                isVerified = true
                                            )
                                            adminRepository.addVerifiedFood(food)
                                            foodSaveStatus = "Food added to Firestore verified database!"
                                            newFoodNameEn = ""
                                            newFoodNameBn = ""
                                            newCalories = ""
                                            newProtein = ""
                                            newCarbs = ""
                                            newFat = ""
                                        } catch (e: Exception) {
                                            foodSaveStatus = "Error: ${e.message}"
                                        } finally {
                                            isSavingFood = false
                                        }
                                    }
                                }
                            },
                            enabled = !isSavingFood,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            if (isSavingFood) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Publish Food to Database", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Broadcast Announcements
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Announcement,
                                contentDescription = null,
                                tint = ElectricEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "অ্যাপ ঘোষণা প্রকাশ করুন" else "Broadcast Announcement",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = announceTitle,
                            onValueChange = { announceTitle = it },
                            label = { Text("Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = ElectricEmerald,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = announceContent,
                            onValueChange = { announceContent = it },
                            label = { Text("Message Content") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = ElectricEmerald,
                                unfocusedBorderColor = GlassBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (announceTitle.isNotBlank() && announceContent.isNotBlank()) {
                                    isPostingAnnounce = true
                                    scope.launch {
                                        try {
                                            adminRepository.postAnnouncement(announceTitle, announceContent)
                                            announceTitle = ""
                                            announceContent = ""
                                        } finally {
                                            isPostingAnnounce = false
                                        }
                                    }
                                }
                            },
                            enabled = !isPostingAnnounce,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricEmerald, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Broadcast to All Users", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
