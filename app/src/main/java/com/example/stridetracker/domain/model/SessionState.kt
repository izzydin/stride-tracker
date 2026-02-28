package com.example.stridetracker.domain.model

data class SessionState(
    val isRunning: Boolean = false,
    val totalStrides: Int = 0,
    val currentSegmentStrides: Int = 0,
    val segments: List<Int> = emptyList(),
    val elapsedTimeMillis: Long = 0L,
    val startTimeNanos: Long = 0L
)
