package com.example.musicmanager.ui.theme.viewModels

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.musicmanager.database.Repository
import com.example.musicmanager.database.models.Song
import kotlinx.coroutines.launch

class DatabaseViewModel(private val Repository: Repository) : ViewModel() {
    val allSongs: LiveData<List<Song>> = Repository.getAllSongs()
    fun addSong(song: Song) {
        viewModelScope.launch {
            Repository.createSong(song)
        }
    }
    fun deleteSong(song: Song) {
        viewModelScope.launch {
            Repository.deleteSong(song)
        }
    }
    fun updateSong(song: Song) {
        viewModelScope.launch {
            Repository.updateSong(song)
        }
    }

}
class DatabaseViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DatabaseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

val LocalDatabaseViewModel = staticCompositionLocalOf<DatabaseViewModel> {
    error("No DatabaseViewModel provided")
}