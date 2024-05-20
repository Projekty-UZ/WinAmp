package com.example.musicmanager.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Albums")
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val name : String,
)
