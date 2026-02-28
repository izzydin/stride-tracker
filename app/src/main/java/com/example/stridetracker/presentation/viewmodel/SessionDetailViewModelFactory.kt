package com.example.stridetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stridetracker.data.local.SessionDao
import com.example.stridetracker.domain.usecase.DeleteSessionUseCase

class SessionDetailViewModelFactory(
    private val sessionDao: SessionDao,
    private val deleteSessionUseCase: DeleteSessionUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionDetailViewModel(sessionDao, deleteSessionUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
