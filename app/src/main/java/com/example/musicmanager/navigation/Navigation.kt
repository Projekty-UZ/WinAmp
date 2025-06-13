package com.example.musicmanager.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import com.example.musicmanager.screens.AddSongScreen
import com.example.musicmanager.screens.AnimatedSplashScreen
import com.example.musicmanager.screens.AuthScreen
import com.example.musicmanager.screens.LocationTrackerScreen
import com.example.musicmanager.screens.PlaylistScreen
import com.example.musicmanager.screens.SongControlScreen
import com.example.musicmanager.screens.SongScreen
import com.example.musicmanager.screens.StepCounterScreen

/**
 * Composable function for managing navigation within the application.
 * Sets up a navigation host with various routes and handles navigation events.
 *
 * @param navController The NavHostController used for managing navigation between screens.
 */
@Composable
fun Navigation(navController: NavHostController) {
    // Observe the current navigation event from the NavigationEventHolder.
    val navigateTo = NavigationEventHolder.navigateTo.collectAsState().value

    // Effect to handle navigation events and reset them after navigation is complete.
    LaunchedEffect(navigateTo) {
        navigateTo?.let { route: String ->
            navController.navigate(route) // Navigate to the specified route.
            NavigationEventHolder.onNavigationComplete() // Reset the navigation event.
        }
    }

    // Define the navigation host with the start destination and transitions.
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.route, // Initial screen displayed on app launch.
        enterTransition = { EnterTransition.None }, // No animation for entering transitions.
        exitTransition = { ExitTransition.None } // No animation for exiting transitions.
    ) {
        // Define the composable for the Song Screen.
        composable(Screens.SongScreen.route) {
            SongScreen(navController = navController)
        }
        // Define the composable for the Splash Screen.
        composable(Screens.SplashScreen.route) {
            AnimatedSplashScreen(navController = navController)
        }
        // Define the composable for the Playlist Screen.
        composable(Screens.PlaylistScreen.route) {
            PlaylistScreen()
        }
        // Define the composable for the Add Song Screen with a "ytLink" argument.
        composable(
            route = Screens.AddSongScreen.route,
            arguments = listOf(navArgument("ytLink") { defaultValue = "" })
        ) { backStackEntry ->
            val ytLink = backStackEntry.arguments?.getString("ytLink") ?: "" // Retrieve the "ytLink" argument.
            AddSongScreen(ytLink)
        }
        // Define the composable for the Song Control Screen.
        composable(Screens.SongControlScreen.route) {
            SongControlScreen()
        }
        // Define the composable for the Step Counter Screen.
        composable(Screens.StepCounterScreen.route) {
            StepCounterScreen()
        }
        // Define the composable for the Location Tracker Screen.
        composable(Screens.LocationTrackerScreen.route) {
            LocationTrackerScreen()
        }
        // Define the composable for the Authentication Screen.
        composable(Screens.AuthScreen.route) {
            AuthScreen()
        }
    }
}


