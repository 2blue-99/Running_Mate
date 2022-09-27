package com.example.runningmate2.repo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay

object MySensorRepo: SensorEventListener {

    private var accel: Float = 0.0f
    private var accelCurrent: Float = 0.0f
    private var accelLast: Float = 0.0f
    lateinit var sensorManager: SensorManager
    private var skip = 300L
    private var myShakeTime = 0L
    private val _notify =  MutableLiveData<Unit>()
    val notify: LiveData<Unit> get() = _notify

    @SuppressLint("ServiceCast")
    fun senSor(application : Application){
        Log.e(javaClass.simpleName, "senSor")
        sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        accel = 10f
        accelCurrent = SensorManager.GRAVITY_EARTH
        accelLast = SensorManager.GRAVITY_EARTH

        sensorManager.registerListener(this, sensorManager
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME)

    }

    override fun onSensorChanged(event: SensorEvent?) {
        val x:Float = event?.values?.get(0) as Float
        val y:Float = event?.values?.get(1) as Float
        val z:Float = event?.values?.get(2) as Float

        accelLast = accelCurrent
        accelCurrent = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val delta:Float = accelCurrent - accelLast

        accel = accel * 0.9f + delta

        System.currentTimeMillis()
        if(accel > 15){
            val currentTime = System.currentTimeMillis()
            if(myShakeTime + skip > currentTime){
                return
            }
            myShakeTime = currentTime
            notifySensorChange()

        }
    }

    private fun notifySensorChange() {
        _notify.value = Unit
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun kill(){
        if(sensorManager != null){

        }
        sensorManager.unregisterListener(this)
    }
}