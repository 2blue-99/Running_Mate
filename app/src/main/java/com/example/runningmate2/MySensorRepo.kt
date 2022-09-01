package com.example.runningmate2

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.google.android.gms.location.LocationCallback

object MySensorRepo : AppCompatActivity(), SensorEventListener {

    private var accel: Float = 0.0f
    private var accelCurrent: Float = 0.0f
    private var accelLast: Float = 0.0f
    private lateinit var sensorManager: SensorManager

    @SuppressLint("ServiceCast")
    fun senSor(application : Application){
        Log.e(javaClass.simpleName, "senSor")
//        val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        val sensor:Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        this.sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        accel = 10f
        accelCurrent = SensorManager.GRAVITY_EARTH
        accelLast = SensorManager.GRAVITY_EARTH

        sensorManager.registerListener(this, sensorManager
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onSensorChanged(event: SensorEvent?) {
//        Log.e(javaClass.simpleName, "onSensorChanged")
        val x:Float = event?.values?.get(0) as Float
        val y:Float = event?.values?.get(0) as Float
        val z:Float = event?.values?.get(0) as Float

        accelLast = accelCurrent
        accelCurrent = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val delta:Float = accelCurrent - accelLast

        accel = accel * 0.9f + delta

        if(accel>30){
//            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.Se)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.e(javaClass.simpleName, "onAccuracyChanged: ", )
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause(){
        sensorManager.unregisterListener(this)
        super.onPause()
    }
}