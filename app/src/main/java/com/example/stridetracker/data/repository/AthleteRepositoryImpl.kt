package com.example.stridetracker.data.repository

import com.example.stridetracker.data.local.AthleteDao
import com.example.stridetracker.domain.repository.AthleteRepository

class AthleteRepositoryImpl(
    private val athleteDao: AthleteDao
) : AthleteRepository {

    override suspend fun deleteAthlete(athleteId: String) {
        val id = athleteId.toLongOrNull()
        if (id != null) {
            athleteDao.deleteAthleteById(id)
        }
    }
}
