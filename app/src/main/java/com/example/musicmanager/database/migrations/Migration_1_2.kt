package com.example.musicmanager.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Tworzenie nowej tabeli
        database.execSQL(
            """
            CREATE TABLE step_count (
                id INTEGER PRIMARY KEY NOT NULL,
                totalSteps INTEGER NOT NULL
            )
            """
        )

        // Opcjonalnie: Wstawienie domyślnego rekordu
        database.execSQL(
            """
            INSERT INTO step_count (id, totalSteps)
            VALUES (1, 0)
            """
        )
    }
}