package com.example.runningmate2.fragment.viewModel

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.runningmate2.LocationRepository
import com.example.runningmate2.MyApplication
import com.example.runningmate2.MyLocationRepo
import com.google.android.gms.maps.model.LatLng
import com.jaehyeon.locationpolylinetest.utils.ListLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainStartViewModel(
    application: Application,
) : AndroidViewModel(application){

    // 지속적으로 받아오는 위치 정보를 List로 관리.
    private val _location = ListLiveData<Location>()
    val location: LiveData<ArrayList<Location>> get() = _location

    // Location 을 Polyline을 그리기 위해 LatLng 로 바꿔 관리.
    private val _latLng = ListLiveData<LatLng>()
    val latLng: LiveData<ArrayList<LatLng>> get() = _latLng

    //위치 정보를 받는 interval
    private val interval = 1500L

    fun repeatCallLocation(){
        Log.e(javaClass.simpleName, "repeatCallLocation: start", )
        viewModelScope.launch {
            while(true) {
                MyLocationRepo.nowLocation(MyApplication.getApplication())?.let { location ->
                    withContext(Dispatchers.Main) {
                        // UI에 관한 코드 이므로 Main 에서 돌린다.
                        Log.e(javaClass.simpleName, "getLocation location : $location", )
                        _location.add(location)
                    }
                }
                delay(interval)
            }
            // 여기서 위치 반복 호출 함수를 가져오고 메인스타트 프래그먼트에서
            // 뷰모델 호출해서 사용
        }
    }

    fun setLatLng(value: LatLng) {
        Log.e(javaClass.simpleName, "setLatLng: $value", )
        _latLng.add(value)
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

