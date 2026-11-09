package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LiquidGlassColorScheme = darkColorScheme(
    primary = ElectricEmerald,
    onPrimary = DeepGreenContainer,
    primaryContainer = DeepGreenContainer,
    onPrimaryContainer = LightGreenText,
    secondary = MintEmerald,
    onSecondary = DeepObsidian,
    secondaryContainer = DarkEmeraldSurfaceElevated,
    onSecondaryContainer = LightGreenText,
    tertiary = WaterBlue,
    onTertiary = DeepObsidian,
    background = DeepObsidian,
    onBackground = TextWhite,
    surface = DarkEmeraldSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkEmeraldSurfaceElevated,
    onSurfaceVariant = TextMuted,
    outline = GlassBorder,
    outlineVariant = GlassBorderSubtle
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = LiquidGlassColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DeepObsidian.toArgb()
                window.navigationBarColor = DeepObsidian.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
