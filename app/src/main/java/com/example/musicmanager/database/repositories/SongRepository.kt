package com.example.musicmanager.database.repositories

import androidx.lifecycle.LiveData
import com.example.musicmanager.database.dao.SongDao
import com.example.musicmanager.database.models.Song

class SongRepository (private val songDao: SongDao){
    fun getAllSongs():LiveData<List<Song>> = songDao.getAllSongs()
    suspend fun getByQuery(query: String): LiveData<List<Song>> = songDao.getByQuery(query)
    suspend fun createSong(song: Song) = songDao.insert(song)
    suspend fun deleteSong(song: Song) = songDao.delete(song)
    suspend fun updateSong(song: Song) = songDao.update(song)
}