package com.example.musicmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.example.musicmanager.database.models.Song

class SongPlayerService : Service() {
    var mediaPlayer: MediaPlayer? = null
    private val binder = SongPlayerBinder()
    var isplaying = mutableStateOf(false)
    var songs = mutableListOf<Song>()
    var currentSong = mutableStateOf(Song(0,"","",0,""))
    var currentSongIndex = mutableStateOf(0)
    var songQueue = mutableListOf<Song>()
    var songStack = mutableListOf<Song>()

    companion object {
        const val ACTION_UPDATE_STATE = "com.example.music.UPDATE_STATE"
        const val EXTRA_IS_PLAYING = "isPlaying"
        const val EXTRA_CURRENT_SONG = "currentSong"
        const val EXTRA_ARTIST = "artist"
    }

    // Example method to broadcast state changes
    private fun broadcastState(title: String? = null, artist: String? = null) {
        val intent = Intent(ACTION_UPDATE_STATE).apply {
            putExtra(EXTRA_IS_PLAYING, isplaying.value)
            putExtra(EXTRA_CURRENT_SONG, title ?: currentSong.value.title)
            putExtra(EXTRA_ARTIST, artist ?: currentSong.value.artist)
        }
        sendBroadcast(intent)
    }

    // Call this method whenever the state changes

    inner class SongPlayerBinder : Binder() {
        fun getService(): SongPlayerService = this@SongPlayerService

    }
    override fun onBind(intent: Intent?): IBinder {
        broadcastState()
        return binder
    }
    override fun onUnbind(intent: Intent?): Boolean {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        broadcastState("Unknown Song", "Unknown Artist")
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification(currentSong.value.title, currentSong.value.artist)
        startForeground(1, notification)

        when(intent?.action){
            Actions.PLAY.toString() -> playSong()
            Actions.PAUSE.toString() -> pauseSong()
            Actions.NEXT.toString() -> nextSong()
            Actions.PREVIOUS.toString() -> previousSong()
            Actions.STOP.toString() -> stopSong()
            Actions.START_SONG.toString() -> startSong(intent)
            Actions.STOP_SERVICE.toString() -> stopSelf()
        }

        return START_STICKY
    }


    private fun playSong(){
        mediaPlayer?.start()
        isplaying.value = true
        Log.d("SongPlayerService", "Playing song ${isplaying.value}")
        broadcastState()
        updateNotification()
    }
    private fun pauseSong(){
        mediaPlayer?.pause()
        isplaying.value = false
        Log.d("SongPlayerService", "Playing song ${isplaying.value}")
        broadcastState()
        updateNotification()

    }
    private fun nextSong(){
        mediaPlayer?.stop()
        mediaPlayer?.release()

        //dodaj na stos ostatnio odtwarzanych piosenek
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
        broadcastState()
        updateNotification()
    }
    private fun previousSong() {
        mediaPlayer?.stop()
        mediaPlayer?.release()

        if (songStack.isNotEmpty()) {
            currentSong.value = songStack.removeAt(songStack.size - 1)
            currentSongIndex.value = songs.indexOf(currentSong.value)

        }
        else{
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
        broadcastState()
        updateNotification()
    }
    private fun stopSong(){
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isplaying.value = false
        broadcastState("Unknown Song", "Unknown Artist")
    }
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

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isplaying.value = false
        super.onDestroy()
    }

    private fun updateNotification() {
        val notification = buildNotification(currentSong.value.title, currentSong.value.artist)
        startForeground(1, notification)
    }

    // update notification every second



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
        val largeIconBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)

        // Build the notification
        return NotificationCompat.Builder(this, channelId).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle(title)
            setContentText(artist)
            setLargeIcon(largeIconBitmap) // Replace with your large icon logic
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2) // Show play/pause, previous, and next in compact view
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

    private fun createPlayPausePendingIntent(): PendingIntent {
        val intent = Intent(this, SongPlayerService::class.java).apply {
            action = if (isplaying.value == true) Actions.PAUSE.toString() else Actions.PLAY.toString()
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

    enum class Actions{
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



