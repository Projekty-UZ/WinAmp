package com.example.musicmanager.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.musicmanager.screens.AddSongScreen
import com.example.musicmanager.screens.AnimatedSplashScreen
import com.example.musicmanager.screens.PlaylistScreen
import com.example.musicmanager.screens.SongScreen

@Composable
fun Navigation(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(Screens.SongScreen.route) {
            SongScreen()
        }
        composable(Screens.SplashScreen.route) {
            AnimatedSplashScreen(navController = navController)
        }
        composable(Screens.PlaylistScreen.route) {
            PlaylistScreen()
        }
        composable(Screens.AddSongScreen.route) {
            AddSongScreen()
        }
    }
}


