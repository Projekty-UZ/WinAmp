package com.example.musicmanager.screens

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.musicmanager.BuildConfig
import com.example.musicmanager.R
import com.example.musicmanager.SongPlayerService
import com.example.musicmanager.ui.theme.Purple40
import com.example.musicmanager.ui.theme.Purple80
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Composable function for rendering the Song Control screen.
 * Provides functionality for controlling song playback, displaying song details, and interacting with a media player service.
 * Includes a slider for seeking within the song and buttons for playback controls (previous, play/pause, next).
 * Displays a banner ad at the bottom of the screen.
 */
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SongControlScreen() {
    // Gradient background for the screen.
    val brush = Brush.verticalGradient(colorStops = arrayOf(
        Pair(0.0f, Purple80),
        Pair(0.2f, Purple80),
        Pair(1.0f, Purple40)
    ))
    val context = LocalContext.current // Retrieve the current context.
    val musicService = remember { mutableStateOf<SongPlayerService?>(null) } // State to hold the music service instance.
    var progress by remember { mutableFloatStateOf(0.0f) } // State for the current progress of the song.
    var progressString by remember { mutableStateOf("00:00") } // State for the formatted progress time.
    var changingValue by remember { mutableStateOf(false) } // State to track if the slider value is being changed.

    // Effect to update the progress and formatted time periodically.
    LaunchedEffect(musicService) {
        while (true) {
            val mediaPlayer = musicService.value?.mediaPlayer
            if (mediaPlayer != null && !changingValue) {
                progress = mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()
                progressString = formatTime(mediaPlayer.currentPosition)
            }
            delay(1000) // Update every second.
        }
    }

    // Connect to the SongPlayerService and manage its lifecycle.
    DisposableEffect(Unit) {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as SongPlayerService.SongPlayerBinder
                musicService.value = binder.getService() // Retrieve the service instance.
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                musicService.value = null // Clear the service instance on disconnection.
            }
        }

        val intent = Intent(context, SongPlayerService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE) // Bind to the service.

        onDispose {
            context.unbindService(connection) // Unbind the service when the composable is disposed.
        }
    }

    // Main layout for the Song Control screen.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush), // Apply the gradient background.
        contentAlignment = Alignment.Center
    ) {
        // Display the album cover.
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Album Cover",
            modifier = Modifier.size(500.dp),
            tint = Color.Magenta
        )
        Column {
            // Display the song title.
            Text(
                text = musicService.value?.currentSong?.value?.title ?: "Song Title",
                color = Color.White,
                style = androidx.compose.ui.text.TextStyle(fontSize = 24.sp),
                modifier = Modifier.padding(8.dp, 500.dp, 0.dp, 0.dp)
            )
            // Display the artist name.
            Text(
                text = musicService.value?.currentSong?.value?.artist ?: "Artist",
                color = Color.White,
                style = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
                modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
            )
            Row {
                // Display the current progress time.
                Text(
                    text = progressString,
                    color = Color.White,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
                )
                Spacer(modifier = Modifier.weight(1f)) // Spacer for layout adjustment.
                // Display the total duration of the song.
                Text(
                    text = formatTime(musicService.value?.mediaPlayer?.duration ?: 0),
                    color = Color.White,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                )
            }
            // Slider for seeking within the song.
            Slider(
                value = progress,
                onValueChange = {
                    changingValue = true // Indicate that the slider value is being changed.
                    val intent = Intent(context, SongPlayerService::class.java).apply {
                        action = SongPlayerService.Actions.PAUSE.toString() // Pause the song during seeking.
                    }
                    context.startService(intent)
                    progress = it
                },
                onValueChangeFinished = {
                    changingValue = false // Indicate that the slider value change is finished.
                    musicService.value?.mediaPlayer?.seekTo((progress * musicService.value?.mediaPlayer?.duration!!).toInt()) // Seek to the new position.
                    val intent = Intent(context, SongPlayerService::class.java).apply {
                        action = SongPlayerService.Actions.PLAY.toString() // Resume playback.
                    }
                    context.startService(intent)
                },
                modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 0.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Button for playing the previous track.
                Button(
                    onClick = {
                        if (musicService.value != null) {
                            val intent = Intent(context, SongPlayerService::class.java).apply {
                                action = SongPlayerService.Actions.PREVIOUS.toString()
                            }
                            context.startService(intent)
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.previous_song_icon),
                        contentDescription = "Previous Track",
                        tint = Color.White
                    )
                }
                // Button for toggling play/pause state.
                Button(
                    onClick = {
                        if (musicService.value != null) {
                            if (musicService.value!!.isplaying.value) {
                                val intent = Intent(context, SongPlayerService::class.java).apply {
                                    action = SongPlayerService.Actions.PAUSE.toString()
                                }
                                context.startService(intent)
                            } else {
                                val intent = Intent(context, SongPlayerService::class.java).apply {
                                    action = SongPlayerService.Actions.PLAY.toString()
                                }
                                context.startService(intent)
                            }
                        }
                    }
                ) {
                    if (musicService.value != null) {
                        Icon(
                            painter = painterResource(id = if (musicService.value!!.isplaying.value) R.drawable.pause_song_icon else R.drawable.play_song_icon),
                            contentDescription = "Pause/Start Track",
                            tint = Color.White
                        )
                    }
                }
                // Button for playing the next track.
                Button(
                    onClick = {
                        if (musicService.value != null) {
                            val intent = Intent(context, SongPlayerService::class.java).apply {
                                action = SongPlayerService.Actions.NEXT.toString()
                            }
                            context.startService(intent)
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.next_song_icon),
                        contentDescription = "Next Track",
                        tint = Color.White
                    )
                }
            }
            // Display a banner ad at the bottom of the screen.
            BannerAd("ca-app-pub-3940256099942544/6300978111")
        }
    }
}

/**
 * Utility function for formatting time in milliseconds to a string in "MM:SS" format.
 *
 * @param milliseconds The time in milliseconds.
 * @return A formatted string representing the time.
 */
fun formatTime(milliseconds: Int): String {
    val minutes = (milliseconds / 1000) / 60
    val seconds = (milliseconds / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

/**
 * Composable function for rendering a banner ad.
 * Displays an AdMob banner ad with the specified ad unit ID.
 *
 * @param adUnitId The AdMob ad unit ID.
 */
@Composable
fun BannerAd(adUnitId: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    loadAd(AdRequest.Builder().build())
                }
            },
            update = { adView ->
                adView.loadAd(AdRequest.Builder().build())
            },
            modifier = Modifier.size(320.dp, 50.dp)
        )
    }
}

/**
 * Preview function for the Song Control screen.
 * Allows developers to preview the SongControlScreen composable in Android Studio's design editor.
 */
@Preview
@Composable
fun SongControlScreenPreview() {
    SongControlScreen()
}

/**
 * Preview function for the Banner Ad composable.
 * Allows developers to preview the BannerAd composable in Android Studio's design editor.
 * Uses the AdMob banner ID defined in the project's build configuration.
 */
@Preview
@Composable
fun BannerAdPreview() {
    BannerAd(BuildConfig.ADMOB_BANNER_ID)
}