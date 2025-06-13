package com.example.musicmanager.database.repositories

import androidx.lifecycle.LiveData
import com.example.musicmanager.database.dao.SongDao
import com.example.musicmanager.database.models.Song

/**
 * Repository class for managing database operations related to songs.
 * Provides methods to retrieve, create, update, and delete songs.
 *
 * @property songDao Data Access Object for song-related operations.
 */
class SongRepository(private val songDao: SongDao) {

    /**
     * Retrieves all songs from the database.
     *
     * @return A LiveData object containing a list of all songs.
     */
    fun getAllSongs(): LiveData<List<Song>> = songDao.getAllSongs()

    /**
     * Retrieves songs based on a query string.
     *
     * @param query The query string to search for songs.
     * @return A LiveData object containing a list of songs matching the query.
     */
    suspend fun getByQuery(query: String): LiveData<List<Song>> = songDao.getByQuery(query)

    /**
     * Inserts a new song into the database.
     *
     * @param song The song to be inserted.
     */
    suspend fun createSong(song: Song) = songDao.insert(song)

    /**
     * Deletes a song from the database.
     *
     * @param song The song to be deleted.
     */
    suspend fun deleteSong(song: Song) = songDao.delete(song)

    /**
     * Updates an existing song in the database.
     *
     * @param song The song to be updated.
     */
    suspend fun updateSong(song: Song) = songDao.update(song)
}