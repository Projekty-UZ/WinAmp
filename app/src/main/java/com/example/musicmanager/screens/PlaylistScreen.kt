package com.example.musicmanager.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Composable function for rendering the Playlist screen.
 * Displays a centered text indicating the Playlist screen.
 *
 * This function uses a `Box` layout to center the content both vertically and horizontally.
 */
@Composable
fun PlaylistScreen() {
    Box(
        modifier = Modifier.fillMaxSize(), // Modifier to make the Box fill the entire screen.
        contentAlignment = Alignment.Center // Align the content to the center of the Box.
    ) {
        Text("Playlist Screen") // Text displayed in the center of the screen.
    }
}