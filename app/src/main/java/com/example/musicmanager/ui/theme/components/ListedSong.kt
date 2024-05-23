package com.example.musicmanager.ui.theme.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicmanager.R
import com.example.musicmanager.database.models.Song

@Composable
fun ListedSong(song: Song){
    ListItem(
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

@Preview
@Composable
fun PreviewlistedSong(){
    ListedSong(Song(1,"Humble","Kendrick Lamar",333,"test"))
    ListedSong(Song(1,"Humble","Kendrick Lamar",333,"test"))
}
