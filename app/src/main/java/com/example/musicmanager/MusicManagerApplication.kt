package com.example.musicmanager

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.musicmanager.database.AppDatabase
import com.example.musicmanager.database.Repository
import com.example.musicmanager.database.repositories.AuthDataRepository
import com.example.musicmanager.database.repositories.StepCountRepository
import com.example.musicmanager.ui.viewModels.StepCountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * The MusicManagerApplication class is the main Application class for the Music Manager app.
 * It initializes application-wide resources such as the database and repositories.
 */
class MusicManagerApplication : Application() {

    /**
     * A CoroutineScope tied to the application's lifecycle.
     * Used for managing coroutines that should live as long as the application is running.
     */
    val applicationScope = CoroutineScope(SupervisorJob())

    /**
     * Lazy initialization of the AppDatabase instance.
     * The database is created using the application context and the applicationScope.
     */
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }

    /**
     * Lazy initialization of the Repository instance.
     * Provides access to DAOs for albums, songs, and their cross-references.
     */
    val repository by lazy { Repository(database.albumDao(), database.songDao(), database.songAlbumCrossDao()) }

    /**
     * Lazy initialization of the AuthDataRepository instance.
     * Manages authentication-related data using the AuthData DAO.
     */
    val authDataRepository by lazy { AuthDataRepository(database.authDataDao()) }

    /**
     * Lazy initialization of the StepCountRepository instance.
     * Manages step count data using the StepCount DAO.
     */
    val stepCountRepository by lazy { StepCountRepository(database.stepCountDao()) }
}