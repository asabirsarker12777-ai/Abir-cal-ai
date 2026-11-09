package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkEmeraldSurface
import com.example.ui.theme.DarkEmeraldSurfaceElevated
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.LanguageManager

enum class NavScreen {
    HOME,
    DIARY,
    SCAN,
    ASSISTANT,
    ANALYTICS,
    PROFILE,
    ADMIN
}

@Composable
fun BottomNavBar(
    currentScreen: NavScreen,
    onNavigate: (NavScreen) -> Unit,
    language: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Main floating bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkEmeraldSurfaceElevated.copy(alpha = 0.95f),
                            DarkEmeraldSurface.copy(alpha = 0.98f)
                        )
                    )
                )
                .border(1.dp, GlassBorder, RoundedCornerShape(32.dp))
                .padding(horizontal = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(64.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavBarItem(
                    icon = Icons.Default.Home,
                    label = LanguageManager.getText("home", language),
                    isSelected = currentScreen == NavScreen.HOME,
                    onClick = { onNavigate(NavScreen.HOME) },
                    testTag = "nav_home"
                )

                NavBarItem(
                    icon = Icons.Default.CalendarMonth,
                    label = LanguageManager.getText("diary", language),
                    isSelected = currentScreen == NavScreen.DIARY,
                    onClick = { onNavigate(NavScreen.DIARY) },
                    testTag = "nav_diary"
                )

                // Placeholder for center scan button
                Box(modifier = Modifier.size(52.dp))

                NavBarItem(
                    icon = Icons.Default.AutoAwesome,
                    label = LanguageManager.getText("assistant", language),
                    isSelected = currentScreen == NavScreen.ASSISTANT,
                    onClick = { onNavigate(NavScreen.ASSISTANT) },
                    testTag = "nav_assistant"
                )

                NavBarItem(
                    icon = Icons.Default.InsertChart,
                    label = LanguageManager.getText("progress", language),
                    isSelected = currentScreen == NavScreen.ANALYTICS,
                    onClick = { onNavigate(NavScreen.ANALYTICS) },
                    testTag = "nav_analytics"
                )
            }
        }

        // Elevated Prominent Center AI Scanner Button
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp)
                .size(58.dp)
                .clip(CircleShape)
                .shadow(elevation = 12.dp, shape = CircleShape, spotColor = ElectricEmerald)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricEmerald,
                            Color(0xFF00B050),
                            Color(0xFF006830)
                        )
                    )
                )
                .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                .clickable { onNavigate(NavScreen.SCAN) }
                .testTag("nav_scan_prominent"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CenterFocusStrong,
                contentDescription = "Scan Food",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun NavBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) ElectricEmerald else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = if (isSelected) ElectricEmerald else TextMuted,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
