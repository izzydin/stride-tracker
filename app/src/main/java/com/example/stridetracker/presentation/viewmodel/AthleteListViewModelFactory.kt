package com.example.stridetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stridetracker.data.local.AthleteDao

class AthleteListViewModelFactory(
    private val athleteDao: AthleteDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AthleteListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AthleteListViewModel(athleteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
