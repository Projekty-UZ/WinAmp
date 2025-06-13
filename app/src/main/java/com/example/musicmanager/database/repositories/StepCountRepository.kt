package com.example.musicmanager.database.repositories

import androidx.lifecycle.LiveData
import com.example.musicmanager.database.dao.StepCountDao
import com.example.musicmanager.database.models.StepCount

/**
 * Repository class for managing database operations related to step count data.
 * Provides methods to retrieve, insert, and update step count records.
 *
 * @property stepCountDao Data Access Object for step count-related operations.
 */
class StepCountRepository(private val stepCountDao: StepCountDao) {

    /**
     * LiveData object representing the current step count record.
     * Automatically updates when the data in the database changes.
     */
    val stepCount: LiveData<StepCount?> = stepCountDao.getStepCount()

    /**
     * Inserts a new step count record into the database.
     *
     * @param stepCount The step count record to be inserted.
     */
    suspend fun insert(stepCount: StepCount) {
        stepCountDao.insert(stepCount)
    }

    /**
     * Updates an existing step count record in the database.
     *
     * @param stepCount The step count record to be updated.
     */
    suspend fun update(stepCount: StepCount) {
        stepCountDao.update(stepCount)
    }
}