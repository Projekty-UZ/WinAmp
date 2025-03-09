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

@Composable
fun StepCounterScreen() {
    val viewModel = LocalStepCountViewModel.current
    val totalSteps = viewModel.stepCount.observeAsState()
    var processing by remember {mutableStateOf(false)}
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (totalSteps.value != null) {
            Text(stringResource(id = R.string.step_counter_desc,
                totalSteps.value!!.totalSteps.toString(),
                calculateDistance(totalSteps.value!!.totalSteps)
            ))
        } else {
            Text(stringResource(id = R.string.step_counter_desc, "0", "0"))
        }
        Button(
            onClick = {
                processing = true
                viewModel.update(StepCount(id = 1, totalSteps = 0))
                val intent = Intent(context , StepCounterService::class.java).apply {
                    putExtra("RESET_STEPS", 0)
                }
                context.startService(intent)
                processing = false
            },
            enabled = !processing
        ) {
            Text(stringResource(id = R.string.step_counter_reset))
        }
    }
}

@SuppressLint("DefaultLocale")
fun calculateDistance(steps: Int): String {
    return String.format("%.2f",(steps * 0.762 / 1000))
}