package com.example.musicmanager.database.repositories

import com.example.musicmanager.database.dao.AuthDataDao
import com.example.musicmanager.database.models.AuthData

class AuthDataRepository(private val authDataDao: AuthDataDao) {
    suspend fun insertAuthData(authData: AuthData) {
        authDataDao.insertAuthData(authData)
    }

    suspend fun updateAuthData(authData: AuthData) {
        authDataDao.updateAuthData(authData)
    }

    fun getAuthData(): AuthData {
        return authDataDao.getAuthData()
    }
}