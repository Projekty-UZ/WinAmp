package com.example.musicmanager.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_count")
data class StepCount(
    @PrimaryKey val id:Int = 1,
    val totalSteps: Int
)
