package com.example.savoreel.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = primaryButtonColor,
    secondary = secondaryDarkColor,
    background = backgroundDarkColor,
    outline = lineColor,
    tertiary = fontDarkColor
)

private val LightColorScheme = lightColorScheme(
    primary = primaryButtonColor,
    secondary = secondaryLightColor,
    background = backgroundLightColor,
    outline = lineColor,
    tertiary = fontLightColor
)

@Composable
fun SavoreelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            try {
                if (darkTheme) DarkColorScheme else LightColorScheme
            } catch (e: Exception) {
                if (darkTheme) DarkColorScheme else LightColorScheme
            }
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
