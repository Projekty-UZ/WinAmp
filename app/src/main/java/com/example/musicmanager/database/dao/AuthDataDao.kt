package com.example.musicmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicmanager.database.models.AuthData

/**
 * Data Access Object (DAO) for performing database operations on the `AuthData` entity.
 * This interface defines methods for inserting, updating, and querying authentication data.
 */
@Dao
interface AuthDataDao {

    /**
     * Inserts authentication data into the database.
     * If a conflict occurs, the existing record will be replaced.
     *
     * @param authData The authentication data to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthData(authData: AuthData)

    /**
     * Updates existing authentication data in the database.
     *
     * @param authData The authentication data to be updated.
     */
    @Update
    suspend fun updateAuthData(authData: AuthData)

    /**
     * Retrieves the authentication data with a specific ID from the database.
     * Assumes the ID is always 1 for the authentication data.
     *
     * @return The authentication data with ID 1.
     */
    @Query("SELECT * FROM auth_data WHERE id = 1")
    fun getAuthData(): AuthData
}