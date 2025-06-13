package com.example.musicmanager.ui.components


import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musicmanager.navigation.BottomNavItem

/**
 * Composable function for rendering a bottom navigation bar.
 * Displays navigation items with icons and labels, allowing users to navigate between different routes.
 *
 * @param items A list of `BottomNavItem` objects representing the navigation items.
 * @param navController The `NavController` used to manage navigation between routes.
 * @param modifier A `Modifier` for customizing the appearance and behavior of the navigation bar.
 * @param onItemClick A lambda function invoked when a navigation item is clicked, passing the clicked `BottomNavItem`.
 */
@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    // Get the current back stack entry to determine the selected route.
    val backStackEntry = navController.currentBackStackEntryAsState()

    // Render the navigation bar with a black background and slight elevation.
    NavigationBar(
        modifier = modifier,
        containerColor = Color.Black,
        tonalElevation = 5.dp,
    ) {
        // Iterate through the navigation items and render each as a NavigationBarItem.
        items.forEach { item ->
            NavigationBarItem(
                label = {
                    Text(text = item.name) // Display the name of the navigation item.
                },
                selected = item.route == backStackEntry.value?.destination?.route, // Check if the item is selected.
                onClick = { onItemClick(item) }, // Handle item click events.
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White, // Icon color for selected items.
                    unselectedIconColor = Color.Gray, // Icon color for unselected items.
                    selectedTextColor = Color.White, // Text color for selected items.
                    unselectedTextColor = Color.Gray // Text color for unselected items.
                ),
                alwaysShowLabel = true, // Always show the label for navigation items.
                icon = {
                    Icon(
                        item.icon, // Display the icon for the navigation item.
                        contentDescription = item.name // Provide a content description for accessibility.
                    )
                }
            )
        }
    }
}