package com.example.musicmanager.ui.viewModels

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.musicmanager.database.models.StepCount
import com.example.musicmanager.database.repositories.StepCountRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for managing step count data.
 * Provides methods for inserting and updating step count records in the database.
 *
 * @property stepCountRepository The repository for accessing step count data.
 */
class StepCountViewModel(private val stepCountRepository: StepCountRepository): ViewModel() {

    /**
     * LiveData containing the current step count record.
     */
    val stepCount: LiveData<StepCount?> = stepCountRepository.stepCount

    /**
     * Inserts a new step count record into the database.
     *
     * @param stepCount The step count record to insert.
     */
    fun insert(stepCount: StepCount) {
        viewModelScope.launch {
            stepCountRepository.insert(stepCount)
        }
    }

    /**
     * Updates an existing step count record in the database.
     *
     * @param stepCount The step count record to update.
     */
    fun update(stepCount: StepCount) {
        viewModelScope.launch {
            stepCountRepository.update(stepCount)
        }
    }
}

/**
 * Factory for creating instances of `StepCountViewModel`.
 *
 * @property repository The repository for accessing step count data.
 */
class StepCountViewModelFactory(
    private val repository: StepCountRepository
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of `StepCountViewModel`.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return An instance of `StepCountViewModel`.
     * @throws IllegalArgumentException If the model class is not `StepCountViewModel`.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepCountViewModel::class.java)) {
            return StepCountViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * CompositionLocal for providing a `StepCountViewModel` instance.
 * Throws an error if no `StepCountViewModel` is provided.
 */
val LocalStepCountViewModel = staticCompositionLocalOf<StepCountViewModel> {
    error("No StepCountViewModel provided")
}