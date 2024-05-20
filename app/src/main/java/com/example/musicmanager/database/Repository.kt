package com.example.musicmanager.database

import com.example.musicmanager.database.dao.AlbumDao
import com.example.musicmanager.database.dao.SongAlbumCrossDao
import com.example.musicmanager.database.dao.SongDao
import com.example.musicmanager.database.models.Song

class Repository(
    private val albumDao: AlbumDao,
    private val songDao: SongDao,
    private val songAlbumCrossDao: SongAlbumCrossDao
) {
    val albums = albumDao.getAllAlbums()
    val songs = songDao.getAllSongs()
    val songAlbumCrosses = songAlbumCrossDao.getAllSongAlbums()
    fun getAllSongs() = songDao.getAllSongs()
    suspend fun getSongByQuery(query: String) = songDao.getByQuery(query)
    suspend fun createSong(song: Song) = songDao.insert(song)
}