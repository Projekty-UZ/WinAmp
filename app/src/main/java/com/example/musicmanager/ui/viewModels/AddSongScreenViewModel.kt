package com.example.musicmanager.ui.viewModels

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * ViewModel for managing the state of the Add Song screen.
 * Provides reactive state variables for YouTube link input, loading status, and progress tracking.
 */
class AddSongScreenViewModel : ViewModel() {

    /**
     * State variable for storing the YouTube link entered by the user.
     * Default value is an empty string.
     */
    var yt_link = mutableStateOf("")

    /**
     * State variable for tracking the loading status of the screen.
     * Default value is false, indicating no loading is in progress.
     */
    var loading = mutableStateOf(false)

    /**
     * State variable for tracking the progress of an operation (e.g., downloading or uploading).
     * Default value is 0.0f, representing no progress.
     */
    var progress = mutableFloatStateOf(0f)
}