package com.example.musicmanager

import android.app.Application
import com.example.musicmanager.database.AppDatabase
import com.example.musicmanager.database.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MusicManagerApplication: Application(){
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { Repository(database.albumDao(),database.songDao(),database.songAlbumCrossDao()) }
}
