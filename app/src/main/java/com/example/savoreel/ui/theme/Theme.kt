package com.example.savoreel.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    // Button
    primary = primaryColor,
    onPrimary = textButtonColor,
    primaryContainer = disableButtonColor,

    // Background
    background = backgroundLightColor,
    onBackground = textPrimaryLightColor, // Font primary

    // Secondary
    secondary = secondaryLightColor,
    onSecondary = textSecondaryLightColor, // Font secondary

    // Tertiary
    tertiary = tertiaryLightColor,
    onTertiary = textTertiaryLightColor , // Font tertiary

    // Quaternary
    surface = tertiaryLightColor,

    // Scrim - màu nền mờ
    scrim = backgroundBlurLightColor,

    outline = outlineColor,

//    scrim = homeLightColor
)

private val DarkColorScheme = darkColorScheme(
    // Button
    primary = primaryColor,
    onPrimary = textButtonColor,
    primaryContainer = disableButtonColor,

    // Background
    background = backgroundDarkColor,
    onBackground = textPrimaryDarkColor, // Font primary

    // Secondary
    secondary = secondaryDarkColor,
    onSecondary = textSecondaryDarkColor, // Font secondary

    // Tertiary
    tertiary = tertiaryDarkColor,
    onTertiary = textTertiaryDarkColor, // Font tertiary

    // Quaternary
    surface = quaternaryDarkColor,

    // Scrim - màu nền mờ
    scrim = backgroundBlurDarkColor,

    outline = outlineColor,

//    scrim = homeDarkColor

)



@Composable
fun SavoreelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}