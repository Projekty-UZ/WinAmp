package com.example.musicmanager.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicmanager.R
import com.example.musicmanager.database.models.Song
import com.example.musicmanager.ui.theme.viewModels.LocalDatabaseViewModel

@Composable
fun SongScreen() {
    val databaseViewModel = LocalDatabaseViewModel.current
    val allSongs = databaseViewModel.allSongs.observeAsState(emptyList())
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        allSongs.value.forEach {
            listedSong(it)
        }
    }
}

@Composable
fun listedSong(song:Song){
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
fun previewlistedSong(){
    listedSong(Song(1,"Humble","Kendrick Lamar",333,"test"))
    listedSong(Song(1,"Humble","Kendrick Lamar",333,"test"))
}
