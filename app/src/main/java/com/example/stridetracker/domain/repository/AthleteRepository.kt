package com.example.stridetracker.domain.repository

interface AthleteRepository {
    suspend fun deleteAthlete(athleteId: String)
}
