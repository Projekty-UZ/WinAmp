package com.example.musicmanager.screens

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
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
import com.example.musicmanager.R
import com.example.musicmanager.SongPlayerService
import com.example.musicmanager.ui.theme.Purple40
import com.example.musicmanager.ui.theme.Purple80
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SongControlScreen() {
    val brush = Brush.verticalGradient(colorStops = arrayOf(
        Pair(0.0f, Purple80),
        Pair(0.2f, Purple80),
        Pair(1.0f, Purple40)
    ))
    val context = LocalContext.current
    val musicService = remember { mutableStateOf<SongPlayerService?>(null) }
    var progress by remember { mutableFloatStateOf(0.0f) }
    var progressString by remember { mutableStateOf("00:00") }
    val coroutineScope = rememberCoroutineScope()
    var changingValue by remember { mutableStateOf(false) }

    coroutineScope.launch {
        while (true) {
            if (musicService.value != null && musicService.value!!.mediaPlayer != null && !changingValue){
                progress = musicService.value!!.mediaPlayer!!.currentPosition.toFloat() / musicService.value!!.mediaPlayer!!.duration.toFloat()
                progressString = formatTime(musicService.value!!.mediaPlayer!!.currentPosition)
            }
            delay(1000)
        }
    }


    // Connect to the SongPlayerService
    DisposableEffect(Unit) {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as SongPlayerService.SongPlayerBinder
                musicService.value = binder.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                musicService.value = null
            }
        }

        val intent = Intent(context, SongPlayerService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)

        onDispose {
            context.unbindService(connection)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Album Cover",
            modifier = Modifier.size(500.dp),
            tint = Color.Magenta
        )
        Column {
            Text(
                text = musicService.value?.currentSong?.value?.title ?: "Song Title",
                color = Color.White,
                style = androidx.compose.ui.text.TextStyle(fontSize = 24.sp),
                modifier = Modifier.padding(8.dp, 500.dp, 0.dp, 0.dp)
            )
            Text(
                text = musicService.value?.currentSong?.value?.artist ?: "Artist",
                color = Color.White,
                style = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
                modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
            )
            Row(

            ) {
                Text(
                    text = progressString,
                    color = Color.White,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatTime(musicService.value?.mediaPlayer?.duration ?: 0),
                    color = Color.White,
                    style = androidx.compose.ui.text.TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                )
            }
            Slider(value = progress,
                onValueChange = {
                    changingValue = true
                    val Intent = Intent(context, SongPlayerService::class.java).apply {
                        action = SongPlayerService.Actions.PAUSE.toString()
                    }
                    context.startService(Intent)
                    progress=it
                },
                onValueChangeFinished ={
                    changingValue = false
                    musicService.value?.mediaPlayer?.seekTo((progress * musicService.value?.mediaPlayer?.duration!!).toInt())
                    val Intent = Intent(context, SongPlayerService::class.java).apply {
                        action = SongPlayerService.Actions.PLAY.toString()
                    }
                    context.startService(Intent)
                } ,
                modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 0.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ){
                Button(
                    onClick = {
                         if(musicService.value != null){
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
                Button(
                    onClick = {
                        if(musicService.value != null){
                            if(musicService.value!!.isplaying.value) {
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
                Button(
                    onClick = {
                         if(musicService.value != null){
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
        }
    }

}
fun formatTime(milliseconds: Int): String {
    val minutes = (milliseconds / 1000) / 60
    val seconds = (milliseconds / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}


@Preview
@Composable
fun SongControlScreenPreview() {
    SongControlScreen()
}