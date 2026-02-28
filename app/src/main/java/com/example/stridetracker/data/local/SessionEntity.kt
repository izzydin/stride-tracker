package com.example.stridetracker.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = AthleteEntity::class,
            parentColumns = ["id"],
            childColumns = ["athleteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["athleteId"])]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val athleteId: Long,
    val date: Long,
    val totalStrides: Int,
    val elapsedTimeMillis: Long
)
