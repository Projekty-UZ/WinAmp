package com.example.musicmanager.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.musicmanager.database.models.StepCount
import com.example.musicmanager.ui.viewModels.LocalStepCountViewModel
import com.example.musicmanager.R
import com.example.musicmanager.StepCounterService
import android.content.Intent
import androidx.compose.ui.platform.LocalContext

/**
 * Composable function for rendering the Step Counter screen.
 * Displays the total steps taken and the calculated distance based on the step count.
 * Includes a button to reset the step count and restart the step counter service.
 */
@Composable
fun StepCounterScreen() {
    // Retrieve the current instance of the LocalStepCountViewModel.
    val viewModel = LocalStepCountViewModel.current

    // Observe the total step count from the ViewModel as a state.
    val totalSteps = viewModel.stepCount.observeAsState()

    // State variable to track whether the reset process is ongoing.
    var processing by remember { mutableStateOf(false) }

    // Retrieve the current context for starting the service.
    val context = LocalContext.current

    // Main layout for the Step Counter screen.
    Column(
        modifier = Modifier.fillMaxSize(), // Modifier to make the Column fill the entire screen.
        horizontalAlignment = Alignment.CenterHorizontally, // Align content horizontally to the center.
        verticalArrangement = Arrangement.Center // Arrange content vertically in the center.
    ) {
        // Display the total steps and calculated distance.
        if (totalSteps.value != null) {
            Text(
                stringResource(
                    id = R.string.step_counter_desc,
                    totalSteps.value!!.totalSteps.toString(),
                    calculateDistance(totalSteps.value!!.totalSteps)
                )
            )
        } else {
            // Display default values if no step count is available.
            Text(stringResource(id = R.string.step_counter_desc, "0", "0"))
        }

        // Button for resetting the step count and restarting the service.
        Button(
            onClick = {
                processing = true // Indicate that the reset process has started.
                viewModel.update(StepCount(id = 1, totalSteps = 0)) // Reset the step count in the ViewModel.
                val intent = Intent(context, StepCounterService::class.java).apply {
                    putExtra("RESET_STEPS", 0) // Pass the reset steps value to the service.
                }
                context.startService(intent) // Start the step counter service.
                processing = false // Indicate that the reset process has completed.
            },
            enabled = !processing // Disable the button while processing.
        ) {
            Text(stringResource(id = R.string.step_counter_reset)) // Display the button text.
        }
    }
}

/**
 * Utility function for calculating the distance based on the number of steps.
 * Converts the step count into kilometers using a fixed step length of 0.762 meters.
 *
 * @param steps The number of steps taken.
 * @return A formatted string representing the distance in kilometers.
 */
@SuppressLint("DefaultLocale")
fun calculateDistance(steps: Int): String {
    return String.format("%.2f", (steps * 0.762 / 1000)) // Calculate and format the distance.
}