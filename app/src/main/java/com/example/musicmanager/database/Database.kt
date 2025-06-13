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

/**
 * Abstract class representing the application's Room database.
 * Defines the database schema, DAOs, and provides methods for database initialization and access.
 *
 * @Database Annotation:
 * - `entities`: Specifies the list of entities included in the database.
 * - `version`: The current version of the database schema.
 * - `exportSchema`: Indicates whether the schema should be exported for version control.
 */
@Database(entities = [Album::class, Song::class, SongAlbumCross::class, StepCount::class, AuthData::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to the Album DAO for performing album-related database operations.
     */
    abstract fun albumDao(): AlbumDao

    /**
     * Provides access to the Song DAO for performing song-related database operations.
     */
    abstract fun songDao(): SongDao

    /**
     * Provides access to the Song-Album Cross DAO for managing relationships between songs and albums.
     */
    abstract fun songAlbumCrossDao(): SongAlbumCrossDao

    /**
     * Provides access to the Step Count DAO for managing step count data.
     */
    abstract fun stepCountDao(): StepCountDao

    /**
     * Provides access to the Auth Data DAO for managing authentication-related data.
     */
    abstract fun authDataDao(): AuthDataDao

    companion object {
        /**
         * Singleton instance of the database to ensure a single point of access.
         * Marked as volatile to ensure thread-safe access.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Retrieves the singleton instance of the database, creating it if necessary.
         *
         * @param context The application context used for database creation.
         * @param scope The coroutine scope used for database initialization tasks.
         * @return The singleton instance of the `AppDatabase`.
         */
        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            // TODO: Remove the database deletion logic later.
            // context.deleteDatabase("music_manager_database")
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "music_manager_database"
                )
                    .addMigrations(MIGRATION_1_2) // Adds migration from version 1 to 2.
                    .addMigrations(MIGRATION_2_3) // Adds migration from version 2 to 3.
                    .addCallback(AppDatabaseCallback(scope)) // Adds a callback for database creation.
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        /**
         * Callback class for handling database creation events.
         *
         * @property scope The coroutine scope used for executing initialization tasks.
         */
        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Called when the database is created for the first time.
             * Populates initial data into the database.
             *
             * @param db The SQLite database instance.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialData(database.stepCountDao())
                    }
                }
            }
        }

        /**
         * Populates initial data into the Step Count table.
         *
         * @param stepCountDao The DAO used for inserting step count data.
         */
        suspend fun populateInitialData(stepCountDao: StepCountDao) {
            // Inserts the first entry into the Step Count table with default values.
            stepCountDao.insert(StepCount(id = 1, totalSteps = 0))
        }
    }
}