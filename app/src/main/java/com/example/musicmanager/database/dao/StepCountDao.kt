package com.example.musicmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.musicmanager.database.models.StepCount

/**
 * Data Access Object (DAO) for performing database operations on the `StepCount` entity.
 * This interface defines methods for inserting, updating, and querying step count data.
 */
@Dao
interface StepCountDao {

    /**
     * Inserts a step count record into the database.
     * This method is a suspend function and should be called within a coroutine.
     *
     * @param stepCount The `StepCount` entity to be inserted.
     */
    @Insert
    suspend fun insert(stepCount: StepCount)

    /**
     * Updates an existing step count record in the database.
     * This method is a suspend function and should be called within a coroutine.
     *
     * @param stepCount The `StepCount` entity to be updated.
     */
    @Update
    suspend fun update(stepCount: StepCount)

    /**
     * Retrieves the step count record with ID 1 from the database.
     *
     * @return A `LiveData` object containing the `StepCount` entity with ID 1, or `null` if not found.
     */
    @Query("SELECT * FROM step_count WHERE id = 1")
    fun getStepCount(): LiveData<StepCount?>
}