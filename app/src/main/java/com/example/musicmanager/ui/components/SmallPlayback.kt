package com.example.musicmanager.ui.components

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.musicmanager.R
import com.example.musicmanager.SongPlayerService
import com.example.musicmanager.navigation.Screens
import com.example.musicmanager.ui.theme.Purple40
import kotlinx.coroutines.delay

@Composable
fun SmallPlayback(navController: NavController,musicService: SongPlayerService) {
    val isPlaying = remember { mutableStateOf(true) }
    val context = LocalContext.current

    var textWidth by remember { mutableStateOf(0) }
    var containerWidth by remember { mutableStateOf(0) }
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(textWidth, containerWidth) {
        if (textWidth > containerWidth) {
            while (true) {
                offsetX.animateTo(
                    targetValue = -(textWidth - containerWidth).toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = (textWidth / 100 * 1000).toInt(),
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {  navController.navigate(Screens.SongControlScreen.route) }
            .zIndex(1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
                .background(color = Purple40)

        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Album Cover",
                modifier = Modifier.size(50.dp),
                tint = Color.Magenta
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                MarqueeText(musicService.currentSong.value.title, modifier = Modifier.fillMaxWidth(),Color.Black)
                MarqueeText(musicService.currentSong.value.artist, modifier = Modifier.fillMaxWidth(),Color.Gray)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val intent = Intent(context, SongPlayerService::class.java).apply {
                        action = SongPlayerService.Actions.PREVIOUS.toString()
                    }
                    context.startService(intent)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.previous_song_icon),
                        contentDescription = "Previous Track",
                        tint = Color.Black
                    )
                }

                IconButton(onClick = {
                    if(musicService.isplaying.value) {
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
                }) {
                    Icon(
                        painter = painterResource(if (musicService.isplaying.value) R.drawable.pause_song_icon else R.drawable.play_song_icon),
                        contentDescription = if (isPlaying.value) "Pause" else "Play",
                        tint = Color.Black
                    )
                }

                IconButton(onClick = {
                    val intent = Intent(context, SongPlayerService::class.java).apply {
                        action = SongPlayerService.Actions.NEXT.toString()
                    }
                    context.startService(intent)
                }) {
                    Icon(
                        painter= painterResource(id = R.drawable.next_song_icon),
                        contentDescription = "Next Track",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun MarqueeText(text: String, modifier : Modifier = Modifier, color: Color = Color.Black) {
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        while (true) {
            scrollState.animateScrollTo(scrollState.maxValue, animationSpec = tween(durationMillis = text.length * 200, easing = LinearEasing))
            delay(1000)
            scrollState.animateScrollTo(0, animationSpec = tween(durationMillis = text.length * 200, easing = LinearEasing))
            delay(1000)
        }
    }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState, reverseScrolling = false)
                .width(IntrinsicSize.Max)
        ) {
            Text(text = text, color = color, fontSize = 16.sp)
        }
    }
}
