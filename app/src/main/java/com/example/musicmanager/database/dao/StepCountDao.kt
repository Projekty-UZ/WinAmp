package com.example.musicmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.musicmanager.database.models.StepCount

@Dao
interface StepCountDao {
    @Insert
    suspend fun insert(stepCount: StepCount)
    @Update
    suspend fun update(stepCount: StepCount)

    @Query("SELECT * FROM step_count WHERE id = 1")
    fun getStepCount(): LiveData<StepCount?>
}