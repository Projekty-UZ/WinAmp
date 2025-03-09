package com.example.musicmanager.ui.viewModels

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.musicmanager.database.models.StepCount
import com.example.musicmanager.database.repositories.StepCountRepository
import kotlinx.coroutines.launch

class StepCountViewModel(private val stepCountRepository: StepCountRepository): ViewModel() {
    val stepCount: LiveData<StepCount?> = stepCountRepository.stepCount

    fun insert(stepCount: StepCount) {
        viewModelScope.launch {
            stepCountRepository.insert(stepCount)
        }
    }

    fun update(stepCount: StepCount) {
        viewModelScope.launch {
            stepCountRepository.update(stepCount)
        }
    }
}

class StepCountViewModelFactory(
    private val repository: StepCountRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepCountViewModel::class.java)) {
            return StepCountViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

val LocalStepCountViewModel = staticCompositionLocalOf<StepCountViewModel> {
    error("No StepCountViewModel provided")
}