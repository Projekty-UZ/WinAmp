package com.example.musicmanager

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.musicmanager.navigation.BottomNavBar
import com.example.musicmanager.navigation.BottomNavItem
import com.example.musicmanager.navigation.Navigation
import com.example.musicmanager.ui.theme.MusicManagerTheme
import com.example.musicmanager.ui.theme.SongViewModel
import com.example.musicmanager.ui.theme.SongViewModelFactory

class MainActivity : ComponentActivity() {
    val songViewModel: SongViewModel by viewModels<SongViewModel>{
        SongViewModelFactory((application as MusicManagerApplication).repository)
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicManagerTheme {
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
                    Navigation(navController = navController,songViewModel)
                }
            }
        }
    }
}