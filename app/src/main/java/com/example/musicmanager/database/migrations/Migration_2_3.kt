package com.example.musicmanager.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
       database.execSQL(
            """
            CREATE TABLE auth_data (
                id INTEGER PRIMARY KEY NOT NULL,
                password TEXT NOT NULL,
                recoveryEmail TEXT NOT NULL
            )
            """
        )
    }
}