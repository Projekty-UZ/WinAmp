package com.example.musicmanager.ui.widgets

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
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
import kotlinx.coroutines.coroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.glance.ColorFilter
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.currentState
import kotlinx.coroutines.flow.first

class MusicControlWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

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

        val preferences = context.dataStore.data.first() // Read from DataStore
        updateAppWidgetState(context, id) { prefs ->
            prefs[PreferencesKeys.IS_PLAYING] = preferences[PreferencesKeys.IS_PLAYING] ?: false
            prefs[PreferencesKeys.CURRENT_SONG] = preferences[PreferencesKeys.CURRENT_SONG] ?: "Unknown Song"
            prefs[PreferencesKeys.ARTIST] = preferences[PreferencesKeys.ARTIST] ?: "Unknown Artist"
        }

        provideContent {

            MusicControlContent(context)
        }
    }


    @SuppressLint("RestrictedApi")
    @Composable
    private fun MusicControlContent(context: Context) {
        val preferences = currentState<Preferences>()

        var isPlaying by remember { mutableStateOf(preferences[PreferencesKeys.IS_PLAYING] ?: false) }
        var currentSong by remember { mutableStateOf(preferences[PreferencesKeys.CURRENT_SONG] ?: "Unknown Song") }
        var artist by remember { mutableStateOf(preferences[PreferencesKeys.ARTIST] ?: "Unknown Artist") }

        LaunchedEffect(Unit) {
            context.dataStore.data.collect { newPreferences ->
                isPlaying = newPreferences[PreferencesKeys.IS_PLAYING] ?: false
                currentSong = newPreferences[PreferencesKeys.CURRENT_SONG] ?: "Unknown Song"
                artist = newPreferences[PreferencesKeys.ARTIST] ?: "Unknown Artist"
            }
        }

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
                        provider = ImageProvider(if(isPlaying) R.drawable.pause_song_icon else R.drawable.play_song_icon),
                        contentDescription = "Play/Pause",
                        modifier = GlanceModifier
                            .size(48.dp)
                            .clickable(if(isPlaying) actionRunCallback<PlayAction>() else actionRunCallback<PauseAction>()),
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

class PlayAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        // Handle play/pause action
        val intent = Intent(context, SongPlayerService::class.java).apply {
            action = SongPlayerService.Actions.PLAY.toString()
        }
        context.startService(intent)
    }
}

class PauseAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        // Handle play/pause action
        val intent = Intent(context, SongPlayerService::class.java).apply {
            action = SongPlayerService.Actions.PAUSE.toString()
        }
        context.startService(intent)
    }
}

class NextAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        // Handle next action
        val intent = Intent(context, SongPlayerService::class.java).apply {
            action = SongPlayerService.Actions.NEXT.toString()
        }
        context.startService(intent)
    }
}

class PreviousAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        // Handle previous action
        val intent = Intent(context, SongPlayerService::class.java).apply {
            action = SongPlayerService.Actions.PREVIOUS.toString()
        }
        context.startService(intent)
    }
}

class MusicControlWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MusicControlWidget()
}

object PreferencesKeys {
    val IS_PLAYING = booleanPreferencesKey("isPlaying")
    val CURRENT_SONG = stringPreferencesKey("currentSong")
    val ARTIST = stringPreferencesKey("artist")
}


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_state")

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