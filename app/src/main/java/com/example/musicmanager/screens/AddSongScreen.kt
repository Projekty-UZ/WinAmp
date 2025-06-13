package com.example.musicmanager.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.musicmanager.database.models.Song
import com.example.musicmanager.ui.viewModels.AddSongScreenViewModel
import com.example.musicmanager.ui.viewModels.DatabaseViewModel
import com.example.musicmanager.ui.viewModels.LocalDatabaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.musicmanager.R

/**
 * Composable function for rendering the Add Song screen.
 * Allows users to input a YouTube link, validate it, and download the song using a Python script.
 * Displays a progress indicator during the download process.
 *
 * @param yt_link A `String` representing the YouTube link to download the song from.
 */
@Composable
fun AddSongScreen(yt_link: String) {
    val databaseViewModel = LocalDatabaseViewModel.current // ViewModel for database operations.
    val coroutineScope = rememberCoroutineScope() // Coroutine scope for launching background tasks.

    val addSongScreenViewModel: AddSongScreenViewModel = viewModel() // ViewModel for managing UI state.
    LaunchedEffect(Unit) {
        // Update the YouTube link in the ViewModel if it's not empty.
        if (yt_link != "empty") {
            addSongScreenViewModel.yt_link.value = "https://www.youtube.com/watch?v=$yt_link"
        }
    }
    val context = LocalContext.current // Retrieve the current context for Android operations.
    if (!Python.isStarted()) {
        Python.start(AndroidPlatform(context)) // Start the Python interpreter if not already started.
    }
    val py = Python.getInstance() // Get the Python instance.
    val module = py.getModule("test") // Load the Python module named "test".

    // Main container for the screen layout.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TextField for entering the YouTube link.
            TextField(
                value = addSongScreenViewModel.yt_link.value,
                onValueChange = { addSongScreenViewModel.yt_link.value = it },
                placeholder = {
                    Text(stringResource(id = R.string.link_placeholder)) // Placeholder text for the TextField.
                },
                modifier = Modifier.width(300.dp),
                singleLine = true,
            )
            // Button for starting the download process.
            Button(
                enabled = !addSongScreenViewModel.loading.value, // Disable the button if loading is in progress.
                onClick = {
                    addSongScreenViewModel.loading.value = true // Set loading state to true.
                    addSongScreenViewModel.progress.floatValue = 0f // Reset progress value.
                    coroutineScope.launch {
                        // Start the Python script in a background thread.
                        withContext(Dispatchers.IO) {
                            python_script_button(module, addSongScreenViewModel.yt_link.value, context, addSongScreenViewModel, databaseViewModel)
                        }
                    }
                    addSongScreenViewModel.viewModelScope.launch {
                        // Monitor the progress of the Python script.
                        while (module.callAttr("get_progress").toFloat() == 1f) {
                            delay(100)
                        }
                        while (addSongScreenViewModel.loading.value) {
                            addSongScreenViewModel.progress.floatValue = module.callAttr("get_progress").toFloat()
                            Log.d("Progress", addSongScreenViewModel.progress.floatValue.toString())
                            delay(500)
                            if (addSongScreenViewModel.progress.floatValue == 1f) {
                                addSongScreenViewModel.loading.value = false // Set loading state to false.
                                addSongScreenViewModel.progress.floatValue = 0f // Reset progress value.
                            }
                        }
                    }
                },
            ) {
                Text(stringResource(id = R.string.download_song)) // Button text.
            }
            // Display a progress indicator if loading is in progress.
            if (addSongScreenViewModel.loading.value) {
                LinearProgressIndicator(
                    progress = addSongScreenViewModel.progress.floatValue,
                    modifier = Modifier.width(300.dp),
                )
            }
        }
    }
}

/**
 * Function for handling the Python script execution and updating the UI state.
 * Validates the YouTube link, downloads the song, and updates the database.
 *
 * @param module The `PyObject` representing the Python module.
 * @param yt_link A `String` representing the YouTube link to download the song from.
 * @param context The `Context` for Android operations.
 * @param addSongScreenViewModel The `AddSongScreenViewModel` for managing UI state.
 * @param databaseViewModel The `DatabaseViewModel` for database operations.
 */
fun python_script_button(module: PyObject, yt_link: String, context: Context, addSongScreenViewModel: AddSongScreenViewModel, databaseViewModel: DatabaseViewModel) {
    val validated = validate_input(yt_link) // Validate the YouTube link.
    if (validated) {
        download_from_yt(module, yt_link) // Call the Python function to download the song.
        val returnTable = module.callAttr("get_message").asList().map { it.toString() } // Retrieve the result from the Python script.
        if (returnTable[0] == "Downloaded") {
            databaseViewModel.viewModelScope.launch(Dispatchers.Main) {
                Toast.makeText(context, context.getString(R.string.download_success), Toast.LENGTH_SHORT).show() // Show success message.
            }
            val song = Song(
                id = 0,
                title = returnTable[1],
                artist = returnTable[2],
                duration = returnTable[3].toInt(),
                pathToFile = returnTable[4]
            )
            databaseViewModel.addSong(song) // Add the song to the database.
        } else {
            databaseViewModel.viewModelScope.launch(Dispatchers.Main) {
                Toast.makeText(context, context.getString(R.string.download_success), Toast.LENGTH_SHORT).show() // Show success message.
                addSongScreenViewModel.loading.value = false // Set loading state to false.
                addSongScreenViewModel.progress.floatValue = 0f // Reset progress value.
            }
        }
    } else {
        databaseViewModel.viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(context, context.getString(R.string.download_invalid_link), Toast.LENGTH_SHORT).show() // Show invalid link message.
            addSongScreenViewModel.loading.value = false // Set loading state to false.
            addSongScreenViewModel.progress.floatValue = 0f // Reset progress value.
        }
    }
}

/**
 * Function for downloading a song using a Python script.
 *
 * @param module The `PyObject` representing the Python module.
 * @param yt_link A `String` representing the YouTube link to download the song from.
 */
fun download_from_yt(module: PyObject, yt_link: String) {
    module.callAttr("download_from_yt", yt_link) // Call the Python function to download the song.
}

/**
 * Function for validating the YouTube link format.
 *
 * @param yt_link A `String` representing the YouTube link to validate.
 * @return `Boolean` indicating whether the link is valid.
 */
fun validate_input(yt_link: String): Boolean {
    val pattern = """^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\.com)?/.+""".toRegex() // Regex pattern for YouTube links.
    return pattern.matches(yt_link) // Check if the link matches the pattern.
}