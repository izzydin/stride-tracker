package com.example.stridetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stridetracker.data.local.AthleteDao
import com.example.stridetracker.data.local.AthleteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AthleteListViewModel(
    private val athleteDao: AthleteDao
) : ViewModel() {

    val athletes: StateFlow<List<AthleteEntity>> = athleteDao.getAllAthletes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addAthlete(name: String) {
        viewModelScope.launch {
            val athlete = AthleteEntity(
                name = name,
                createdAt = System.currentTimeMillis()
            )
            athleteDao.insertAthlete(athlete)
        }
    }
}
