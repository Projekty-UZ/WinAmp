package com.example.musicmanager

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
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

    inner class SongPlayerBinder : Binder() {
        fun getService(): SongPlayerService = this@SongPlayerService

    }
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    override fun onUnbind(intent: Intent?): Boolean {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification(intent)
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
    }
    private fun pauseSong(){
        mediaPlayer?.pause()
        isplaying.value = false
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
    }
    private fun stopSong(){
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isplaying.value = false
    }
    private fun startSong(intent: Intent){
        mediaPlayer?.stop()
        mediaPlayer?.release()
        if(currentSong.value.id != 0) {
            songStack.add(currentSong.value)
        }

        if(songQueue.isNotEmpty()){
            currentSong.value = songQueue.removeAt(0)
            currentSongIndex.value = songs.indexOf(currentSong.value)
        }else {
            currentSong.value = Song(intent.getIntExtra("id",0),intent.getStringExtra("title")!!,intent.getStringExtra("artist")!!,intent.getIntExtra("duration",0),intent.getStringExtra("pathToFile")!!)
            currentSongIndex.value = songs.indexOf(currentSong.value)
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(currentSong.value.pathToFile)
            prepare()
            start()
            isplaying.value = true
            setOnCompletionListener { nextSong() }
        }
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isplaying.value = false
        super.onDestroy()
    }

    @SuppressLint("ResourceAsColor")
    private fun buildNotification(intent: Intent?): Notification {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel for Android 8.0 and above
            createNotificationChannel("SongPlayerChannel", "My Song Player Service Service")
        } else {
            // If earlier version, channel ID is not used
            ""
        }


        val notificationLayout = RemoteViews(packageName, R.layout.playback_notification)
        notificationLayout.setTextViewText(R.id.text_artist,  intent?.getStringExtra("artist"))
        notificationLayout.setTextViewText(R.id.text_song_title,"Playing: " + intent?.getStringExtra("title"))
        // Build the notification using NotificationCompat.Builder for backward compatibility
        val builder = NotificationCompat.Builder(this, channelId)
        builder.setSmallIcon(R.mipmap.ic_launcher)
            .setColor(R.color.purple_200)// Set the small icon for the notification
            .setContent(notificationLayout)
        return builder.build()                             // Build and return the notification
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

