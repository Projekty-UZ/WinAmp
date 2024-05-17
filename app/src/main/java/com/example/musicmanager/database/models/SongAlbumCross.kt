package com.example.musicmanager.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    tableName = "SongAlbumCrossRef",
    primaryKeys = ["songId", "albumId"],
    foreignKeys =[
        ForeignKey(entity = Song::class, parentColumns = ["id"], childColumns = ["songId"], onDelete = CASCADE),
        ForeignKey(entity = Album::class, parentColumns = ["id"], childColumns = ["albumId"], onDelete = CASCADE)
    ]
    )
data class SongAlbumCross(
    val songId: Int,
    val albumId: Int
)
