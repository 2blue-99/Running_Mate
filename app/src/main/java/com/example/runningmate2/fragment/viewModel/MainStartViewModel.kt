package com.example.runningmate2.fragment.viewModel

import android.app.Application
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.runningmate2.Calorie
import com.example.runningmate2.MyApplication
import com.example.runningmate2.fragment.MainMapsFragment
import com.example.runningmate2.repo.MyLocationRepo
import com.example.runningmate2.repo.MySensorRepo
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.example.runningmate2.utils.ListLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainStartViewModel(
    application: Application,
) : AndroidViewModel(application){

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
        Log.e(javaClass.simpleName, "myStep")
        MySensorRepo.senSor(MyApplication.getApplication())
        MySensorRepo.notify.observeForever {
            _step.value?.let {
                _step.value = it + 1
            }
        }
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
//    fun test(){
//        Log.e("TAG", " text호출!", )
//        workManager
//            .beginUniqueWork(
//                "download",
//                ExistingWorkPolicy.KEEP,
//                downloadRequest
//            )
//            .enqueue()
//    }
}