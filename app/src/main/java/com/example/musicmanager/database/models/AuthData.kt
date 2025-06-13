package com.example.musicmanager.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Represents authentication data stored in the database.
 * This class is annotated with Room annotations to define the table structure.
 *
 * @property id The unique identifier for the authentication data. Default value is 1.
 * @property password The password associated with the authentication data.
 * @property recoveryEmail The recovery email associated with the authentication data.
 */
@Entity(tableName = "auth_data")
data class AuthData(
    @PrimaryKey val id: Int = 1,
    val password: String,
    val recoveryEmail: String
)
