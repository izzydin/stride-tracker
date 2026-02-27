package com.example.stridetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: Long,
    val totalStrides: Int,
    val elapsedTimeMillis: Long
)
