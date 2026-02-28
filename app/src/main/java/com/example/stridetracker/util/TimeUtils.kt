package com.example.stridetracker.util

import java.util.Locale

fun formatRelativeTime(
    timeNanos: Long,
    sessionStartNanos: Long
): String {
    val relativeNanos = timeNanos - sessionStartNanos
    // Avoid negative values if any
    val safeRelativeNanos = if (relativeNanos < 0) 0L else relativeNanos
    val relativeMillis = safeRelativeNanos / 1_000_000
    
    val minutes = relativeMillis / 60000
    val seconds = (relativeMillis % 60000) / 1000
    val centiseconds = (relativeMillis % 1000) / 10

    return String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, centiseconds)
}
