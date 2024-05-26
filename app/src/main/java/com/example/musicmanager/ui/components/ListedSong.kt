package com.example.musicmanager.ui.components


import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.musicmanager.MainActivity
import com.example.musicmanager.R
import com.example.musicmanager.SongPlayerService
import com.example.musicmanager.database.models.Song

@Composable
fun ListedSong(song: Song){
    val context = LocalContext.current as MainActivity
    ListItem(
        modifier = Modifier.clickable{
            val playIntent = Intent(context, SongPlayerService::class.java).apply {
                action = SongPlayerService.Actions.START_SONG.toString()
                putExtra("pathToFile", song.pathToFile)
                putExtra("title", song.title)
                putExtra("artist", song.artist)
                putExtra("id", song.id)
                putExtra("duration", song.duration)
            }
            context.startService(playIntent)
        },
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.music_note),
                contentDescription = "Song Icon",
                modifier = Modifier.size(50.dp)
            )
        },
        headlineContent = {
            Text(
                text = song.title,
            )
        },
        supportingContent = {
            Text(
                text = song.artist,
            )
        },
        trailingContent = {
            Icon(
                painter = painterResource(id = R.drawable.more),
                contentDescription = "More Icon",
                modifier = Modifier.size(50.dp)
            )
        }
    )
    Divider()
}

