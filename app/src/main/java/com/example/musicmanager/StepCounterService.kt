package com.example.musicmanager

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.lifecycle.asFlow
import com.example.musicmanager.database.models.StepCount
import com.example.musicmanager.ui.viewModels.StepCountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private lateinit var  viewModel: StepCountViewModel
    private var initialStepDB = 0
    private var initialStepSensor = 0
    private var stepSensorReceived = false
    private var databaseReceived = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        viewModel = StepCountViewModel((application as MusicManagerApplication).stepCountRepository)
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.stepCount.asFlow().first(
            ).let {
                initialStepDB = it?.totalSteps ?: 0
                databaseReceived = true
            }

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Obsłuż Intent z nową wartością kroków
        intent?.getIntExtra("RESET_STEPS", -1)?.let { newSteps ->
            if (newSteps != -1) {
                initialStepDB = newSteps
                initialStepSensor = 0
                stepSensorReceived = false
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val totalSteps = it.values[0].toInt()
                if (!stepSensorReceived) {
                    initialStepSensor = totalSteps
                    stepSensorReceived = true
                }
                if(databaseReceived) {
                    saveStepsToDatabase(totalSteps)
                }
            }
        }
    }

    private fun saveStepsToDatabase(totalSteps: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val stepCount = StepCount(id = 1, totalSteps = totalSteps - initialStepSensor + initialStepDB)
            viewModel.update(stepCount)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}