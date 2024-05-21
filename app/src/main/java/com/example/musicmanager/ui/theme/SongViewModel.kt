package com.example.musicmanager.ui.theme

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.musicmanager.database.Repository
import com.example.musicmanager.database.models.Song
import kotlinx.coroutines.launch

class SongViewModel(private val songRepository: Repository) : ViewModel() {
    val allSongs: LiveData<List<Song>> = songRepository.getAllSongs()
    fun addSong(song: Song) {
        viewModelScope.launch {
            songRepository.createSong(song)
        }
    }
    fun deleteSong(song: Song) {
        viewModelScope.launch {
            songRepository.deleteSong(song)
        }
    }
    fun updateSong(song: Song) {
        viewModelScope.launch {
            songRepository.updateSong(song)
        }
    }

}
class SongViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SongViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SongViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
