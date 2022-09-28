package com.example.runningmate2.fragment.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.runningmate2.Calorie
import com.example.runningmate2.MyApplication
import com.example.runningmate2.fragment.MainMapsFragment
import com.example.runningmate2.repo.MyLocationRepo
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.example.runningmate2.utils.ListLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainStartViewModel(
    application: Application,
) : AndroidViewModel(application), SensorEventListener{

    // 지속적으로 받아오는 위치 정보를 List로 관리.
    private val _location = ListLiveData<Location>()
    val location: LiveData<ArrayList<Location>> get() = _location

    private val _distance = MutableLiveData<Double>()
    val distance: LiveData<Double> get() = _distance

    private val _calorie = MutableLiveData<Double>()
    val calorie: LiveData<Double> get() = _calorie

    private val _fixDisplayBtn = MutableLiveData<LatLng>()
    val fixDisplayBtn: LiveData<LatLng> get() = _fixDisplayBtn

    private val _setNowBtn = MutableLiveData<Location>()
    val setNowBtn: LiveData<Location> get() = _setNowBtn

    // Location 을 Polyline을 그리기 위해 LatLng 로 바꿔 관리.
    private val _latLng = ListLiveData<LatLng>()
    val latLng: LiveData<ArrayList<LatLng>> get() = _latLng

    private val _time = MutableLiveData<String>()
    val time: LiveData<String> get() = _time

    private val _step = MutableLiveData<Int>(0)
    val step: LiveData<Int> get() = _step

    private var _second = 0
    private var _minute = 0
    private var _hour = 0
    private var second = ""
    private var minute = ""
    private var hour = ""
    private var calorieHap = 0.0
    private val beforeLocate = Location(LocationManager.NETWORK_PROVIDER)
    private val afterLocate = Location(LocationManager.NETWORK_PROVIDER)
    private val locationData = ArrayList<LatLng>()
    private var distanceHap : Double = 0.0
//    private val workManager = WorkManager.getInstance(MyApplication.getApplication())
//    private val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>().build()
    var end = 0

    private var accel: Float = 0.0f
    private var accelCurrent: Float = 0.0f
    private var accelLast: Float = 0.0f
    lateinit var sensorManager: SensorManager
    private var skip = 300L
    private var myShakeTime = 0L
    private val _notify =  MutableLiveData<Unit>()
    val notify: LiveData<Unit> get() = _notify


    // 맨처음 위치 받아와서 넣기.
    fun repeatCallLocation() {
        object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }

            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                p0.lastLocation?.let { location ->
                    if(end != 1) {
                        Log.e("TAG", "end : $end")
                        Log.e(javaClass.simpleName, "location : $location")
                        _location.add(location)
                        _setNowBtn.value = location
                    }
                }
            }
        }.also { MyLocationRepo.nowLocation(MyApplication.getApplication(), it) }
    }


    fun setLatLng(value: LatLng) {
        _latLng.add(value)
        val now = LatLng(value.latitude, value.longitude)
        _fixDisplayBtn.value = now
        calculatorDistance(value)
    }

    fun myTime(){
        Log.e(javaClass.simpleName, "myTime")
        viewModelScope.launch {
            while(true){
                delay(1000L)
                _second++
                if(_second == 60){
                    _second = 0
                    _minute++
                }else if(_minute == 60){
                    _hour++
                    _minute = 0
                }

                if(_second.toString().length == 1){
                    second = "0$_second"
                }else{
                    second = "$_second"
                }

                if(_minute.toString().length == 1){
                    minute = "0$_minute"
                }else{
                    minute = "$_minute"
                }

                if(_hour.toString().length == 1){
                    hour = "0$_hour"
                }else{
                    hour = "$_hour"
                }
                _time.value = "$hour:$minute:$second"
            }
        }
    }

    fun myStep(){
        senSor(MyApplication.getApplication())
        notify.observeForever {
            Log.e("TAG", "viewModel count: $it", )
            _step.value?.let {
                Log.e("TAG", " _step.value : $it", )
                _step.value = it+1
            }
        }
    }

    fun stepInit(){
        killSensor()
    }

    fun calculatorDistance(value:LatLng){
        locationData.add(value)
        if(locationData.size > 1){
            if(locationData.size == 2) {
                beforeLocate.latitude = locationData.first().latitude
                beforeLocate.longitude = locationData.first().longitude
            }
            afterLocate.latitude= locationData.last().latitude
            afterLocate.longitude= locationData.last().longitude

            var result = beforeLocate.distanceTo(afterLocate).toDouble()

            if(result <= 2) result = 0.0
            Log.e("TAG", " 거리 result : $result", )

            distanceHap += result
            _distance.value = distanceHap

            beforeLocate.latitude = locationData.last().latitude
            beforeLocate.longitude = locationData.last().longitude

            if (result != 0.0){
                Log.e("TAG", " 칼로리 호출", )
                val myCalorie = Calorie().myCalorie()
                calorieHap += myCalorie
                _calorie.value = calorieHap
            }
        }
    }

    @SuppressLint("ServiceCast")
    fun senSor(application : Application){
        Log.e(javaClass.simpleName, "senSor")
        sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        accel = 10f
        accelCurrent = SensorManager.GRAVITY_EARTH
        accelLast = SensorManager.GRAVITY_EARTH

        sensorManager.registerListener(this, sensorManager
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME)

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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun killSensor(){
        sensorManager.unregisterListener(this)
    }
}
