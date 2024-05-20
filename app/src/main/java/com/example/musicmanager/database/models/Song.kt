package com.example.musicmanager.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "Songs")
data class Song(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val title: String,
    val artist: String,
    val duration: Int,
    val pathToFile: String
)
