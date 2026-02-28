package com.example.stridetracker.domain.usecase

import com.example.stridetracker.domain.repository.SessionRepository

class DeleteSessionUseCase(
    private val repository: SessionRepository
) {
    suspend operator fun invoke(sessionId: String) {
        repository.deleteSession(sessionId)
    }
}
