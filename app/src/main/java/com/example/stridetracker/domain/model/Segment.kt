package com.example.stridetracker.domain.model

data class Segment(
    val strideCount: Int,
    val startTimeNanos: Long,
    val endTimeNanos: Long
)
