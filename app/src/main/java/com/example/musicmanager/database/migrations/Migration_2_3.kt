package com.example.musicmanager.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Represents a migration from database version 2 to version 3.
 * This migration creates a new table `auth_data` to store authentication information.
 *
 * @property MIGRATION_2_3 The migration object defining the changes between versions.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    /**
     * Executes the migration logic to update the database schema.
     *
     * @param database The SQLite database instance to apply the migration on.
     */
    override fun migrate(database: SupportSQLiteDatabase) {
        // Creates the `auth_data` table with columns `id`, `password`, and `recoveryEmail`.
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