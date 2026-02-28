package com.example.stridetracker.presentation.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stridetracker.data.local.SegmentEntity
import com.example.stridetracker.data.local.SessionDao
import com.example.stridetracker.data.local.SessionEntity
import com.example.stridetracker.domain.model.Segment
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
        val now = SystemClock.elapsedRealtimeNanos()
        
        _uiState.update { it.copy(isRunning = isStarting) }

        if (isStarting) {
            if (_uiState.value.elapsedTimeMillis == 0L) {
                // First start: Initialize session origin and first segment origin
                startTimeNanos = now
                _uiState.update { it.copy(
                    startTimeNanos = now,
                    currentSegmentStartTimeNanos = now
                ) }
            } else {
                // Resume: Re-calculate the "virtual" start time to exclude pause duration
                startTimeNanos = now - (_uiState.value.elapsedTimeMillis * 1_000_000L)
            }
            
            timerJob = viewModelScope.launch {
                while (true) {
                    val currentElapsedMillis = (SystemClock.elapsedRealtimeNanos() - startTimeNanos) / 1_000_000L
                    _uiState.update { it.copy(elapsedTimeMillis = currentElapsedMillis) }
                    delay(50)
                }
            }
        } else {
            timerJob?.cancel()
            timerJob = null
            saveSession(now)
        }
    }

    private fun saveSession(stopTimeNanos: Long) {
        val currentState = _uiState.value
        if (currentState.totalStrides > 0 || currentState.elapsedTimeMillis > 0) {
            viewModelScope.launch(Dispatchers.IO) {
                val session = SessionEntity(
                    athleteId = athleteId,
                    date = System.currentTimeMillis(),
                    totalStrides = currentState.totalStrides,
                    elapsedTimeMillis = currentState.elapsedTimeMillis,
                    startTimeNanos = currentState.startTimeNanos
                )
                val sessionId = sessionDao.insertSession(session)

                // Finalize the current in-progress segment using the session stop time
                val segmentStart = if (currentState.segments.isEmpty()) {
                    currentState.startTimeNanos
                } else {
                    currentState.currentSegmentStartTimeNanos
                }
                
                val finalSegments = currentState.segments + Segment(
                    strideCount = currentState.currentSegmentStrides,
                    startTimeNanos = segmentStart,
                    endTimeNanos = stopTimeNanos
                )

                val segmentEntities = finalSegments.mapIndexed { index, segment ->
                    SegmentEntity(
                        sessionId = sessionId,
                        segmentIndex = index,
                        strideCount = segment.strideCount,
                        startTimeNanos = segment.startTimeNanos,
                        endTimeNanos = segment.endTimeNanos
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
            val now = SystemClock.elapsedRealtimeNanos()
            _uiState.update {
                val segmentStart = if (it.segments.isEmpty()) {
                    it.startTimeNanos
                } else {
                    it.currentSegmentStartTimeNanos
                }
                
                it.copy(
                    segments = it.segments + Segment(
                        strideCount = it.currentSegmentStrides,
                        startTimeNanos = segmentStart,
                        endTimeNanos = now
                    ),
                    currentSegmentStrides = 0,
                    currentSegmentStartTimeNanos = now
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
