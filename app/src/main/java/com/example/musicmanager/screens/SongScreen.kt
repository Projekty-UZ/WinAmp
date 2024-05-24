package com.example.musicmanager.screens



import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.example.musicmanager.ui.components.ListedSong
import com.example.musicmanager.ui.viewModels.LocalDatabaseViewModel

@Composable
fun SongScreen() {
    val databaseViewModel = LocalDatabaseViewModel.current
    val allSongs = databaseViewModel.allSongs.observeAsState(emptyList())
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(allSongs.value) { song ->
            ListedSong(song)
        }
    }
}

