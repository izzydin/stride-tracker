package com.example.stridetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stridetracker.domain.model.SessionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MeasurementViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SessionState())
    val uiState: StateFlow<SessionState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: Long = 0L

    fun onStartStop() {
        val isStarting = !_uiState.value.isRunning
        _uiState.update { it.copy(isRunning = isStarting) }

        if (isStarting) {
            // When session starts, store startTime (adjusting for already elapsed time to support resume)
            startTime = System.currentTimeMillis() - _uiState.value.elapsedTimeMillis
            timerJob = viewModelScope.launch {
                while (true) {
                    val currentElapsed = System.currentTimeMillis() - startTime
                    _uiState.update { it.copy(elapsedTimeMillis = currentElapsed) }
                    delay(100)
                }
            }
        } else {
            // When session stops, cancel coroutine
            timerJob?.cancel()
            timerJob = null
        }
    }

    fun onStrideClick() {
        if (_uiState.value.isRunning) {
            _uiState.update {
                it.copy(
                    totalStrides = it.totalStrides + 1,
                    currentSegmentStrides = it.currentSegmentStrides + 1
                )
            }
        }
    }

    fun onDistanceClick() {
        if (_uiState.value.isRunning) {
            _uiState.update {
                it.copy(
                    segments = it.segments + it.currentSegmentStrides,
                    currentSegmentStrides = 0
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
