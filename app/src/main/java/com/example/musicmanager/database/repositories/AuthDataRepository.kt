package com.example.musicmanager.database.repositories

import com.example.musicmanager.database.dao.AuthDataDao
import com.example.musicmanager.database.models.AuthData

/**
 * Repository class for managing database operations related to authentication data.
 * Provides methods to insert, update, and retrieve authentication data.
 *
 * @property authDataDao Data Access Object for authentication-related operations.
 */
class AuthDataRepository(private val authDataDao: AuthDataDao) {

    /**
     * Inserts new authentication data into the database.
     *
     * @param authData The authentication data to be inserted.
     */
    suspend fun insertAuthData(authData: AuthData) {
        authDataDao.insertAuthData(authData)
    }

    /**
     * Updates existing authentication data in the database.
     *
     * @param authData The authentication data to be updated.
     */
    suspend fun updateAuthData(authData: AuthData) {
        authDataDao.updateAuthData(authData)
    }

    /**
     * Retrieves the authentication data from the database.
     *
     * @return The authentication data stored in the database.
     */
    fun getAuthData(): AuthData {
        return authDataDao.getAuthData()
    }
}