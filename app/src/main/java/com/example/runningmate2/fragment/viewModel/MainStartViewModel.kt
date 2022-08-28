package com.example.runningmate2.fragment.viewModel

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.runningmate2.MyApplication
import com.example.runningmate2.MyLocationRepo
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
    val _location = ListLiveData<Location>()
    val location: LiveData<ArrayList<Location>> get() = _location

    // Location 을 Polyline을 그리기 위해 LatLng 로 바꿔 관리.
    private val _latLng = ListLiveData<LatLng>()
    val latLng: LiveData<ArrayList<LatLng>> get() = _latLng

    var _time = ListLiveData<String>()

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

    fun repeatCallLocation(){
        viewModelScope.launch {
            while(true) {
                MyLocationRepo.nowLocation(MyApplication.getApplication())?.let { location ->
                    withContext(Dispatchers.Main) {
                        if(beforeData == ""){
                            beforeData = location
                            _location.add(location)
                        }else if(beforeData != location){
                            beforeData = location
                            _location.add(location)
                            Log.e(javaClass.simpleName, "@@@@ location : $location", )
                        }
                    }
                    Log.e(javaClass.simpleName, "beforeData: $beforeData", )
                    Log.e(javaClass.simpleName, "location: $location", )
                }
                delay(interval)
            }
        }
    }

    fun setLatLng(value: LatLng) {
        Log.e(javaClass.simpleName, "setLatLng: $value", )
        _latLng.add(value)
//        myDistance(value)


    }

//    fun myDistance(locate : LatLng){
//        val R = 6372.8 * 1000
//        Log.e(javaClass.simpleName, "latLng.value: ${latLng.value}")
//
//        if(latLng.value?.size!! > 1){
//            latLng.value?.last()
//            val dLat = Math.toRadians(locate.latitude - latLng.value?.last()!!.latitude)
//            val dLon = Math.toRadians(locate.longitude - latLng.value?.last()!!.longitude)
//            val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(locate.latitude)) * cos(Math.toRadians(latLng.value?.last()!!.latitude))
//            val c = 2 * asin(sqrt(a))
//            Log.e(javaClass.simpleName, "myDistance result: ${(R * c)}")
//            return (R * c)
//        }
//
//    }

    fun myTime(){
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
                _time.add("$hour:$minute:$second")
//                myTime = "$hour:$minute:$second"
            }
        }

    }


}

//while(true) {
//    val nowLocation = MyLocation.nowLocation(MyApplication.getApplication())
//    Log.e(
//        javaClass.simpleName,
//        "viewModel nowLocation : ${nowLocation!!.latitude}, ${nowLocation.longitude}"
//    )
//    val myLocation = LatLng(nowLocation.latitude, nowLocation.longitude)
//    mMap = googleMap
//    mMap.addMarker(MarkerOptions().position(myLocation).title("Marker in Sydney"))
//    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,18F))

