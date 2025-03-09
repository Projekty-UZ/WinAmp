package com.example.musicmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicmanager.database.models.AuthData

@Dao
interface AuthDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthData(authData: AuthData)

    @Update
    suspend fun updateAuthData(authData: AuthData)

    @Query("SELECT * FROM auth_data WHERE id = 1")
    fun getAuthData(): AuthData

}