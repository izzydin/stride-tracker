package com.example.stridetracker.domain.usecase

import com.example.stridetracker.domain.repository.AthleteRepository

class DeleteAthleteUseCase(
    private val repository: AthleteRepository
) {
    suspend operator fun invoke(athleteId: String) {
        repository.deleteAthlete(athleteId)
    }
}
