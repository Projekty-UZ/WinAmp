package com.example.musicmanager.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.musicmanager.ui.components.ListedSong
import com.example.musicmanager.ui.viewModels.LocalDatabaseViewModel

/**
 * Composable function for rendering the Song Screen.
 * Displays a list of songs retrieved from the local database using a LazyColumn.
 * Each song is represented by the `ListedSong` composable, which includes navigation functionality.
 *
 * @param navController The NavController used for handling navigation between screens.
 */
@Composable
fun SongScreen(navController: NavController) {
    // Retrieve the current instance of the LocalDatabaseViewModel.
    val databaseViewModel = LocalDatabaseViewModel.current

    // Observe the list of all songs from the database as a state.
    val allSongs = databaseViewModel.allSongs.observeAsState(emptyList())

    // Display the list of songs using a LazyColumn.
    LazyColumn(
        modifier = Modifier.fillMaxSize(), // Modifier to make the LazyColumn fill the entire screen.
    ) {
        // Iterate through the list of songs and display each one using the ListedSong composable.
        items(allSongs.value) { song ->
            ListedSong(song, navController)
        }
    }
}



