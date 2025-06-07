package com.example.musicmanager

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicmanager.ui.components.BottomNavBar
import com.example.musicmanager.navigation.BottomNavItem
import com.example.musicmanager.navigation.Navigation
import com.example.musicmanager.navigation.NavigationEventHolder
import com.example.musicmanager.navigation.Screens
import com.example.musicmanager.ui.components.SmallPlayback
import com.example.musicmanager.ui.theme.MusicManagerTheme
import com.example.musicmanager.ui.viewModels.DatabaseViewModel
import com.example.musicmanager.ui.viewModels.DatabaseViewModelFactory
import com.example.musicmanager.ui.viewModels.LocalDatabaseViewModel
import com.example.musicmanager.ui.viewModels.StepCountViewModel
import com.example.musicmanager.ui.viewModels.StepCountViewModelFactory
import androidx.compose.runtime.CompositionLocalProvider
import com.example.musicmanager.ui.viewModels.LocalStepCountViewModel
import com.google.android.gms.ads.MobileAds


class MainActivity : ComponentActivity() {
    val databaseViewModel: DatabaseViewModel by viewModels<DatabaseViewModel>{
        DatabaseViewModelFactory((application as MusicManagerApplication).repository, (application as MusicManagerApplication).authDataRepository)
    }
    val stepCountViewModel: StepCountViewModel by viewModels<StepCountViewModel>{
        StepCountViewModelFactory((application as MusicManagerApplication).stepCountRepository)
    }
    var musicService: SongPlayerService? = null

    var pendingIntent: Intent? = null

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
        Screens.AddSongScreen.route,
        Screens.StepCounterScreen.route,
        Screens.LocationTrackerScreen.route
    )

    val screensWithoutPlayback= listOf(
        Screens.SplashScreen.route,
        Screens.SongControlScreen.route
    )



    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if(databaseViewModel.userAuthenticated){
            handleIntent(intent)
        }else{
            pendingIntent = intent
        }
    }

     fun handlePendingIntent() {
        // Check if there is a pending intent
        if (pendingIntent != null) {
            // Handle the pending intent
            handleIntent(pendingIntent!!)
            // Clear the pending intent
            pendingIntent = null
        }
    }

    private fun handleIntent(intent: Intent) {
        Log.d("INTENT", "received $intent")
        Log.d("INTENT", intent.toString())
        if (intent.getBooleanExtra("NAVIGATE_TO_SONG_CONTROL", false) == true) {
            NavigationEventHolder.navigateTo(Screens.SongControlScreen.route)
        }
        if (intent.action == Intent.ACTION_SEND) {
            if (intent.type == "text/plain") {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)

                // Regex to check if the shared text is a YouTube link
                val youtubeRegex = Regex("^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+")

                if (sharedText != null && youtubeRegex.matches(sharedText)) {
                    // Check if the link is a playlist (contains "list=")
                    if (sharedText.contains("playlist?list")) {
                        Toast.makeText(this, getString(R.string.playlist_toast), Toast.LENGTH_SHORT).show()
                    } else {
                        // Extract the video ID
                        val videoId = if (sharedText.contains("v=")) {
                            sharedText.split("v=")[1].substringBefore("&")
                        } else {
                            sharedText.split("/").last()
                        }

                        if (videoId.isNotEmpty()) {
                            NavigationEventHolder.navigateTo(Screens.AddSongScreen.createRoute(videoId))
                        } else {
                            Toast.makeText(this, getString(R.string.intent_id_toast), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.not_yt_link_toast), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
           ActivityCompat.requestPermissions(
               this,
               arrayOf(android.Manifest.permission.POST_NOTIFICATIONS,
                   android.Manifest.permission.ACTIVITY_RECOGNITION,
                   android.Manifest.permission.ACCESS_FINE_LOCATION,
                   android.Manifest.permission.ACCESS_COARSE_LOCATION),
               0
           )
        }
        Intent(this, SongPlayerService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        if(intent != null){
            pendingIntent = intent
        }

        databaseViewModel.onAuthenticated = {
            handlePendingIntent()
        }

        startService(Intent(this, StepCounterService::class.java))

        MobileAds.initialize(this){}

            setContent {
                MusicManagerTheme {
                    CompositionLocalProvider(
                        LocalDatabaseViewModel provides databaseViewModel,
                        LocalStepCountViewModel provides stepCountViewModel
                    ) {
                        val navController = rememberNavController()
                        val currentBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = currentBackStackEntry?.destination?.route
                        Scaffold(
                            snackbarHost = {
                                if(isBound && musicService?.currentSong?.value?.id != 0 && currentRoute !in screensWithoutPlayback){
                                    SmallPlayback(navController = navController, musicService = musicService!!)
                                }
                            },
                            bottomBar = {

                                if (currentRoute in screensWithBottomNav) {
                                    BottomNavBar(
                                        items = listOf(
                                            BottomNavItem(
                                                Screens.SongScreen.route,
                                                stringResource(id = R.string.songs),
                                                painterResource(id = R.drawable.music_note)
                                            ),
                                            BottomNavItem(
                                                Screens.PlaylistScreen.route,
                                                stringResource(id = R.string.playlists),
                                                painterResource(id = R.drawable.music_playlist)
                                            ),
                                            BottomNavItem(
                                                Screens.AddSongScreen.createRoute("empty"),
                                                stringResource(id = R.string.add_song),
                                                painterResource(id = R.drawable.add)
                                            ),
                                            BottomNavItem(
                                                Screens.StepCounterScreen.route,
                                                stringResource(id = R.string.step_cunter),
                                                painterResource(id = R.drawable.health)
                                            ),
                                            BottomNavItem(
                                                Screens.LocationTrackerScreen.route,
                                                stringResource(id = R.string.gps),
                                                painterResource(id = R.drawable.location)
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
        val stepCounterIntent = Intent(this, StepCounterService::class.java)
        stopService(stepCounterIntent)
        super.onDestroy()
    }
}