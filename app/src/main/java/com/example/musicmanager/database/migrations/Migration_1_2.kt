package com.example.musicmanager.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Represents a migration from database version 1 to version 2.
 * This migration creates a new table `step_count` and optionally inserts a default record.
 *
 * @property MIGRATION_1_2 The migration object defining the changes between versions.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    /**
     * Executes the migration logic to update the database schema.
     *
     * @param database The SQLite database instance to apply the migration on.
     */
    override fun migrate(database: SupportSQLiteDatabase) {
        // Creates the `step_count` table with columns `id` and `totalSteps`.
        database.execSQL(
            """
            CREATE TABLE step_count (
                id INTEGER PRIMARY KEY NOT NULL,
                totalSteps INTEGER NOT NULL
            )
            """
        )

        // Optionally inserts a default record into the `step_count` table.
        database.execSQL(
            """
            INSERT INTO step_count (id, totalSteps)
            VALUES (1, 0)
            """
        )
    }
}