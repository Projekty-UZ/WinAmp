package com.example.musicmanager.ui.widgets

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.musicmanager.R
import com.example.musicmanager.SongPlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.glance.appwidget.updateAll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.glance.ColorFilter
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import kotlinx.coroutines.flow.first

/**
 * A GlanceAppWidget implementation for controlling music playback.
 * Displays the current song, artist, and playback controls (play/pause, next, previous).
 */
class MusicControlWidget : GlanceAppWidget() {

    /**
     * Defines the state management for the widget using PreferencesGlanceStateDefinition.
     */
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    /**
     * Provides the content for the widget and updates its state.
     * Registers a BroadcastReceiver to listen for updates from the SongPlayerService.
     *
     * @param context The application context.
     * @param id The unique identifier for the widget instance.
     */
    @SuppressLint("NewApi", "CoroutineCreationDuringComposition")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Register a BroadcastReceiver to listen for state updates
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d("Received intent", intent.toString())
                if (intent.action == SongPlayerService.ACTION_UPDATE_STATE) {
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.d("MusicControlWidget", "Received state update")
                        saveState(context, intent)
                        updateAll(context)
                    }
                }
            }
        }
        context.registerReceiver(receiver, IntentFilter(SongPlayerService.ACTION_UPDATE_STATE),
            Context.RECEIVER_EXPORTED)

        // Read preferences from DataStore
        val preferences = context.dataStore.data.first()
        updateAppWidgetState(context, id) { prefs ->
            prefs[PreferencesKeys.IS_PLAYING] = preferences[PreferencesKeys.IS_PLAYING] ?: false
            prefs[PreferencesKeys.CURRENT_SONG] = preferences[PreferencesKeys.CURRENT_SONG] ?: "Unknown Song"
            prefs[PreferencesKeys.ARTIST] = preferences[PreferencesKeys.ARTIST] ?: "Unknown Artist"
        }

        // Provide the widget content
        provideContent {
            MusicControlContent(context)
        }
    }

    /**
     * Composable function to define the UI content of the music control widget.
     * Displays the album cover, song title, artist name, and playback controls.
     *
     * @param context The application context.
     */
    @SuppressLint("RestrictedApi")
    @Composable
    private fun MusicControlContent(context: Context) {
        val preferences = currentState<Preferences>()

        // State variables for widget content
        var isPlaying by remember { mutableStateOf(preferences[PreferencesKeys.IS_PLAYING] ?: false) }
        var currentSong by remember { mutableStateOf(preferences[PreferencesKeys.CURRENT_SONG] ?: "Unknown Song") }
        var artist by remember { mutableStateOf(preferences[PreferencesKeys.ARTIST] ?: "Unknown Artist") }

        // Update state when preferences change
        LaunchedEffect(Unit) {
            context.dataStore.data.collect { newPreferences ->
                isPlaying = newPreferences[PreferencesKeys.IS_PLAYING] ?: false
                currentSong = newPreferences[PreferencesKeys.CURRENT_SONG] ?: "Unknown Song"
                artist = newPreferences[PreferencesKeys.ARTIST] ?: "Unknown Artist"
            }
        }

        // Widget layout
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ImageProvider(R.drawable.ic_launcher_background))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Album Cover
            Image(
                provider = ImageProvider(R.drawable.ic_launcher_foreground),
                contentDescription = "Album Cover",
                modifier = GlanceModifier.size(50.dp)
            )
            if (currentSong == "Unknown Song") {
                Text(
                    text = context.getString(R.string.no_song_widget),
                    style = TextStyle(color = ColorProvider(R.color.white), fontSize = 16.sp)
                )
            } else {
                // Song Title
                Text(
                    text = currentSong,
                    style = TextStyle(color = ColorProvider(R.color.white), fontSize = 16.sp)
                )

                // Artist Name
                Text(
                    text = artist,
                    style = TextStyle(color = ColorProvider(R.color.white), fontSize = 14.sp)
                )

                // Control Buttons
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Button
                    Image(
                        provider = ImageProvider(R.drawable.previous_song_icon),
                        contentDescription = "Previous",
                        modifier = GlanceModifier
                            .size(48.dp)
                            .clickable(actionRunCallback<PreviousAction>()),
                        colorFilter = ColorFilter.tint(ColorProvider(R.color.white))
                    )

                    // Play/Pause Button
                    Image(
                        provider = ImageProvider(if (isPlaying) R.drawable.pause_song_icon else R.drawable.play_song_icon),
                        contentDescription = "Play/Pause",
                        modifier = GlanceModifier
                            .size(48.dp)
                            .clickable(actionRunCallback<PlayPauseAction>()),
                        colorFilter = ColorFilter.tint(ColorProvider(R.color.white))
                    )

                    // Next Button
                    Image(
                        provider = ImageProvider(R.drawable.next_song_icon),
                        contentDescription = "Next",
                        modifier = GlanceModifier
                            .size(48.dp)
                            .clickable(actionRunCallback<NextAction>()),
                        colorFilter = ColorFilter.tint(ColorProvider(R.color.white))
                    )
                }
            }
        }
    }
}

