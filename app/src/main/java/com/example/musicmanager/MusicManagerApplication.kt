package com.example.musicmanager

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.musicmanager.database.AppDatabase
import com.example.musicmanager.database.Repository
import com.example.musicmanager.database.repositories.StepCountRepository
import com.example.musicmanager.ui.viewModels.StepCountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MusicManagerApplication: Application(){
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { Repository(database.albumDao(),database.songDao(),database.songAlbumCrossDao()) }
    val stepCountRepository by lazy { StepCountRepository(database.stepCountDao()) }
}
