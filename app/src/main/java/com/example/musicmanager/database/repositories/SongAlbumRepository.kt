package com.example.musicmanager.database.repositories

import androidx.lifecycle.LiveData
import com.example.musicmanager.database.dao.SongAlbumCrossDao
import com.example.musicmanager.database.models.Song
import com.example.musicmanager.database.models.SongAlbumCross

/**
 * Repository class for managing database operations related to song-album relationships.
 * Provides methods to retrieve, insert, update, and delete song-album cross references.
 *
 * @property songAlbumCrossDao Data Access Object for song-album relationship operations.
 */
class SongAlbumRepository(private val songAlbumCrossDao: SongAlbumCrossDao) {

    /**
     * Retrieves all songs associated with a specific album ID.
     *
     * @param albumId The ID of the album whose songs are to be retrieved.
     * @return A LiveData object containing a list of songs associated with the album.
     */
    fun getSongsByAlbumId(albumId: Int): LiveData<List<Song>> = songAlbumCrossDao.getSongsOfAlbum(albumId)

    /**
     * Retrieves all song-album cross references from the database.
     *
     * @return A LiveData object containing a list of all song-album cross references.
     */
    fun getAllSongAlbums(): LiveData<List<SongAlbumCross>> = songAlbumCrossDao.getAllSongAlbums()

    /**
     * Inserts a new song-album cross reference into the database.
     *
     * @param songAlbumCross The song-album cross reference to be inserted.
     */
    suspend fun insertCross(songAlbumCross: SongAlbumCross) = songAlbumCrossDao.insert(songAlbumCross)

    /**
     * Deletes an existing song-album cross reference from the database.
     *
     * @param songAlbumCross The song-album cross reference to be deleted.
     */
    suspend fun deleteCross(songAlbumCross: SongAlbumCross) = songAlbumCrossDao.delete(songAlbumCross)

    /**
     * Updates an existing song-album cross reference in the database.
     *
     * @param songAlbumCross The song-album cross reference to be updated.
     */
    suspend fun updateCross(songAlbumCross: SongAlbumCross) = songAlbumCrossDao.update(songAlbumCross)
}