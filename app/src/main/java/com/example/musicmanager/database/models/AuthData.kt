package com.example.musicmanager.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_data")
data class AuthData(
    @PrimaryKey val id:Int = 1,
    val password: String,
    val recoveryEmail : String
)
