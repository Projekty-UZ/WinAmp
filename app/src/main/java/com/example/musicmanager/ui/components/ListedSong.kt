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
import androidx.navigation.NavController
import com.example.musicmanager.R
import com.example.musicmanager.SongPlayerService
import com.example.musicmanager.database.models.Song
import com.example.musicmanager.navigation.Screens

/**
 * Composable function for rendering a song item in a list.
 * Displays song details such as title, artist, and icons for interaction.
 * Allows users to start playing the song and navigate to the song control screen.
 *
 * @param song The `Song` object containing details of the song to display.
 * @param navController The `NavController` used for navigation between screens.
 */
@Composable
fun ListedSong(song: Song, navController: NavController) {
    // Retrieve the current context for starting services and navigation.
    val context = LocalContext.current

    // Render a list item with clickable functionality to start the song and navigate.
    ListItem(
        modifier = Modifier.clickable {
            // Create an intent to start the SongPlayerService with song details.
            val playIntent = Intent(context, SongPlayerService::class.java).apply {
                action = SongPlayerService.Actions.START_SONG.toString()
                putExtra("pathToFile", song.pathToFile) // Path to the song file.
                putExtra("title", song.title) // Title of the song.
                putExtra("artist", song.artist) // Artist of the song.
                putExtra("id", song.id) // ID of the song.
                putExtra("duration", song.duration) // Duration of the song.
            }
            // Start the service to play the song.
            context.startService(playIntent)
            // Navigate to the song control screen.
            navController.navigate(Screens.SongControlScreen.route)
        },
        leadingContent = {
            // Display an icon representing the song.
            Icon(
                painter = painterResource(id = R.drawable.music_note),
                contentDescription = "Song Icon",
                modifier = Modifier.size(50.dp)
            )
        },
        headlineContent = {
            // Display the title of the song.
            Text(
                text = song.title,
            )
        },
        supportingContent = {
            // Display the artist of the song.
            Text(
                text = song.artist,
            )
        },
        trailingContent = {
            // Display an icon for additional options.
            Icon(
                painter = painterResource(id = R.drawable.more),
                contentDescription = "More Icon",
                modifier = Modifier.size(50.dp)
            )
        }
    )
    // Add a divider below the list item.
    Divider()
}

