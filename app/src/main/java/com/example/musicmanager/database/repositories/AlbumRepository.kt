package com.example.musicmanager.database.repositories

import androidx.lifecycle.LiveData
import com.example.musicmanager.database.dao.AlbumDao
import com.example.musicmanager.database.models.Album

class AlbumRepository(private val albumDao: AlbumDao) {
    fun getAllAlbums():LiveData<List<Album>> = albumDao.getAllAlbums()
    fun getByName(name: String): LiveData<List<Album>> = albumDao.getByName(name)
    suspend fun createAlbum(album: Album) = albumDao.insert(album)
    suspend fun deleteAlbum(album: Album) = albumDao.delete(album)
    suspend fun updateAlbum(album: Album) = albumDao.update(album)
}