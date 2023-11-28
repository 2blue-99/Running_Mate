package com.running.runningmate2.viewModel.fragmentViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.running.data.local.sharedPreference.SharedPreferenceHelperImpl
import com.running.domain.SavedData.DomainWeather
import com.running.domain.state.ResourceState
import com.running.domain.usecase.GetWeatherUseCase
import com.running.domain.usecase.LocationUseCase
import com.running.runningmate2.base.BaseViewModel
import com.running.runningmate2.utils.Calorie
import com.running.runningmate2.utils.Event
import com.running.runningmate2.utils.MyApplication
import com.running.runningmate2.utils.ListLiveData
import com.running.runningmate2.utils.MapState
import com.running.runningmate2.utils.WeatherRequestMaker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val sharedPreferences: SharedPreferenceHelperImpl,
    private val locationUseCase: LocationUseCase,
    private val sharedPreferenceHelperImpl: SharedPreferenceHelperImpl
) : BaseViewModel(), SensorEventListener {

    private val _distance = MutableLiveData<Double>()
    val distance: LiveData<Double> get() = _distance

    private val _calorie = MutableLiveData<Double>()
    val calorie: LiveData<Double> get() = _calorie

    private val _fixDisplayBtn = MutableLiveData<LatLng>()
    val fixDisplayBtn: LiveData<LatLng> get() = _fixDisplayBtn
    // Location 을 Polyline을 그리기 위해 LatLng 로 바꿔 관리.
    private val _latLng = ListLiveData<LatLng>()
    val latLng: LiveData<ArrayList<LatLng>> get() = _latLng

    private val _time = MutableLiveData<String>()
    val time: LiveData<String> get() = _time

    private val _step = MutableLiveData(0)
    val step: LiveData<Int> get() = _step

    private val _notify = MutableLiveData<Unit>()
    val notify: LiveData<Unit> get() = _notify

    private val _weatherData = MutableLiveData<DomainWeather?>()
    val weatherData: LiveData<DomainWeather?> get() = _weatherData
    fun getWeatherData() = weatherData.value

    private val _success = MutableLiveData<Event<Unit>>()
    val success: LiveData<Event<Unit>> get() = _success

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
    private var distanceHap: Double = 0.0
    private var accel: Float = 0.0f
    private var accelCurrent: Float = 0.0f
    private var accelLast: Float = 0.0f
    lateinit var sensorManager: SensorManager
    private var skip = 300L
    private var myShakeTime = 0L

    private val _mapState = MutableLiveData<MapState>()
    val mapState: LiveData<MapState> get() = _mapState
    fun changeState(state: MapState){ _mapState.value = state }

    private val _location = ListLiveData<Location>()
    val location: LiveData<ArrayList<Location>> get() = _location
    fun getLastLocation() = _location.value?.last()




    init {
        locationUseCase.getLocationDataStream().onEach {
            when (it) {
                is ResourceState.Success -> {
                    _location.add(it.data)
                    _mapState.value = MapState.HOME
                }
                else -> {
                    _mapState.value = MapState.LOADING
                }
            }
        }.launchIn(modelScope)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeatherData(location: Location) {
        modelScope.launch {
            try {
                val data = WeatherRequestMaker(location)
                val weatherRes = getWeatherUseCase(data)
                _weatherData.postValue(weatherRes)
            } catch (e: Exception) {
                val fakeData = DomainWeather(
                    temperatures = 0.0.toString(),
                    rn1 = 0.0.toString(),
                    eastWestWind = 0.0.toString(),
                    southNorthWind = 0.0.toString(),
                    humidity = 0.0.toString(),
                    rainType = 0.0.toString(),
                    windDirection = 0.0.toString(),
                    windSpeed = 0.0.toString()
                )
                _weatherData.postValue(fakeData)
            }
        }
    }

//    @SuppressLint("SimpleDateFormat")
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun createRequestParams(myLocation: Location?): HashMap<String, String> {
//        val now = LocalDateTime.now()
//        Log.e("TAG", "now : $now")
//        val baseTime = when {
//            now.hour > 11 -> {
//                if (now.minute < 40) "${now.hour - 1}00"
//                else "${now.hour}00"
//            }
//
//            now.hour == 10 -> {
//                if (now.minute < 40) "0900"
//                else "1000"
//            }
//
//            now.hour in 1..9 -> {
//                if (now.minute < 40) "0${now.hour - 1}00"
//                else "0${now.hour}00"
//            }
//
//            now.hour == 0 -> {
//                if (now.minute < 40) "2300"
//                else "0000"
//            }
//
//            else -> "0000"
//        }
//
//        val baseDate = if (now.hour != 0) {
//            LocalDate.now().toString().replace("-", "")
//        } else {
//            val myDate = SimpleDateFormat("yyyy/MM/dd")
//            val calendar = Calendar.getInstance()
//            val today = Date()
//            calendar.time = today
//            calendar.add(Calendar.DATE, -1)
//            myDate.format(calendar.time).replace("/", "")
//        }
//        val locate = myLocation?.let { TransLocationUtil.convertLocation(it) }
//
//        return HashMap<String, String>().apply {
//            put("serviceKey", com.running.runningmate2.BuildConfig.SERVICE_KEY)
//            put("pageNo", "1")
//            put("numOfRows", "10")
//            put("dataType", "JSON")
//            put("base_date", baseDate)
//            put("base_time", baseTime)
//            put("nx", locate?.nx?.toInt().toString() ?: "55")
//            put("ny", locate?.ny?.toInt().toString() ?: "127")
//        }
//    }

    fun setLatLng(value: LatLng) {
        _latLng.add(value)
        _latLng.clear()
        val now = LatLng(value.latitude, value.longitude)
        _fixDisplayBtn.value = now
        calculatorDistance(value)
    }

    fun myTime() {
        Log.e(javaClass.simpleName, "myTime")
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                _second++
                if (_second == 60) {
                    _second = 0
                    _minute++
                } else if (_minute == 60) {
                    _hour++
                    _minute = 0
                }

                if (_second.toString().length == 1)
                    second = "0$_second"
                else
                    second = "$_second"


                if (_minute.toString().length == 1)
                    minute = "0$_minute"
                else
                    minute = "$_minute"


                if (_hour.toString().length == 1)
                    hour = "0$_hour"
                else
                    hour = "$_hour"

                _time.value = "$hour:$minute:$second"
            }
        }
    }

    fun myStep() {
        senSor(MyApplication.getApplication())
        notify.observeForever {
            Log.e("TAG", "viewModel count: $it")
            _step.value?.let {
                Log.e("TAG", " _step.value : $it")
                _step.value = it + 1
            }
        }
    }

    fun stepInit() {
        killSensor()
    }

    private fun calculatorDistance(value: LatLng) {
        locationData.add(value)
        if (locationData.size > 1) {
            if (locationData.size == 2) {
                beforeLocate.latitude = locationData.first().latitude
                beforeLocate.longitude = locationData.first().longitude
            }
            afterLocate.latitude = locationData.last().latitude
            afterLocate.longitude = locationData.last().longitude

            var result = beforeLocate.distanceTo(afterLocate).toDouble()

            if (result <= 2) result = 0.0
            Log.e("TAG", " 거리 result : $result")

            distanceHap += result
            _distance.value = distanceHap

            beforeLocate.latitude = locationData.last().latitude
            beforeLocate.longitude = locationData.last().longitude

            if (result != 0.0) {
                val myCalorie = Calorie(sharedPreferences.getWeight()).myCalorie()
                calorieHap += myCalorie
                _calorie.value = calorieHap
            }
        }
    }

    @SuppressLint("ServiceCast")
    fun senSor(application: Application) {
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
        accelCurrent = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val delta: Float = accelCurrent - accelLast

        accel = accel * 0.9f + delta

        System.currentTimeMillis()
        if (accel > 15) {
            val currentTime = System.currentTimeMillis()
            if (myShakeTime + skip > currentTime) {
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

    private fun killSensor() {
        sensorManager.unregisterListener(this)
    }

//    fun removeLocation(){
//        onCleared()
//        locationUseCase.removeLocationDataStream()
//    }

    fun setData(weight: String) {
        try {
            sharedPreferenceHelperImpl.saveWeight(weight.toInt())
            _success.value = Event(Unit)
        } catch (t: Throwable) {
//            _error.value = Event("실수 형태로 입력하세요.")
        }
    }

    fun getWeight(): Int = sharedPreferenceHelperImpl.getWeight()
}
