package com.example.musicmanager.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents step count data stored in the database.
 * This class is annotated with Room annotations to define the table structure.
 *
 * @property id The unique identifier for the step count record. Default value is 1.
 * @property totalSteps The total number of steps recorded.
 */
@Entity(tableName = "step_count")
data class StepCount(
    @PrimaryKey val id: Int = 1,
    val totalSteps: Int
)