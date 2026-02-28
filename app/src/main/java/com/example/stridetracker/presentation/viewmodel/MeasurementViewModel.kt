package com.example.stridetracker.presentation.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stridetracker.data.local.SegmentEntity
import com.example.stridetracker.data.local.SessionDao
import com.example.stridetracker.data.local.SessionEntity
import com.example.stridetracker.domain.model.SessionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MeasurementViewModel(
    private val athleteId: Long,
    private val sessionDao: SessionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionState())
    val uiState: StateFlow<SessionState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTimeNanos: Long = 0L

    fun onStartStop() {
        val isStarting = !_uiState.value.isRunning
        _uiState.update { it.copy(isRunning = isStarting) }

        if (isStarting) {
            // Use elapsedRealtimeNanos for monotonic, high-precision timing
            startTimeNanos = SystemClock.elapsedRealtimeNanos() - (_uiState.value.elapsedTimeMillis * 1_000_000L)
            timerJob = viewModelScope.launch {
                while (true) {
                    val currentElapsedMillis = (SystemClock.elapsedRealtimeNanos() - startTimeNanos) / 1_000_000L
                    _uiState.update { it.copy(elapsedTimeMillis = currentElapsedMillis) }
                    // 16ms delay targets roughly 60fps for smooth UI updates
                    delay(16)
                }
            }
        } else {
            timerJob?.cancel()
            timerJob = null
            saveSession()
        }
    }

    private fun saveSession() {
        val currentState = _uiState.value
        if (currentState.totalStrides > 0 || currentState.elapsedTimeMillis > 0) {
            viewModelScope.launch(Dispatchers.IO) {
                val session = SessionEntity(
                    athleteId = athleteId,
                    date = System.currentTimeMillis(),
                    totalStrides = currentState.totalStrides,
                    elapsedTimeMillis = currentState.elapsedTimeMillis
                )
                val sessionId = sessionDao.insertSession(session)

                val allSegments = if (currentState.currentSegmentStrides > 0) {
                    currentState.segments + currentState.currentSegmentStrides
                } else {
                    currentState.segments
                }

                val segmentEntities = allSegments.mapIndexed { index, strideCount ->
                    SegmentEntity(
                        sessionId = sessionId,
                        segmentIndex = index,
                        strideCount = strideCount
                    )
                }

                if (segmentEntities.isNotEmpty()) {
                    sessionDao.insertSegments(segmentEntities)
                }
            }
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
