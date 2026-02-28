package com.example.stridetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActiveAthleteViewModel : ViewModel() {

    private val _selectedAthleteId = MutableStateFlow<Long?>(null)
    val selectedAthleteId: StateFlow<Long?> = _selectedAthleteId.asStateFlow()

    fun selectAthlete(athleteId: Long) {
        _selectedAthleteId.value = athleteId
    }
}
