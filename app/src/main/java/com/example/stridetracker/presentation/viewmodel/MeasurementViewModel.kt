package com.example.stridetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.stridetracker.domain.model.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MeasurementViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SessionState())
    val uiState: StateFlow<SessionState> = _uiState.asStateFlow()

    fun onStartStop() {
        _uiState.update { it.copy(isRunning = !it.isRunning) }
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
}
