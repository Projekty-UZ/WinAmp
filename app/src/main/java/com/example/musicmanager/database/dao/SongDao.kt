package com.example.musicmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicmanager.database.models.Song

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: Song)
    @Delete
    suspend fun delete(song: Song)
    @Update
    suspend fun update(song: Song)
    @Query("SELECT * FROM Songs")
    fun getAllSongs(): LiveData<List<Song>>
    @Query("SELECT DISTINCT * FROM Songs WHERE title = :query or artist = :query")
    fun getByQuery(query: String): LiveData<List<Song>>
}