package com.example.musicmanager

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicmanager.ui.components.BottomNavBar
import com.example.musicmanager.navigation.BottomNavItem
import com.example.musicmanager.navigation.Navigation
import com.example.musicmanager.navigation.Screens
import com.example.musicmanager.ui.components.SmallPlayback
import com.example.musicmanager.ui.theme.MusicManagerTheme
import com.example.musicmanager.ui.viewModels.DatabaseViewModel
import com.example.musicmanager.ui.viewModels.DatabaseViewModelFactory
import com.example.musicmanager.ui.viewModels.LocalDatabaseViewModel


class MainActivity : ComponentActivity() {
    val databaseViewModel: DatabaseViewModel by viewModels<DatabaseViewModel>{
        DatabaseViewModelFactory((application as MusicManagerApplication).repository)
    }
    var musicService: SongPlayerService? = null

    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SongPlayerService.SongPlayerBinder
            musicService = binder.getService()
            databaseViewModel.allSongs.observe(this@MainActivity){
                musicService?.songs = it.toMutableList()
            }
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService?.songs = mutableListOf()
            isBound = false
        }
    }

    val screensWithBottomNav= listOf(
        Screens.SongScreen.route,
        Screens.PlaylistScreen.route,
        Screens.AddSongScreen.route
    )

    val screensWithoutPlayback= listOf(
        Screens.SplashScreen.route,
        Screens.SongControlScreen.route
    )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
           ActivityCompat.requestPermissions(
               this,
               arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
               0
           )
        }
        Intent(this, SongPlayerService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

            setContent {
                MusicManagerTheme {
                    CompositionLocalProvider(
                        LocalDatabaseViewModel provides databaseViewModel
                    ) {
                        val navController = rememberNavController()
                        val currentBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = currentBackStackEntry?.destination?.route
                        Scaffold(
                            snackbarHost = {
                                if(isBound && musicService?.isplaying?.value == true && currentRoute !in screensWithoutPlayback){
                                    SmallPlayback(navController = navController, musicService = musicService!!)
                                }
                            },
                            bottomBar = {

                                if (currentRoute in screensWithBottomNav) {
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
                    }
                }
            }

    }
    override fun onDestroy() {
        val intent=Intent(this, SongPlayerService::class.java).apply{
            action = SongPlayerService.Actions.STOP_SERVICE.toString()
        }
        startService(intent)
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        super.onDestroy()
    }
}