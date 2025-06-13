package com.example.musicmanager.database.repositories

import androidx.lifecycle.LiveData
import com.example.musicmanager.database.dao.AlbumDao
import com.example.musicmanager.database.models.Album

/**
 * Repository class for managing database operations related to albums.
 * Provides methods to retrieve, create, update, and delete albums.
 *
 * @property albumDao Data Access Object for album-related operations.
 */
class AlbumRepository(private val albumDao: AlbumDao) {

    /**
     * Retrieves all albums from the database.
     *
     * @return A LiveData object containing a list of all albums.
     */
    fun getAllAlbums(): LiveData<List<Album>> = albumDao.getAllAlbums()

    /**
     * Retrieves albums by their name from the database.
     *
     * @param name The name of the albums to search for.
     * @return A LiveData object containing a list of albums matching the name.
     */
    fun getByName(name: String): LiveData<List<Album>> = albumDao.getByName(name)

    /**
     * Inserts a new album into the database.
     *
     * @param album The album to be inserted.
     */
    suspend fun createAlbum(album: Album) = albumDao.insert(album)

    /**
     * Deletes an album from the database.
     *
     * @param album The album to be deleted.
     */
    suspend fun deleteAlbum(album: Album) = albumDao.delete(album)

    /**
     * Updates an existing album in the database.
     *
     * @param album The album to be updated.
     */
    suspend fun updateAlbum(album: Album) = albumDao.update(album)
}