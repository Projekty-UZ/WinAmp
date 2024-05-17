package com.example.musicmanager.database.repositories

import androidx.lifecycle.LiveData
import com.example.musicmanager.database.dao.SongAlbumCrossDao
import com.example.musicmanager.database.models.Song
import com.example.musicmanager.database.models.SongAlbumCross

class SongAlbumRepository (private val songAlbumCrossDao: SongAlbumCrossDao){
    fun getSongsByAlbumId(albumId: Int) :LiveData<List<Song>> = songAlbumCrossDao.getSongsOfAlbum(albumId)
    fun getAllSongAlbums(): LiveData<List<SongAlbumCross>> = songAlbumCrossDao.getAllSongAlbums()
    suspend fun insertCross(songAlbumCross: SongAlbumCross) = songAlbumCrossDao.insert(songAlbumCross)
    suspend fun deleteCross(songAlbumCross: SongAlbumCross) = songAlbumCrossDao.delete(songAlbumCross)
    suspend fun updateCross(songAlbumCross: SongAlbumCross) = songAlbumCrossDao.update(songAlbumCross)
}