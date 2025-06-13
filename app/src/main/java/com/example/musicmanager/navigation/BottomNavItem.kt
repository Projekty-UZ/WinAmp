package com.example.musicmanager.navigation

import androidx.compose.ui.graphics.painter.Painter
/**
 * Data class representing a bottom navigation item in a Compose-based application.
 * Each item includes a route for navigation, a display name, and an icon.
 *
 * @property route The navigation route associated with the item.
 * @property name The display name of the navigation item.
 * @property icon The icon representing the navigation item.
 */
data class BottomNavItem(
    val route: String,
    val name: String,
    val icon: Painter
)
