package com.example.musicmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicmanager.database.models.Song
import com.example.musicmanager.database.models.SongAlbumCross

/**
 * Data Access Object (DAO) for performing database operations on the `SongAlbumCross` entity.
 * This interface defines methods for querying, inserting, updating, and deleting song-album relationships.
 */
@Dao
interface SongAlbumCrossDao {

    /**
     * Retrieves all song-album relationships from the database.
     *
     * @return A LiveData object containing a list of all `SongAlbumCross` entities.
     */
    @Query("SELECT * FROM SongAlbumCrossRef")
    fun getAllSongAlbums(): LiveData<List<SongAlbumCross>>

    /**
     * Retrieves all songs associated with a specific album from the database.
     *
     * @param albumId The ID of the album whose songs are to be retrieved.
     * @return A LiveData object containing a list of `Song` entities associated with the specified album.
     */
    @Query("SELECT  Songs.* FROM Songs INNER JOIN SongAlbumCrossRef ON Songs.id = SongAlbumCrossRef.songId WHERE SongAlbumCrossRef.albumId = :albumId")
    fun getSongsOfAlbum(albumId: Int): LiveData<List<Song>>

    /**
     * Inserts a song-album relationship into the database.
     * If a conflict occurs, the existing record will be replaced.
     *
     * @param songAlbumCross The `SongAlbumCross` entity to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(songAlbumCross: SongAlbumCross)

    /**
     * Deletes a song-album relationship from the database.
     *
     * @param songAlbumCross The `SongAlbumCross` entity to be deleted.
     */
    @Delete
    fun delete(songAlbumCross: SongAlbumCross)

    /**
     * Updates an existing song-album relationship in the database.
     *
     * @param songAlbumCross The `SongAlbumCross` entity to be updated.
     */
    @Update
    fun update(songAlbumCross: SongAlbumCross)
}