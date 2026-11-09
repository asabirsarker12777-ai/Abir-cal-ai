package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricEmerald
import com.example.ui.theme.MintEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlin.math.roundToInt

@Composable
fun NutritionRing(
    consumedCalories: Double,
    targetCalories: Int,
    size: Dp = 190.dp,
    strokeWidth: Dp = 16.dp,
    labelConsumed: String = "Consumed",
    labelKcal: String = "kcal"
) {
    val progress = if (targetCalories > 0) {
        (consumedCalories / targetCalories).toFloat().coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "calorie_progress"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            // Background track
            drawCircle(
                color = Color(0x2600E676),
                style = stroke
            )
            // Progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        ElectricEmerald,
                        MintEmerald,
                        Color(0xFF34D399),
                        ElectricEmerald
                    )
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = stroke
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${consumedCalories.roundToInt()}",
                color = TextWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$labelKcal $labelConsumed",
                color = ElectricEmerald,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "/ $targetCalories $labelKcal",
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}
