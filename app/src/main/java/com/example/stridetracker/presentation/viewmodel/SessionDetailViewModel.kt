package com.example.stridetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stridetracker.data.local.SegmentEntity
import com.example.stridetracker.data.local.SessionDao
import com.example.stridetracker.data.local.SessionEntity
import com.example.stridetracker.domain.usecase.DeleteSessionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionDetailViewModel(
    private val sessionDao: SessionDao,
    private val deleteSessionUseCase: DeleteSessionUseCase
) : ViewModel() {

    private val _session = MutableStateFlow<SessionEntity?>(null)
    val session: StateFlow<SessionEntity?> = _session.asStateFlow()

    private val _segments = MutableStateFlow<List<SegmentEntity>>(emptyList())
    val segments: StateFlow<List<SegmentEntity>> = _segments.asStateFlow()

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()

    private var sessionJob: Job? = null
    private var segmentsJob: Job? = null

    fun getSession(sessionId: Long) {
        sessionJob?.cancel()
        sessionJob = viewModelScope.launch {
            sessionDao.getSessionById(sessionId).collect {
                _session.value = it
            }
        }
    }

    fun getSegments(sessionId: Long) {
        segmentsJob?.cancel()
        segmentsJob = viewModelScope.launch {
            sessionDao.getSegmentsForSession(sessionId).collect {
                _segments.value = it
            }
        }
    }

    fun deleteSession() {
        val currentSessionId = _session.value?.id ?: return
        viewModelScope.launch {
            deleteSessionUseCase(currentSessionId.toString())
            _isDeleted.value = true
        }
    }
}
