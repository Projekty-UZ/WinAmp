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
import com.example.musicmanager.screens.LocationTrackerScreen
import com.example.musicmanager.screens.PlaylistScreen
import com.example.musicmanager.screens.SongControlScreen
import com.example.musicmanager.screens.SongScreen
import com.example.musicmanager.screens.StepCounterScreen

@Composable
fun Navigation(navController: NavHostController){
    val navigateTo = NavigationEventHolder.navigateTo.collectAsState().value

    LaunchedEffect(navigateTo) {
        navigateTo?.let { route: String ->
            navController.navigate(route)
            NavigationEventHolder.onNavigationComplete() // Reset the navigation event
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(Screens.SongScreen.route) {
            SongScreen(navController = navController)
        }
        composable(Screens.SplashScreen.route) {
            AnimatedSplashScreen(navController = navController)
        }
        composable(Screens.PlaylistScreen.route) {
            PlaylistScreen()
        }
        composable(
            route = Screens.AddSongScreen.route,
            arguments = listOf(navArgument("ytLink") { defaultValue = "" })
        ) { backStackEntry ->
            val ytLink = backStackEntry.arguments?.getString("ytLink") ?: ""
            AddSongScreen(ytLink)
        }
        composable(Screens.SongControlScreen.route) {
            SongControlScreen()
        }
        composable(Screens.StepCounterScreen.route) {
            StepCounterScreen()
        }
        composable(Screens.LocationTrackerScreen.route) {
            LocationTrackerScreen()
        }
    }
}


