package com.example.musicmanager.database

import com.example.musicmanager.database.dao.AlbumDao
import com.example.musicmanager.database.dao.SongAlbumCrossDao
import com.example.musicmanager.database.dao.SongDao
import com.example.musicmanager.database.models.Song

/**
 * Repository class for managing database operations related to albums, songs, and their relationships.
 * Provides methods to retrieve, create, update, and delete songs, as well as access album and song-album data.
 *
 * @property albumDao Data Access Object for album-related operations.
 * @property songDao Data Access Object for song-related operations.
 * @property songAlbumCrossDao Data Access Object for song-album relationship operations.
 */
class Repository(
    private val albumDao: AlbumDao,
    private val songDao: SongDao,
    private val songAlbumCrossDao: SongAlbumCrossDao
) {
    /**
     * Retrieves all albums from the database.
     */
    val albums = albumDao.getAllAlbums()

    /**
     * Retrieves all songs from the database.
     */
    val songs = songDao.getAllSongs()

    /**
     * Retrieves all song-album relationships from the database.
     */
    val songAlbumCrosses = songAlbumCrossDao.getAllSongAlbums()

    /**
     * Retrieves all songs from the database.
     *
     * @return A list of all songs.
     */
    fun getAllSongs() = songDao.getAllSongs()

    /**
     * Retrieves a song based on a query string.
     *
     * @param query The query string to search for songs.
     * @return A list of songs matching the query.
     */
    suspend fun getSongByQuery(query: String) = songDao.getByQuery(query)

    /**
     * Inserts a new song into the database.
     *
     * @param song The song to be inserted.
     */
    suspend fun createSong(song: Song) = songDao.insert(song)

    /**
     * Updates an existing song in the database.
     *
     * @param song The song to be updated.
     */
    suspend fun updateSong(song: Song) = songDao.update(song)

    /**
     * Deletes a song from the database.
     *
     * @param song The song to be deleted.
     */
    suspend fun deleteSong(song: Song) = songDao.delete(song)
}