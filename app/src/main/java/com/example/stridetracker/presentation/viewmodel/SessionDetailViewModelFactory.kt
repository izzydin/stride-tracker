package com.example.stridetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stridetracker.data.local.SessionDao

class SessionDetailViewModelFactory(
    private val sessionDao: SessionDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionDetailViewModel(sessionDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
