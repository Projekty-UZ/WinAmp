package com.example.musicmanager

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.rememberNavController
import com.example.musicmanager.navigation.BottomNavBar
import com.example.musicmanager.navigation.BottomNavItem
import com.example.musicmanager.navigation.Navigation
import com.example.musicmanager.ui.theme.MusicManagerTheme
import com.example.musicmanager.ui.theme.viewModels.DatabaseViewModel
import com.example.musicmanager.ui.theme.viewModels.DatabaseViewModelFactory
import com.example.musicmanager.ui.theme.viewModels.LocalDatabaseViewModel


class MainActivity : ComponentActivity() {
    val databaseViewModel: DatabaseViewModel by viewModels<DatabaseViewModel>{
        DatabaseViewModelFactory((application as MusicManagerApplication).repository)
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicManagerTheme {
                CompositionLocalProvider (
                    LocalDatabaseViewModel provides databaseViewModel
                ){
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavBar(
                            items = listOf(
                                BottomNavItem("songs", "Songs", painterResource(id = R.drawable.music_note)),
                                BottomNavItem("playlists", "Playlists", painterResource(id = R.drawable.music_playlist)),
                                BottomNavItem("add_song", "Add Song", painterResource(id = R.drawable.add))
                            ),
                            navController = navController,
                            onItemClick = {
                                navController.navigate(it.route)
                            }
                        )
                    }

                ) {
                    Navigation(navController = navController)
                }
            }}
        }
    }
}