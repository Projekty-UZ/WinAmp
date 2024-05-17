package com.example.musicmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicmanager.database.models.Album

@Dao
interface AlbumDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg album: Album)
    @Update
    fun update(album: Album)
    @Delete
    fun delete(album: Album)
    @Query("SELECT * FROM Albums")
    fun getAllAlbums(): LiveData<List<Album>>
    @Query("SELECT * FROM Albums WHERE name = :name")
    fun getByName(name: String): LiveData<List<Album>>

}