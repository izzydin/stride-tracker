package com.example.stridetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stridetracker.data.local.SessionDao
import com.example.stridetracker.data.local.SessionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SessionHistoryViewModel(
    athleteId: Long,
    sessionDao: SessionDao
) : ViewModel() {

    val sessions: StateFlow<List<SessionEntity>> = sessionDao.getSessionsForAthlete(athleteId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
