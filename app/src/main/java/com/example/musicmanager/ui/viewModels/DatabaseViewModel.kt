package com.example.musicmanager.ui.viewModels


import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.musicmanager.database.Repository
import com.example.musicmanager.database.models.AuthData
import com.example.musicmanager.database.models.Song
import com.example.musicmanager.database.repositories.AuthDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * ViewModel for managing database operations and user authentication.
 * Provides methods for CRUD operations on songs and authentication data.
 *
 * @property Repository The repository for accessing song data.
 * @property authDataRepository The repository for accessing authentication data.
 */
class DatabaseViewModel(private val Repository: Repository, private val authDataRepository: AuthDataRepository) : ViewModel() {

    /**
     * LiveData containing a list of all songs in the database.
     */
    val allSongs: LiveData<List<Song>> = Repository.getAllSongs()

    /**
     * Indicates whether the user is authenticated.
     */
    var userAuthenticated = false

    /**
     * Callback to be invoked when the user is authenticated.
     */
    var onAuthenticated: (() -> Unit)? = null

    /**
     * Authenticates the user and invokes the `onAuthenticated` callback if set.
     */
    fun authenticateUser() {
        // Perform authentication logic
        userAuthenticated = true
        onAuthenticated?.invoke()
    }

    /**
     * Retrieves authentication data from the database.
     * This operation is performed on the IO dispatcher.
     *
     * @return The authentication data.
     */
    suspend fun getAuthData(): AuthData {
        return withContext(Dispatchers.IO) {
            authDataRepository.getAuthData()
        }
    }

    /**
     * Updates the authentication data in the database.
     *
     * @param password The new password.
     * @param recoveryEmail The new recovery email.
     */
    fun updateAuthData(password: String, recoveryEmail: String) {
        viewModelScope.launch {
            authDataRepository.updateAuthData(AuthData(1, password, recoveryEmail))
        }
    }

    /**
     * Inserts new authentication data into the database.
     *
     * @param password The password to insert.
     * @param recoveryEmail The recovery email to insert.
     */
    fun insertAuthData(password: String, recoveryEmail: String) {
        viewModelScope.launch {
            authDataRepository.insertAuthData(AuthData(1, password, recoveryEmail))
        }
    }

    /**
     * LiveData for displaying toast messages to the user.
     */
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    /**
     * Adds a new song to the database.
     *
     * @param song The song to add.
     */
    fun addSong(song: Song) {
        viewModelScope.launch {
            Repository.createSong(song)
        }
    }

    /**
     * Deletes a song from the database and its associated file from the filesystem.
     * Displays a toast message indicating success or failure.
     *
     * @param song The song to delete.
     */
    fun deleteSong(song: Song) {
        viewModelScope.launch {
            val file = File(song.pathToFile)
            val isDeleted = file.delete()
            if (isDeleted) {
                _toastMessage.value = "Song deleted"
                Repository.deleteSong(song)
            } else {
                _toastMessage.value = "Error deleting song"
            }
        }
    }

    /**
     * Updates an existing song in the database.
     *
     * @param song The song to update.
     */
    fun updateSong(song: Song) {
        viewModelScope.launch {
            Repository.updateSong(song)
        }
    }
}

/**
 * Factory for creating instances of `DatabaseViewModel`.
 *
 * @property repository The repository for accessing song data.
 * @property authDataRepository The repository for accessing authentication data.
 */
class DatabaseViewModelFactory(private val repository: Repository, private val authDataRepository: AuthDataRepository) : ViewModelProvider.Factory {
    /**
     * Creates an instance of `DatabaseViewModel`.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return An instance of `DatabaseViewModel`.
     * @throws IllegalArgumentException If the model class is not `DatabaseViewModel`.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DatabaseViewModel(repository, authDataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * CompositionLocal for providing a `DatabaseViewModel` instance.
 * Throws an error if no `DatabaseViewModel` is provided.
 */
val LocalDatabaseViewModel = staticCompositionLocalOf<DatabaseViewModel> {
    error("No DatabaseViewModel provided")
}