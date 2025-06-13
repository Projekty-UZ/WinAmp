package com.example.musicmanager

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.telephony.TelephonyManager
import android.util.Log
import android.view.KeyEvent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.example.musicmanager.database.models.Song
import com.example.musicmanager.navigation.NavigationEventHolder
import com.example.musicmanager.navigation.Screens
import kotlin.or
import kotlin.text.toLong

/**
 * The SongPlayerService class is responsible for managing music playback in the application.
 * It handles media playback, audio focus, notifications, and interactions with the MediaSession API.
 */
class SongPlayerService : Service() {

    /**
     * MediaPlayer instance for playing audio files.
     */
    var mediaPlayer: MediaPlayer? = null

    /**
     * Binder instance for binding the service to activities.
     */
    private val binder = SongPlayerBinder()

    /**
     * Tracks whether a song is currently playing.
     */
    var isplaying = mutableStateOf(false)

    /**
     * List of songs available for playback.
     */
    var songs = mutableListOf<Song>()

    /**
     * The currently playing song.
     */
    var currentSong = mutableStateOf(Song(0, "", "", 0, ""))

    /**
     * Index of the currently playing song in the songs list.
     */
    var currentSongIndex = mutableStateOf(0)

    /**
     * Queue of songs to be played next.
     */
    var songQueue = mutableListOf<Song>()

    /**
     * Stack of previously played songs for navigation.
     */
    var songStack = mutableListOf<Song>()

    /**
     * AudioManager instance for managing audio focus.
     */
    private lateinit var audioManager: AudioManager

    /**
     * BroadcastReceiver for handling phone call interruptions.
     */
    private lateinit var phoneCallReceiver: PhoneCallReceiver

    /**
     * MediaSession instance for handling media controls.
     */
    private lateinit var mediaSession: MediaSession

    companion object {
        /**
         * Action for broadcasting playback state updates.
         */
        const val ACTION_UPDATE_STATE = "com.example.music.UPDATE_STATE"

        /**
         * Extra key for indicating whether a song is playing.
         */
        const val EXTRA_IS_PLAYING = "isPlaying"

        /**
         * Extra key for the title of the currently playing song.
         */
        const val EXTRA_CURRENT_SONG = "currentSong"

        /**
         * Extra key for the artist of the currently playing song.
         */
        const val EXTRA_ARTIST = "artist"
    }

    /**
     * Called when the service is created.
     * Initializes the AudioManager, MediaSession, and registers the phone call receiver.
     */
    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        phoneCallReceiver = PhoneCallReceiver()
        registerReceiver(
            phoneCallReceiver,
            IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        )

