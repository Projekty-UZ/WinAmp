package com.example.musicmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musicmanager.database.dao.AlbumDao
import com.example.musicmanager.database.dao.SongAlbumCrossDao
import com.example.musicmanager.database.dao.SongDao
import com.example.musicmanager.database.models.Album
import com.example.musicmanager.database.models.Song
import com.example.musicmanager.database.models.SongAlbumCross

@Database(entities = [Album::class, Song::class, SongAlbumCross::class], version = 1, exportSchema = false)
abstract  class AppDatabase: RoomDatabase(){
    abstract fun albumDao(): AlbumDao
    abstract fun songDao(): SongDao
    abstract fun songAlbumCrossDao(): SongAlbumCrossDao
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_manager_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}