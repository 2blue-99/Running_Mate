package com.example.runningmate2.fragment.viewModel

import android.app.Application
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.runningmate2.MyApplication
import com.example.runningmate2.MyLocationRepo
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.jaehyeon.locationpolylinetest.utils.ListLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.*
import kotlin.math.pow

class MainStartViewModel(
    application: Application,
) : AndroidViewModel(application){

    // 지속적으로 받아오는 위치 정보를 List로 관리.
    private val _location = ListLiveData<Location>()
    val location: LiveData<ArrayList<Location>> get() = _location

    private val _nowLocation = MutableLiveData<LatLng>()
    val nowLocation: LiveData<LatLng> get() = _nowLocation

    // Location 을 Polyline을 그리기 위해 LatLng 로 바꿔 관리.
    private val _latLng = ListLiveData<LatLng>()
    val latLng: LiveData<ArrayList<LatLng>> get() = _latLng

    private val _time = MutableLiveData<String>()
    val time: LiveData<String> get() = _time

//    var _distance = 0.0
    var distance = ListLiveData<Double>()

    //위치 정보를 받는 interval
    private val interval = 1500L
    private var beforeData : Any = ""

    private var _second = 0
    private var _minute = 0
    private var _hour = 0
    private var second = ""
    private var minute = ""
    private var hour = ""

    // 맨처음 위치 받아와서 넣기.
    fun repeatCallLocation(){
        Log.e(javaClass.simpleName, "repeatCallLocation")
        object: LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }

            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                p0.lastLocation?.let { location ->
                    _location.add(location)
                }
            }
        }.also {
            MyLocationRepo.nowLocation(MyApplication.getApplication(), it)
        }
    }

    //
    fun setLatLng(value: LatLng) {
        Log.e(javaClass.simpleName, "setLatLng")
        _latLng.add(value)

        val now = LatLng(value.latitude, value.longitude)
        _nowLocation.value = now

        //시작시 계산하기 위해 가져감
//        calculationDistance(value)

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

//                Log.e(javaClass.simpleName, "viewModel time {$hour:$minute:$second}", )
                _time.value = "$hour:$minute:$second"
            }
        }
    }

//    fun calculationDistance(my : LatLng){
//
//
//
//        val myLoc = Location(LocationManager.NETWORK_PROVIDER)
//        val targetLoc = Location(LocationManager.NETWORK_PROVIDER)
//        myLoc.latitude= lat1
//        myLoc.longitude = lng1
//
//        targetLoc.latitude= lat2
//        targetLoc.longitude = lng2
//
//        myLoc.distanceTo(targetLoc)
//    }
}
