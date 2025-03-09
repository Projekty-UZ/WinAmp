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

class DatabaseViewModel(private val Repository: Repository,private val authDataRepository: AuthDataRepository) : ViewModel() {
    val allSongs: LiveData<List<Song>> = Repository.getAllSongs()
    var userAuthenticated = false
    var onAuthenticated: (() -> Unit)? = null

    fun authenticateUser() {
        // Perform authentication logic
        userAuthenticated = true
        onAuthenticated?.invoke()
    }

    suspend fun getAuthData(): AuthData {
        return withContext(Dispatchers.IO) {
            authDataRepository.getAuthData()
        }
    }

    fun updateAuthData(password: String, recoveryEmail: String) {
        viewModelScope.launch {
            authDataRepository.updateAuthData(AuthData(1, password, recoveryEmail))
        }
    }

    fun insertAuthData(password: String, recoveryEmail: String) {
        viewModelScope.launch {
            authDataRepository.insertAuthData(AuthData(1, password, recoveryEmail))
        }
    }
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage:LiveData<String> get() = _toastMessage
    fun addSong(song: Song) {
        viewModelScope.launch {
            Repository.createSong(song)
        }
    }
    fun deleteSong(song: Song) {
        viewModelScope.launch {
            val file = File(song.pathToFile)
            val isdeleted = file.delete()
            if(isdeleted) {
                _toastMessage.value = "Song deleted"
                Repository.deleteSong(song)
            }else{
                _toastMessage.value = "Error deleting song"
            }
        }
    }
    fun updateSong(song: Song) {
        viewModelScope.launch {
            Repository.updateSong(song)
        }
    }

}
class DatabaseViewModelFactory(private val repository: Repository, private val authDataRepository: AuthDataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DatabaseViewModel(repository,authDataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

val LocalDatabaseViewModel = staticCompositionLocalOf<DatabaseViewModel> {
    error("No DatabaseViewModel provided")
}