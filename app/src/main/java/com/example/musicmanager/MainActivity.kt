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


/**
 * The MainActivity class serves as the entry point for the Music Manager application.
 * It manages the lifecycle of the app, handles user intents, and binds to services.
 */
class MainActivity : ComponentActivity() {

    /**
     * ViewModel for database operations.
     * Provides access to the repository and authentication data repository.
     */
    val databaseViewModel: DatabaseViewModel by viewModels<DatabaseViewModel> {
        DatabaseViewModelFactory(
            (application as MusicManagerApplication).repository,
            (application as MusicManagerApplication).authDataRepository
        )
    }

    /**
     * ViewModel for step count operations.
     * Provides access to the step count repository.
     */
    val stepCountViewModel: StepCountViewModel by viewModels<StepCountViewModel> {
        StepCountViewModelFactory((application as MusicManagerApplication).stepCountRepository)
    }

    /**
     * Reference to the SongPlayerService instance.
     * Used for managing music playback.
     */
    var musicService: SongPlayerService? = null

    /**
     * Stores a pending intent to be handled later.
     */
    var pendingIntent: Intent? = null

    /**
     * Indicates whether the service is bound to the activity.
     */
    private var isBound = false

    /**
     * ServiceConnection implementation for binding to the SongPlayerService.
     * Manages the connection lifecycle and updates the music service with song data.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as SongPlayerService.SongPlayerBinder
            musicService = binder.getService()
            databaseViewModel.allSongs.observe(this@MainActivity) {
                musicService?.songs = it.toMutableList()
            }
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService?.songs = mutableListOf()
            isBound = false
        }
    }

    /**
     * List of screen routes that display a bottom navigation bar.
     */
    val screensWithBottomNav = listOf(
        Screens.SongScreen.route,
        Screens.PlaylistScreen.route,
        Screens.AddSongScreen.route,
        Screens.StepCounterScreen.route,
        Screens.LocationTrackerScreen.route
    )

    /**
     * List of screen routes that do not display playback controls.
     */
    val screensWithoutPlayback = listOf(
        Screens.SplashScreen.route,
        Screens.SongControlScreen.route
    )

    /**
     * Handles new intents received by the activity.
     * If the user is authenticated, the intent is processed immediately.
     * Otherwise, it is stored as a pending intent.
     *
     * @param intent The new intent to handle.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (databaseViewModel.userAuthenticated) {
            handleIntent(intent)
        } else {
            pendingIntent = intent
        }
    }

    /**
     * Processes the pending intent if one exists.
     */
    fun handlePendingIntent() {
        if (pendingIntent != null) {
            handleIntent(pendingIntent!!)
            pendingIntent = null
        }
    }

    /**
     * Processes the given intent.
     * Handles navigation and YouTube link sharing.
     *
     * @param intent The intent to process.
     */
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

    /**
     * Called when the activity is created.
     * Initializes services, permissions, and the UI.
     *
     * @param savedInstanceState The saved instance state.
     */
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.POST_NOTIFICATIONS,
                    android.Manifest.permission.ACTIVITY_RECOGNITION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                0
            )
        }
        Intent(this, SongPlayerService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        if (intent != null) {
            pendingIntent = intent
        }

        databaseViewModel.onAuthenticated = {
            handlePendingIntent()
        }

        startService(Intent(this, StepCounterService::class.java))

        MobileAds.initialize(this) {}

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
                            if (isBound && musicService?.currentSong?.value?.id != 0 && currentRoute !in screensWithoutPlayback) {
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

    /**
     * Called when the activity is destroyed.
     * Stops services and unbinds from the SongPlayerService.
     */
    override fun onDestroy() {
        val intent = Intent(this, SongPlayerService::class.java).apply {
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