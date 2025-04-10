package com.example.musicmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musicmanager.database.dao.AlbumDao
import com.example.musicmanager.database.dao.AuthDataDao
import com.example.musicmanager.database.dao.SongAlbumCrossDao
import com.example.musicmanager.database.dao.SongDao
import com.example.musicmanager.database.dao.StepCountDao
import com.example.musicmanager.database.migrations.MIGRATION_1_2
import com.example.musicmanager.database.migrations.MIGRATION_2_3
import com.example.musicmanager.database.models.Album
import com.example.musicmanager.database.models.AuthData
import com.example.musicmanager.database.models.Song
import com.example.musicmanager.database.models.SongAlbumCross
import com.example.musicmanager.database.models.StepCount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Album::class, Song::class, SongAlbumCross::class, StepCount::class, AuthData::class], version = 3, exportSchema = false)
abstract  class AppDatabase: RoomDatabase(){
    abstract fun albumDao(): AlbumDao
    abstract fun songDao(): SongDao
    abstract fun songAlbumCrossDao(): SongAlbumCrossDao
    abstract fun stepCountDao(): StepCountDao
    abstract fun authDataDao(): AuthDataDao
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context,scope: CoroutineScope): AppDatabase{
            //TODO delete it later
            //context.deleteDatabase("music_manager_database")
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_manager_database"
                )
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                return instance
            }
        }
        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialData(database.stepCountDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(stepCountDao: StepCountDao) {
            // Insert the first entry for the StepCount table
            stepCountDao.insert(StepCount(id = 1, totalSteps = 0))
        }
    }


}