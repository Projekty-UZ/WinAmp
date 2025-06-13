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

/**
 * Service for tracking step count using the device's step counter sensor.
 * This service interacts with the sensor and updates the step count in the database.
 */
class StepCounterService : Service(), SensorEventListener {

    /**
     * Manages access to the device's sensors.
     */
    private lateinit var sensorManager: SensorManager

    /**
     * Represents the step counter sensor.
     */
    private var stepSensor: Sensor? = null

    /**
     * ViewModel for accessing and updating step count data in the database.
     */
    private lateinit var viewModel: StepCountViewModel

    /**
     * Initial step count retrieved from the database.
     */
    private var initialStepDB = 0

    /**
     * Initial step count retrieved from the sensor.
     */
    private var initialStepSensor = 0

    /**
     * Indicates whether the initial step count from the sensor has been received.
     */
    private var stepSensorReceived = false

    /**
     * Indicates whether the initial step count from the database has been received.
     */
    private var databaseReceived = false

    /**
     * This method is required for bound services but returns null as this service does not support binding.
     *
     * @param intent The intent used to bind the service.
     * @return Always returns null.
     */
    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Called when the service is created.
     * Initializes the sensor manager, registers the step counter sensor, and retrieves the initial step count from the database.
     */
    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        viewModel = StepCountViewModel((application as MusicManagerApplication).stepCountRepository)
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.stepCount.asFlow().first().let {
                initialStepDB = it?.totalSteps ?: 0
                databaseReceived = true
            }
        }
    }

    /**
     * Called when the service receives a start command.
     * Handles intents to reset the step count.
     *
     * @param intent The intent containing the reset step count value.
     * @param flags Additional flags for the start command.
     * @param startId The start ID for the command.
     * @return START_STICKY to indicate the service should remain running.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getIntExtra("RESET_STEPS", -1)?.let { newSteps ->
            if (newSteps != -1) {
                initialStepDB = newSteps
                initialStepSensor = 0
                stepSensorReceived = false
            }
        }
        return START_STICKY
    }

    /**
     * Called when the service is destroyed.
     * Unregisters the sensor listener to stop receiving updates.
     */
    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    /**
     * Called when the sensor detects a change in step count.
     * Updates the step count and saves it to the database.
     *
     * @param event The sensor event containing the updated step count.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val totalSteps = it.values[0].toInt()
                if (!stepSensorReceived) {
                    initialStepSensor = totalSteps
                    stepSensorReceived = true
                }
                if (databaseReceived) {
                    saveStepsToDatabase(totalSteps)
                }
            }
        }
    }

    /**
     * Saves the updated step count to the database.
     *
     * @param totalSteps The total step count reported by the sensor.
     */
    private fun saveStepsToDatabase(totalSteps: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val stepCount = StepCount(id = 1, totalSteps = totalSteps - initialStepSensor + initialStepDB)
            viewModel.update(stepCount)
        }
    }

    /**
     * Called when the accuracy of the sensor changes.
     * This implementation does not handle accuracy changes.
     *
     * @param sensor The sensor whose accuracy changed.
     * @param accuracy The new accuracy value.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}