        mediaSession = MediaSession(this, "SongPlayerService")
        mediaSession.setMediaButtonReceiver(null)
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession.setCallback(object : MediaSession.Callback() {

            /**
             * Handles media button events such as play, pause, next, and previous.
             *
             * @param mediaButtonIntent The intent containing the media button event.
             * @return True if the event was handled, false otherwise.
             */
            override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                val keyEvent =
                    mediaButtonIntent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                keyEvent?.let {
                    Log.d("SongPlayerService", "Media button pressed: ${it.keyCode} , ${it.action}")
                    if (it.action == KeyEvent.ACTION_DOWN) {
                        when (it.keyCode) {
                            KeyEvent.KEYCODE_MEDIA_PLAY -> playSong()
                            KeyEvent.KEYCODE_MEDIA_PAUSE -> pauseSong()
                            KeyEvent.KEYCODE_MEDIA_NEXT -> nextSong()
                            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> previousSong()
                        }
                    }
                }
                return super.onMediaButtonEvent(mediaButtonIntent)
            }
        })

        // Activate the MediaSession
        mediaSession.isActive = true
    }

    /**
     * AudioFocusRequest instance for managing audio focus changes.
     */
    @SuppressLint("NewApi")
    val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setOnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> pauseSong()
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pauseSong()
                AudioManager.AUDIOFOCUS_GAIN -> playSong()
            }
        }
        .build()

    /**
     * BroadcastReceiver for handling phone call interruptions.
     * Pauses playback during phone calls and resumes afterward.
     */
    class PhoneCallReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                if (state == TelephonyManager.EXTRA_STATE_RINGING || state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                    SongPlayerService().pauseSong()
                } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                    SongPlayerService().playSong()
                }
            }
        }
    }

    /**
     * Broadcasts the current playback state.
     *
     * @param title The title of the currently playing song (optional).
     * @param artist The artist of the currently playing song (optional).
     */
    private fun broadcastState(title: String? = null, artist: String? = null) {
        val intent = Intent(ACTION_UPDATE_STATE).apply {
            putExtra(EXTRA_IS_PLAYING, isplaying.value)
            putExtra(EXTRA_CURRENT_SONG, title ?: currentSong.value.title)
            putExtra(EXTRA_ARTIST, artist ?: currentSong.value.artist)
        }
        sendBroadcast(intent)
    }

    /**
     * Binder class for providing access to the service instance.
     */
    inner class SongPlayerBinder : Binder() {
        fun getService(): SongPlayerService = this@SongPlayerService
    }

    /**
     * Called when the service is bound to an activity.
     *
     * @param intent The intent used to bind the service.
     * @return The binder instance for the service.
     */
    @SuppressLint("InlinedApi")
    override fun onBind(intent: Intent?): IBinder {
        broadcastState()
        return binder
    }

    /**
     * Called when the service is unbound from an activity.
     * Stops and releases the MediaPlayer instance.
     *
     * @param intent The intent used to unbind the service.
     * @return True if the service should remain bound, false otherwise.
     */
    override fun onUnbind(intent: Intent?): Boolean {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        broadcastState("Unknown Song", "Unknown Artist")
        return super.onUnbind(intent)
    }

    /**
     * Called when the service receives a start command.
     * Handles playback actions and updates the notification.
     *
     * @param intent The intent containing the action to perform.
     * @param flags Additional flags for the start command.
     * @param startId The start ID for the command.
     * @return The start mode for the service.
     */
    @SuppressLint("NewApi")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification(currentSong.value.title, currentSong.value.artist)
        startForeground(1, notification)

        when (intent?.action) {
            Actions.PLAY.toString() -> {
                val result = audioManager.requestAudioFocus(audioFocusRequest)
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    playSong()
                }
            }

            Actions.PAUSE.toString() -> pauseSong()
            Actions.NEXT.toString() -> nextSong()
            Actions.PREVIOUS.toString() -> previousSong()
            Actions.STOP.toString() -> stopSong()
            Actions.START_SONG.toString() -> {
                val result = audioManager.requestAudioFocus(audioFocusRequest)
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    startSong(intent)
                } else {
                    NavigationEventHolder.navigateTo(Screens.SongScreen.route)
                }
            }

            Actions.STOP_SERVICE.toString() -> stopSelf()
        }

        return START_STICKY
    }

    /**
     * Updates the playback state in the MediaSession.
     *
     * @param state The playback state to set.
     */
    private fun updatePlaybackState(state: Int) {
        mediaPlayer?.let {
            val playbackState = PlaybackState.Builder()
                .setState(state, it.currentPosition.toLong(), 1f)
                .setActions(
                    PlaybackState.ACTION_PLAY or
                            PlaybackState.ACTION_PAUSE or
                            PlaybackState.ACTION_SKIP_TO_NEXT or
                            PlaybackState.ACTION_SKIP_TO_PREVIOUS
                )
                .build()
            mediaSession.setPlaybackState(playbackState)
        } ?: Log.e("SongPlayerService", "MediaPlayer is null in updatePlaybackState")
    }

    /**
     * Starts playback of the current song.
     */
    private fun playSong() {
        mediaPlayer?.start()
        isplaying.value = true
        Log.d("SongPlayerService", "Playing song ${isplaying.value}")
        updatePlaybackState(PlaybackState.STATE_PLAYING)
        broadcastState()
        updateNotification()
    }

    /**
     * Pauses playback of the current song.
     */
    private fun pauseSong() {
        mediaPlayer?.pause()
        isplaying.value = false
        Log.d("SongPlayerService", "Playing song ${isplaying.value}")
        updatePlaybackState(PlaybackState.STATE_PAUSED)
        broadcastState()
        updateNotification()
    }

    /**
     * Skips to the next song in the playlist.
     */
    private fun nextSong() {
        updatePlaybackState(PlaybackState.STATE_SKIPPING_TO_NEXT)
        mediaPlayer?.stop()
        mediaPlayer?.release()

        songStack.add(currentSong.value)

        val newindex = (currentSongIndex.value + 1) % songs.size
        currentSongIndex.value = newindex
        currentSong.value = songs[currentSongIndex.value]

        mediaPlayer = MediaPlayer().apply {
            setDataSource(currentSong.value.pathToFile)
            prepare()
            start()
            isplaying.value = true
            setOnCompletionListener { nextSong() }
        }
        updatePlaybackState(PlaybackState.STATE_PLAYING)
        broadcastState()
        updateNotification()
    }

    /**
     * Skips to the previous song in the playlist or stack.
     */
    private fun previousSong() {
        updatePlaybackState(PlaybackState.STATE_SKIPPING_TO_PREVIOUS)
        mediaPlayer?.stop()
        mediaPlayer?.release()

        if (songStack.isNotEmpty()) {
            currentSong.value = songStack.removeAt(songStack.size - 1)
            currentSongIndex.value = songs.indexOf(currentSong.value)
        } else {
            currentSongIndex.value = (0 until songs.size).random()
            currentSong.value = songs[currentSongIndex.value]
        }
        mediaPlayer = MediaPlayer().apply {
            setDataSource(currentSong.value.pathToFile)
            prepare()
            start()
            isplaying.value = true
            setOnCompletionListener { nextSong() }
        }
        updatePlaybackState(PlaybackState.STATE_PLAYING)
        broadcastState()
        updateNotification()
    }

    /**
     * Stops playback and releases the MediaPlayer instance.
     */
    private fun stopSong() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isplaying.value = false
        broadcastState("Unknown Song", "Unknown Artist")
    }

    /**
     * Starts playback of a specific song based on the provided intent.
     *
     * @param intent The intent containing song details.
     */
    private fun startSong(intent: Intent) {
        mediaPlayer?.stop()
        mediaPlayer?.release()

        if (currentSong.value.id != 0) {
            songStack.add(currentSong.value)
        }

        if (songQueue.isNotEmpty()) {
            currentSong.value = songQueue.removeAt(0)
            currentSongIndex.value = songs.indexOf(currentSong.value)
        } else {
            currentSong.value = Song(
                intent.getIntExtra("id", 0),
                intent.getStringExtra("title")!!,
                intent.getStringExtra("artist")!!,
                intent.getIntExtra("duration", 0),
                intent.getStringExtra("pathToFile")!!
            )
            currentSongIndex.value = songs.indexOf(currentSong.value)
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(currentSong.value.pathToFile)
            prepare()
            start()
            isplaying.value = true
            setOnCompletionListener { nextSong() }
        }
        broadcastState()
        updateNotification()
    }

    /**
     * Called when the service is destroyed.
     * Stops playback, releases resources, and unregisters the phone call receiver.
     */
    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        mediaSession.release()
        isplaying.value = false
        unregisterReceiver(phoneCallReceiver)
        super.onDestroy()
    }

    /**
     * Updates the notification with the current song details.
     */
    private fun updateNotification() {
        val notification = buildNotification(currentSong.value.title, currentSong.value.artist)
        startForeground(1, notification)
    }

    /**
     * Builds a notification for the music player service.
     * The notification includes actions for play/pause, previous, and next, and displays the current song's title and artist.
     *
     * @param title The title of the currently playing song.
     * @param artist The artist of the currently playing song.
     * @return A Notification object configured for the music player service.
     */
    private fun buildNotification(title: String?, artist: String?): Notification {
        // Create notification channel for Android O and above
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("SongPlayerChannel", "My Song Player Service")
        } else {
            ""
        }

        // Create actions for play/pause, previous, and next
        val playPauseAction = NotificationCompat.Action(
            if (isplaying.value == true) R.drawable.pause_song_icon else R.drawable.play_song_icon,
            if (isplaying.value == true) "Pause" else "Play",
            createPlayPausePendingIntent()
        )

        val previousAction = NotificationCompat.Action(
            R.drawable.previous_song_icon,
            "Previous",
            createActionPendingIntent(Actions.PREVIOUS)
        )

        val nextAction = NotificationCompat.Action(
            R.drawable.next_song_icon,
            "Next",
            createActionPendingIntent(Actions.NEXT)
        )
        val mediaSession = MediaSessionCompat(this, "SongPlayerService")
        val largeIconBitmap =
            BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)

        // Build the notification
        return NotificationCompat.Builder(this, channelId).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle(title)
            setContentText(artist)
            setLargeIcon(largeIconBitmap) // Replace with your large icon logic
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(
                        0,
                        1,
                        2
                    ) // Show play/pause, previous, and next in compact view
                    .setMediaSession(mediaSession.sessionToken)
            )
            addAction(previousAction)
            addAction(playPauseAction)
            addAction(nextAction)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setOngoing(true) // Make the notification non-removable
            setContentIntent(createNotificationPendingIntent())
        }.build()
    }

    /**
     * Creates a PendingIntent for the play/pause action in the notification.
     *
     * @return A PendingIntent for toggling play/pause functionality.
     */
    private fun createPlayPausePendingIntent(): PendingIntent {
        val intent = Intent(this, SongPlayerService::class.java).apply {
            action =
                if (isplaying.value == true) Actions.PAUSE.toString() else Actions.PLAY.toString()
        }
        return PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Creates a PendingIntent for a specific action (e.g., PREVIOUS, NEXT).
     *
     * @param action The action to be performed (e.g., PREVIOUS, NEXT).
     * @return A PendingIntent for the specified action.
     */
    private fun createActionPendingIntent(action: Actions): PendingIntent {
        val intent = Intent(this, SongPlayerService::class.java).apply {
            this.action = action.toString()
        }
        return PendingIntent.getService(
            this,
            action.ordinal, // Use action ordinal as request code to ensure unique PendingIntents
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Creates a PendingIntent for the notification content (to open the app).
     *
     * @return A PendingIntent for opening the app from the notification.
     */
    private fun createNotificationPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("NAVIGATE_TO_SONG_CONTROL", true) // Add a flag to indicate navigation
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Creates a notification channel for Android O and above.
     *
     * @param channelId The ID of the notification channel.
     * @param channelName The name of the notification channel.
     * @return The ID of the created notification channel.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        // Define the properties of the notification channel
        val chan = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = 0xFFD0BCFF.toInt()                  // Set the notification light color
        chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC// Set lock screen visibility

        // Get the notification manager system service
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Create the notification channel
        service.createNotificationChannel(chan)

        return channelId                                  // Return the channel ID
    }

    /**
     * Enum class representing various actions for the music player service.
     */
    enum class Actions {
        START_SONG,
        ADD_SONG_QUEUE,
        ADD_PLAYLIST_QUEUE,
        STOP,
        PLAY,
        PAUSE,
        NEXT,
        PREVIOUS,
        STOP_SERVICE
    }
}



