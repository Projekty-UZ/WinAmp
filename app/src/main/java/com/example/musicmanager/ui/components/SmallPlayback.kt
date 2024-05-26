package com.example.musicmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.musicmanager.R
import com.example.musicmanager.SongPlayerService
import com.example.musicmanager.navigation.Screens
import com.example.musicmanager.ui.theme.Purple40

@Composable
fun SmallPlayback(navController: NavController,musicService: SongPlayerService) {
    val isPlaying = remember { mutableStateOf(true) }

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
                Text(
                    text = musicService.currentSong.value.title,
                    color = Color.Black
                )
                Text(
                    text = musicService.currentSong.value.artist,
                    color = Color.Gray
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO: Implement previous track action */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.previous_song_icon),
                        contentDescription = "Previous Track",
                        tint = Color.Black
                    )
                }

                IconButton(onClick = { isPlaying.value = !isPlaying.value }) {
                    Icon(
                        painter = painterResource(if (isPlaying.value) R.drawable.pause_song_icon else R.drawable.play_song_icon),
                        contentDescription = if (isPlaying.value) "Pause" else "Play",
                        tint = Color.Black
                    )
                }

                IconButton(onClick = { /* TODO: Implement next track action */ }) {
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
