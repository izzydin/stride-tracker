package com.example.stridetracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AthleteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAthlete(athlete: AthleteEntity)

    @Query("SELECT * FROM athletes ORDER BY createdAt DESC")
    fun getAllAthletes(): Flow<List<AthleteEntity>>

    @Query("SELECT * FROM athletes WHERE id = :id")
    fun getAthleteById(id: Long): Flow<AthleteEntity?>
}
