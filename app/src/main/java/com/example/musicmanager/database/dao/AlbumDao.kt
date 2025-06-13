package com.example.musicmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicmanager.database.models.Album

/**
 * Data Access Object (DAO) for performing database operations on the `Album` entity.
 * This interface defines methods for inserting, updating, deleting, and querying albums.
 */
@Dao
interface AlbumDao {

    /**
     * Inserts one or more albums into the database.
     * If a conflict occurs, the existing record will be replaced.
     *
     * @param album The albums to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg album: Album)

    /**
     * Updates an existing album in the database.
     *
     * @param album The album to be updated.
     */
    @Update
    fun update(album: Album)

    /**
     * Deletes an album from the database.
     *
     * @param album The album to be deleted.
     */
    @Delete
    fun delete(album: Album)

    /**
     * Retrieves all albums from the database.
     *
     * @return A LiveData object containing a list of all albums.
     */
    @Query("SELECT * FROM Albums")
    fun getAllAlbums(): LiveData<List<Album>>

    /**
     * Retrieves albums from the database that match the specified name.
     *
     * @param name The name of the albums to retrieve.
     * @return A LiveData object containing a list of albums with the specified name.
     */
    @Query("SELECT * FROM Albums WHERE name = :name")
    fun getByName(name: String): LiveData<List<Album>>
}