/**
 * Handles the play/pause action for the music control widget.
 * Toggles the playback state by sending an intent to the SongPlayerService.
 */
class PlayPauseAction : ActionCallback {
    /**
     * Called when the play/pause action is triggered.
     * Reads the current playback state from the DataStore and sends the appropriate intent to the SongPlayerService.
     *
     * @param context The application context.
     * @param glanceId The unique identifier for the widget instance.
     * @param parameters Additional parameters for the action (not used here).
     */
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val preferences = context.dataStore.data.first()
        val isPlaying = preferences[PreferencesKeys.IS_PLAYING] ?: false

        val intent = Intent(context, SongPlayerService::class.java).apply {
            action = if (isPlaying) SongPlayerService.Actions.PAUSE.toString()
            else SongPlayerService.Actions.PLAY.toString()
        }
        context.startService(intent)
    }
}

/**
 * Handles the next action for the music control widget.
 * Sends an intent to the SongPlayerService to skip to the next song.
 */
class NextAction : ActionCallback {
    /**
     * Called when the next action is triggered.
     * Sends an intent to the SongPlayerService to play the next song.
     *
     * @param context The application context.
     * @param glanceId The unique identifier for the widget instance.
     * @param parameters Additional parameters for the action (not used here).
     */
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val intent = Intent(context, SongPlayerService::class.java).apply {
            action = SongPlayerService.Actions.NEXT.toString()
        }
        context.startService(intent)
    }
}

/**
 * Handles the previous action for the music control widget.
 * Sends an intent to the SongPlayerService to skip to the previous song.
 */
class PreviousAction : ActionCallback {
    /**
     * Called when the previous action is triggered.
     * Sends an intent to the SongPlayerService to play the previous song.
     *
     * @param context The application context.
     * @param glanceId The unique identifier for the widget instance.
     * @param parameters Additional parameters for the action (not used here).
     */
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val intent = Intent(context, SongPlayerService::class.java).apply {
            action = SongPlayerService.Actions.PREVIOUS.toString()
        }
        context.startService(intent)
    }
}

/**
 * A receiver for the MusicControlWidget.
 * Associates the widget with its implementation class.
 */
class MusicControlWidgetReceiver : GlanceAppWidgetReceiver() {
    /**
     * Specifies the GlanceAppWidget implementation for this receiver.
     */
    override val glanceAppWidget: GlanceAppWidget = MusicControlWidget()
}

/**
 * Object containing keys for accessing preferences in the DataStore.
 */
object PreferencesKeys {
    /**
     * Key for storing the playback state (playing or paused).
     */
    val IS_PLAYING = booleanPreferencesKey("isPlaying")

    /**
     * Key for storing the title of the currently playing song.
     */
    val CURRENT_SONG = stringPreferencesKey("currentSong")

    /**
     * Key for storing the artist of the currently playing song.
     */
    val ARTIST = stringPreferencesKey("artist")
}

/**
 * Extension property for accessing the DataStore instance in the application context.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_state")

/**
 * Saves the current playback state to the DataStore.
 * Updates the widget state based on the received intent.
 *
 * @param context The application context.
 * @param intent The intent containing playback state information.
 */
private suspend fun saveState(context: Context, intent: Intent) {
    Log.d("MusicControlWidget", "Saving state")
    Log.d("MusicControlWidget", "Is playing: ${intent.getBooleanExtra(SongPlayerService.EXTRA_IS_PLAYING, false)}")
    Log.d("MusicControlWidget", "Current song: ${intent.getStringExtra(SongPlayerService.EXTRA_CURRENT_SONG) ?: "Unknown Song"}")
    Log.d("MusicControlWidget", "Artist: ${intent.getStringExtra(SongPlayerService.EXTRA_ARTIST) ?: "Unknown Artist"}")
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.IS_PLAYING] =
            intent.getBooleanExtra(SongPlayerService.EXTRA_IS_PLAYING, false)
        preferences[PreferencesKeys.CURRENT_SONG] =
            intent.getStringExtra(SongPlayerService.EXTRA_CURRENT_SONG) ?: "Unknown Song"
        preferences[PreferencesKeys.ARTIST] =
            intent.getStringExtra(SongPlayerService.EXTRA_ARTIST) ?: "Unknown Artist"
    }
    MusicControlWidget().updateAll(context)
}