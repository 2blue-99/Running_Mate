package com.example.runningmate2.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.runningmate2.Calorie
import com.example.runningmate2.MainActivity
import com.example.runningmate2.R
import com.example.runningmate2.databinding.FragmentMapsBinding
import com.example.runningmate2.fragment.viewModel.MainStartViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.addPolyline
import kotlin.math.round

class MainMapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private lateinit var mMap: GoogleMap
    private var mainMarker: Marker? = null
    private var startingPointMarker: Marker? = null
    private var nowPointMarker: Marker? = null
    private val mainStartViewModel: MainStartViewModel by viewModels()
    private val binding get() = _binding!!
    private var start: Boolean = false
    private var myNowLati : Double? = null
    private var myNowLong : Double? = null
    private var distanceHap : Double = 0.0
    private val beforeLocate = Location(LocationManager.NETWORK_PROVIDER)
    private val afterLocate = Location(LocationManager.NETWORK_PROVIDER)
    private var calorieHap = 0.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.e(javaClass.simpleName, "onCreateView")
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val view = binding.root

        //위치 집어넣기 시작.
        mainStartViewModel.repeatCallLocation()

        // start 버튼
        binding.button.setOnClickListener {
            start = true
            runningStart()
        }

        // stop 버튼
        binding.stopButton.setOnClickListener {
            (activity as MainActivity).changeFragment(2)
            start = false
        }

        //현재 위치로 줌 해주는 버튼
        binding.setBtn.setOnClickListener {
            //시작 버튼 눌렀을 때
            if (start) {
                mainStartViewModel.nowLocation.observe(viewLifecycleOwner) { locations ->
                    var myLocation =
                        LatLng(locations.latitude - 0.0013, locations.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17F))
                }
                //시작 안 했을 때
            } else {
                mainStartViewModel.location.observe(viewLifecycleOwner) { locations ->
                    val myLocation = LatLng(locations.last().latitude, locations.last().longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17F))
                }
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e(javaClass.simpleName, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.e(javaClass.simpleName, "onMapReady")
        mMap = googleMap

        // 맨 처음 시작, onCreateView에서 위치를 넣은 후, 이곳에서 위치를 옵져버 함.
        mainStartViewModel.location.observe(viewLifecycleOwner) { locations ->

            if (locations.isNotEmpty()) {
                myNowLati = locations.last().latitude
                myNowLong = locations.last().longitude
//                Log.e(javaClass.simpleName, "location.observe, locations : $locations ")
                LatLng(locations.last().latitude, locations.last().longitude).also {
                    // start됐을때 setLatLng함수에 값 넣고 이쪽 함수는 끝
                    if (start) {
                        Log.e(javaClass.simpleName, "observe setLatLng start")
                        mainStartViewModel.setLatLng(it)
                    }else{
                        // 첫번쨰 값만 카메라 이동
                        if(locations.size == 1){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 17F))
                        }
                        mMap.clear()
                        mainMarker = mMap.addMarker(
                            MarkerOptions()
                                .position(it)
                                .title("pureum")
                                .alpha(0.9F)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.jeahyon))
                        )
                    }

                }
            }
        }
        // start이후 첫 지점 찍으려면 여기서 latlngs.first()라고 해서 마커 찍어야 할듯
        // 폴리라인 하는 곳
        mainStartViewModel.latLng.observe(viewLifecycleOwner) { latlngs ->
//            latlngs.first()
            if (latlngs.isNotEmpty()) {
                // start시 화면 고정
//                if (latlngs.size == 1){
//                    var myLocation =
//                        LatLng(latlngs.last().latitude - 0.0013, latlngs.last().longitude)
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17F))
//                }
                mMap.addPolyline {
                    addAll(latlngs)
                    color(Color.RED)
                }
            }
        }
        // start버튼 눌러지면 실시간으로 이동하는 마커.
        mainStartViewModel.nowLocation.observe(viewLifecycleOwner) { nowLocations ->
            if (nowLocations != null) {
                Log.e(javaClass.simpleName, "nowLocation start: $nowLocations")
                nowPointMarker?.remove()
                nowPointMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(nowLocations)
                        .title("pureum")
                        .alpha(0.9F)
                    //여기에 내위치 마커 만들기
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.jeahyon))
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun runningStart() {
        mMap.clear()
        Log.e(javaClass.simpleName, " Running Start ")
        (activity as MainActivity).changeFragment(1)
        mainStartViewModel.myTime()
        mainStartViewModel.myStep()

        //빠르게 줌하기 위해 만듦
        if(myNowLong != null && myNowLong != null) {
            var startZoom = LatLng(myNowLati!! - 0.0013, myNowLong!!)
            var startLocate = LatLng(myNowLati!!, myNowLong!!)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startZoom, 17F))
            nowPointMarker = mMap.addMarker(
                MarkerOptions()
                    .position(startLocate)
                    .title("pureum")
                    .alpha(0.9F)
                //여기에 내위치 마커 만들기
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.jeahyon))
            )
        }
        binding.button.visibility = View.INVISIBLE
        binding.stopButton.visibility = View.VISIBLE
        binding.textConstraint.visibility = View.VISIBLE

        mainStartViewModel.time.observe(viewLifecycleOwner) { time ->
            binding.timeText.text = time
        }

        // 거리 계산
        mainStartViewModel.latLng.observe(viewLifecycleOwner){latLngs ->

            if (latLngs.size > 1){
                if(latLngs.size == 2) {
                    beforeLocate.latitude = latLngs.first().latitude
                    beforeLocate.longitude = latLngs.first().longitude
                }
                afterLocate.latitude= latLngs.last().latitude
                afterLocate.longitude= latLngs.last().longitude

//                Log.e(javaClass.simpleName, "거리 : ${beforeLocate.distanceTo(afterLocate)}")
                // 소숫점 1자리까지 반올림.
                var result = beforeLocate.distanceTo(afterLocate).toDouble()

                if(beforeLocate.distanceTo(afterLocate).toString().length > 3){
//                    Log.e(javaClass.simpleName, "길이 3보다 큰 거리 result: $result", )
                    result = round(result * 10 )/10
                }
                // 제자리 있을때 보정.
                if(result <= 2.0){ result = 0.0 }

//                Log.e(javaClass.simpleName, "보정 후 result: $result", )
                binding.distenceText.text = "${distanceHap + result} M"
                distanceHap += result
                beforeLocate.latitude = latLngs.last().latitude
                beforeLocate.longitude = latLngs.last().longitude
                // 칼로리 계산해주기
                if (result != 0.0){
                    val myCalorie = Calorie().myCalorie()
                    calorieHap += myCalorie
                    binding.caloriText.text = "${calorieHap} Kcal"
                }
            }
        }

//        //칼로리 계산
//        mainStartViewModel.calorie.observe(viewLifecycleOwner){calorie ->
//            if(calorie.toString().length > 2) {
//                binding.caloriText.text = "${calorie.toString().substring(0 until 3)} Kcal"
//            }
//        }


    }
}