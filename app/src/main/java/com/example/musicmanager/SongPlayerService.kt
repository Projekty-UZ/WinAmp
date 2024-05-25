package com.example.musicmanager

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class SongPlayerService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer?.stop()
        mediaPlayer?.release()

        val musicFilePath = intent?.getStringExtra("pathToFile") ?: return START_NOT_STICKY
        mediaPlayer = MediaPlayer().apply {
            setDataSource(musicFilePath)
            prepare()
            start()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}


