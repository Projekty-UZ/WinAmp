package com.example.musicmanager.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.musicmanager.screens.AddSongScreen
import com.example.musicmanager.screens.PlaylistScreen
import com.example.musicmanager.screens.SongScreen
import com.example.musicmanager.ui.theme.viewModels.DatabaseViewModel

@Composable
fun Navigation(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = "songs",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable("songs") {
            SongScreen()
        }
        composable("playlists") {
            PlaylistScreen()
        }
        composable("add_song") {
            AddSongScreen(navController)
        }
    }
}


