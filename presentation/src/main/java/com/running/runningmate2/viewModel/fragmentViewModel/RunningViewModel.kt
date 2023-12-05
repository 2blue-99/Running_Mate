package com.running.runningmate2.viewModel.fragmentViewModel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.running.data.local.sharedPreference.SharedPreferenceHelperImpl
import com.running.runningmate2.base.BaseViewModel
import com.running.runningmate2.utils.Calorie
import com.running.runningmate2.utils.ListLiveData
import com.running.runningmate2.utils.MyApplication
import com.running.runningmate2.utils.TimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 2023-12-05
 * pureum
 */
@HiltViewModel
class RunningViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferenceHelperImpl,
): BaseViewModel(), SensorEventListener {
    private val _distance = MutableLiveData(0.0)
    val distance: LiveData<Double> get() = _distance

    private val _calorie = MutableLiveData(0.0)
    val calorie: LiveData<Double> get() = _calorie

    private val _time = MutableLiveData<String>()
    val time: LiveData<String> get() = _time

    private val _step = MutableLiveData(0)
    val step: LiveData<Int> get() = _step

    private var accel: Float = 0.0f
    private var accelCurrent: Float = 0.0f
    private var accelLast: Float = 0.0f
    private lateinit var sensorManager: SensorManager
    private var skip = 300L
    private var myShakeTime = 0L
    private var timeSteam: Job? = null

    fun calculateDistance(locationList: ArrayList<Location>) {
        if(locationList.size > 1){
            var result = locationList.first().distanceTo(locationList.last()).toDouble()
            if (result <= 2) result = 0.0
            _distance.value = _distance.value?.plus(result)
            if (result != 0.0) calculateCalorie()
        }
    }

    private fun calculateCalorie(){
        val weight = sharedPreferences.getWeight()
        _calorie.value = _calorie.value?.plus(Calorie(weight).myCalorie())
    }

    fun startTime() {
        timeSteam = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _time.value = TimeHelper.getCustomTime()
            }
        }
    }

    private fun senSor(application: Application) {
        sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accel = 10f
        accelCurrent = SensorManager.GRAVITY_EARTH
        accelLast = SensorManager.GRAVITY_EARTH
        sensorManager.registerListener(
            this, sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val x: Float = event?.values?.get(0) as Float
        val y: Float = event.values?.get(1) as Float
        val z: Float = event.values?.get(2) as Float

        accelLast = accelCurrent
        accelCurrent = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        accel = accel * 0.9f + accelCurrent - accelLast

        if (accel > 15) {
            val currentTime = System.currentTimeMillis()
            if (myShakeTime + skip > currentTime) return
            myShakeTime = currentTime
            _step.value = _step.value?.plus(1)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    fun stepInit() { sensorManager.unregisterListener(this) }
    fun startStep() { senSor(MyApplication.getApplication()) }
    fun resetTime(){
        timeSteam?.cancel()
        TimeHelper.resetTime()
    }
}