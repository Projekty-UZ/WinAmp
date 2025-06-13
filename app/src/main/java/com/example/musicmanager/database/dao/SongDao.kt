package com.example.musicmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicmanager.database.models.Song

/**
 * Data Access Object (DAO) for performing database operations on the `Song` entity.
 * This interface defines methods for inserting, updating, deleting, and querying songs.
 */
@Dao
interface SongDao {

    /**
     * Inserts a song into the database.
     * If a conflict occurs, the existing record will be replaced.
     *
     * @param song The `Song` entity to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: Song)

    /**
     * Deletes a song from the database.
     *
     * @param song The `Song` entity to be deleted.
     */
    @Delete
    suspend fun delete(song: Song)

    /**
     * Updates an existing song in the database.
     *
     * @param song The `Song` entity to be updated.
     */
    @Update
    suspend fun update(song: Song)

    /**
     * Retrieves all songs from the database.
     *
     * @return A `LiveData` object containing a list of all `Song` entities.
     */
    @Query("SELECT * FROM Songs")
    fun getAllSongs(): LiveData<List<Song>>

    /**
     * Retrieves songs from the database that match the specified query.
     * The query can match either the title or the artist of the song.
     *
     * @param query The search query to filter songs by title or artist.
     * @return A `LiveData` object containing a list of matching `Song` entities.
     */
    @Query("SELECT DISTINCT * FROM Songs WHERE title = :query or artist = :query")
    fun getByQuery(query: String): LiveData<List<Song>>
}