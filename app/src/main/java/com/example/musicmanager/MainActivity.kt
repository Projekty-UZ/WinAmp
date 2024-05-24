package com.example.musicmanager

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicmanager.ui.components.BottomNavBar
import com.example.musicmanager.navigation.BottomNavItem
import com.example.musicmanager.navigation.Navigation
import com.example.musicmanager.navigation.Screens
import com.example.musicmanager.ui.theme.MusicManagerTheme
import com.example.musicmanager.ui.viewModels.DatabaseViewModel
import com.example.musicmanager.ui.viewModels.DatabaseViewModelFactory
import com.example.musicmanager.ui.viewModels.LocalDatabaseViewModel


class MainActivity : ComponentActivity() {
    val databaseViewModel: DatabaseViewModel by viewModels<DatabaseViewModel>{
        DatabaseViewModelFactory((application as MusicManagerApplication).repository)
    }
    val screensWithBottomNav= listOf(
        Screens.SongScreen.route,
        Screens.PlaylistScreen.route,
        Screens.AddSongScreen.route
    )
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicManagerTheme {
                CompositionLocalProvider (
                    LocalDatabaseViewModel provides databaseViewModel
                ){
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry?.destination?.route
                Scaffold(
                        bottomBar = {
                            if(currentRoute in screensWithBottomNav) {
                                BottomNavBar(
                                    items = listOf(
                                        BottomNavItem(
                                            Screens.SongScreen.route,
                                            "Songs",
                                            painterResource(id = R.drawable.music_note)
                                        ),
                                        BottomNavItem(
                                            Screens.PlaylistScreen.route,
                                            "Playlists",
                                            painterResource(id = R.drawable.music_playlist)
                                        ),
                                        BottomNavItem(
                                            Screens.AddSongScreen.route,
                                            "Add Song",
                                            painterResource(id = R.drawable.add)
                                        )
                                    ),
                                    navController = navController,
                                    onItemClick = {
                                        navController.navigate(it.route)
                                    }
                                )
                            }
                    }

                ) {
                    Navigation(navController = navController)
                }
            }}
        }
    }
}