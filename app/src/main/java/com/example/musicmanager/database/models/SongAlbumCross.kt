package com.example.musicmanager.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

/**
 * Represents a cross-reference entity between songs and albums in the database.
 * This class is annotated with Room annotations to define the table structure and relationships.
 *
 * @property songId The unique identifier of the song. References the `id` column in the `Songs` table.
 * @property albumId The unique identifier of the album. References the `id` column in the `Albums` table.
 */
@Entity(
    tableName = "SongAlbumCrossRef",
    primaryKeys = ["songId", "albumId"],
    foreignKeys = [
        ForeignKey(entity = Song::class, parentColumns = ["id"], childColumns = ["songId"], onDelete = CASCADE),
        ForeignKey(entity = Album::class, parentColumns = ["id"], childColumns = ["albumId"], onDelete = CASCADE)
    ]
)
data class SongAlbumCross(
    val songId: Int,
    val albumId: Int
)
