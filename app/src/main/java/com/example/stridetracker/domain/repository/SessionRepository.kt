package com.example.stridetracker.domain.repository

interface SessionRepository {
    suspend fun deleteSession(sessionId: String)
}
