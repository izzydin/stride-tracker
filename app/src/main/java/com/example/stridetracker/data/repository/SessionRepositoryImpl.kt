package com.example.stridetracker.data.repository

import com.example.stridetracker.data.local.SessionDao
import com.example.stridetracker.domain.repository.SessionRepository

class SessionRepositoryImpl(
    private val sessionDao: SessionDao
) : SessionRepository {

    override suspend fun deleteSession(sessionId: String) {
        val id = sessionId.toLongOrNull()
        if (id != null) {
            sessionDao.deleteSessionById(id)
        }
    }
}
