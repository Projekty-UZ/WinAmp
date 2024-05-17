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

@Dao
interface SongAlbumCrossDao {
    @Query("SELECT * FROM SongAlbumCrossRef")
    fun getAllSongAlbums(): LiveData<List<SongAlbumCross>>
    @Query("SELECT  Songs.* FROM Songs INNER JOIN SongAlbumCrossRef ON Songs.id = SongAlbumCrossRef.songId WHERE SongAlbumCrossRef.albumId = :albumId")
    fun getSongsOfAlbum(albumId: Int): LiveData<List<Song>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(songAlbumCross: SongAlbumCross)
    @Delete
    fun delete(songAlbumCross: SongAlbumCross)
    @Update
    fun update(songAlbumCross: SongAlbumCross)
}