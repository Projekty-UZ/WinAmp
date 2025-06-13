package com.example.musicmanager.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Defines the color scheme for dark mode in the application.
 * Uses predefined color values for primary, secondary, and tertiary colors.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80, // Primary color for dark mode.
    secondary = PurpleGrey80, // Secondary color for dark mode.
    tertiary = Pink80 // Tertiary color for dark mode.
)

/**
 * Defines the color scheme for light mode in the application.
 * Uses predefined color values for primary, secondary, and tertiary colors.
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40, // Primary color for light mode.
    secondary = PurpleGrey40, // Secondary color for light mode.
    tertiary = Pink40 // Tertiary color for light mode.

    /* Other default colors to override
    background = Color(0xFFFFFBFE), // Background color for light mode.
    surface = Color(0xFFFFFBFE), // Surface color for light mode.
    onPrimary = Color.White, // Text color on primary elements.
    onSecondary = Color.White, // Text color on secondary elements.
    onTertiary = Color.White, // Text color on tertiary elements.
    onBackground = Color(0xFF1C1B1F), // Text color on background elements.
    onSurface = Color(0xFF1C1B1F), // Text color on surface elements.
    */
)

/**
 * Composable function for applying the application's theme.
 * Dynamically selects the color scheme based on the system's dark mode setting and Android version.
 *
 * @param darkTheme Boolean indicating whether dark mode is enabled.
 * @param dynamicColor Boolean indicating whether dynamic color is enabled (available on Android 12+).
 * @param content The composable content to be styled with the theme.
 */
@Composable
fun MusicManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Default to system's dark mode setting.
    dynamicColor: Boolean = true, // Enable dynamic color by default.
    content: @Composable () -> Unit // Lambda for the composable content.
) {
    val colorScheme = when {
        // Use dynamic color scheme if supported and enabled.
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Use predefined dark color scheme if dark mode is enabled.
        darkTheme -> DarkColorScheme
        // Use predefined light color scheme otherwise.
        else -> LightColorScheme
    }

    // Apply the selected color scheme and typography to the MaterialTheme.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}