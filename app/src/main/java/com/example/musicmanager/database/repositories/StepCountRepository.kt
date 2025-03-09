package com.example.musicmanager.database.repositories

import androidx.lifecycle.LiveData
import com.example.musicmanager.database.dao.StepCountDao
import com.example.musicmanager.database.models.StepCount

class StepCountRepository(private val stepCountDao: StepCountDao) {
    val stepCount: LiveData<StepCount?> = stepCountDao.getStepCount()

    suspend fun insert(stepCount: StepCount) {
        stepCountDao.insert(stepCount)
    }

    suspend fun update(stepCount: StepCount) {
        stepCountDao.update(stepCount)
    }
}