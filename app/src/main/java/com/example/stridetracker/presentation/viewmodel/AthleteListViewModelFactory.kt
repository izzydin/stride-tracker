package com.example.stridetracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stridetracker.data.local.AthleteDao
import com.example.stridetracker.domain.usecase.DeleteAthleteUseCase

class AthleteListViewModelFactory(
    private val athleteDao: AthleteDao,
    private val deleteAthleteUseCase: DeleteAthleteUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AthleteListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AthleteListViewModel(athleteDao, deleteAthleteUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
