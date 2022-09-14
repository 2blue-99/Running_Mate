package com.example.runningmate2.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.domain.model.DomainWeather
import com.example.runningmate2.*
import com.example.runningmate2.databinding.FragmentMapsBinding
import com.example.runningmate2.fragment.viewModel.MainStartViewModel
import com.example.runningmate2.room.AppDataBase
import com.example.runningmate2.utils.EventObserver
import com.example.runningmate2.viewModel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.ktx.addPolyline
import java.lang.Math.round
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList

class MainMapsFragment : Fragment(), OnMapReadyCallback {

//    private var _binding: FragmentMapsBinding? = null
    private lateinit var mMap: GoogleMap
    private var mainMarker: Marker? = null
    private var nowPointMarker: Marker? = null
    private val mainStartViewModel: MainStartViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val binding : FragmentMapsBinding by lazy {
        FragmentMapsBinding.inflate(layoutInflater)
    }
    private var start: Boolean = false
    private var myNowLati : Double? = null
    private var myNowLong : Double? = null
    private var distanceHap : Double = 0.0
    private val beforeLocate = Location(LocationManager.NETWORK_PROVIDER)
    private val afterLocate = Location(LocationManager.NETWORK_PROVIDER)
    private val firstLocate = Location(LocationManager.NETWORK_PROVIDER)
    private var calorieHap = 0.0
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var weatherData: DomainWeather? = null
    private var static = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(javaClass.simpleName, "onCreateView")
//        _binding = FragmentMapsBinding.inflate(inflater, container, false)
//        val view = binding.root

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true && it[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
                mainStartViewModel.repeatCallLocation()

            else {
                Toast.makeText(requireContext(), "위치 이용에 동의를 하셔야 이용 할 수 있다구 :)", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        binding.loadingText.visibility = View.VISIBLE
        binding.setBtn.visibility = View.INVISIBLE

        //위치 집어넣기 시작.
//        mainStartViewModel.repeatCallLocation()

        // start 버튼
        binding.startButton.setOnClickListener {
            if(binding.startButton.text == "Start"){
                val bottomSheet = BottomSheet(mainViewModel.getWeight()) {
                    mainViewModel.setData(it)
                }
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            //stop버튼
            }else{
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {

                    val nowTime = "${LocalDate.now()} ${LocalTime.now().hour}:${LocalTime.now().minute}"
                    var dayOfWeek = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK).toString()) {
                            "1" -> "일"
                            "2" -> "월"
                            "3" -> "화"
                            "4" -> "수"
                            "5" -> "목"
                            "6" -> "금"
                            else -> "토"
                        }
                    val datas =  RunningData(
//                        {String.format("%.2f",distanceHap + result)}
                        dayOfWeek,
                        nowTime,
                        binding.runingBox.runTimeText.text.toString(),
                        binding.runingBox.runDistanceText.text.toString(),
                        binding.runingBox.runCaloreText.text.toString(),
                        binding.runingBox.runStepText.text.toString())
                    Log.e(javaClass.simpleName, "stop btn 토스 : $datas")
                    mainViewModel.insertDB(datas)
                    (activity as MainActivity).changeFragment(2)
                    start = false
                    mainViewModel.clearWeatherData()
                }
            }
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

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e(javaClass.simpleName, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.error.observe(viewLifecycleOwner, EventObserver {
            //에러 띄우기
//            Toast.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            })

        mainViewModel.success.observe(viewLifecycleOwner, EventObserver{
            start = true
            runningStart()
            binding.startButton.text = "Stop"
        })

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        mainViewModel.getWeatherData.observe(viewLifecycleOwner){ weather->
            weatherData = weather
        }

        mainStartViewModel.location.observe(viewLifecycleOwner) { locations ->
            if(locations.size > 0 && weatherData == null) {
                Log.e(javaClass.simpleName, "날씨 호출")
                mainViewModel.getWeatherData(locations.first())
                binding.weatherView.weatherTem
            }

            if (locations.isNotEmpty()) {
                binding.loadingText.visibility = View.INVISIBLE
                if(!start){
                    binding.startButton.visibility = View.VISIBLE
                }
                binding.setBtn.visibility = View.VISIBLE

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
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.jeahyon))
                        )
                    }

                }
            }
        }

        mainViewModel.getWeatherData.observe(viewLifecycleOwner){myData->
            binding.weatherView.loadingIcon.visibility = View.INVISIBLE
            binding.startButton.visibility = View.VISIBLE
            binding.weatherView.weatherIcon.visibility = View.VISIBLE
            binding.weatherView.weatherTem.text = "${myData?.temperatures?.toDouble()?.let { round(it) } ?: "loading.."} º"
            binding.weatherView.humidity.text = myData?.humidity ?: "loading.."

                when(myData?.rainType?.toDouble()?.toInt()){
                    //0 없음, 1 장대비, 2367 눈, 5 비
                    0 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(MyApplication.getApplication(),R.drawable.ic___weather_suncloude)
                    1 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(MyApplication.getApplication(),R.drawable.ic___weather__strongrain)
                    2 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(MyApplication.getApplication(),R.drawable.ic__weather_snow)
                    3 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(MyApplication.getApplication(),R.drawable.ic__weather_snow)
                    5 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(MyApplication.getApplication(),R.drawable.ic___weather_rain)
                    6 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(MyApplication.getApplication(),R.drawable.ic__weather_snow)
                    7 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(MyApplication.getApplication(),R.drawable.ic__weather_snow)
                    else -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(MyApplication.getApplication(),R.drawable.ic___weather_suncloude)
                }

            Log.e(javaClass.simpleName, "옵져버 날씨 데이터 : $myData")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(googleMap: GoogleMap) {
        static = false

        mainStartViewModel.location.observe(viewLifecycleOwner) { locations ->
            if(locations.size > 0 && weatherData == null) {
                Log.e(javaClass.simpleName, "날씨 호출")
                mainViewModel.getWeatherData(locations.first())
            }
        }
        binding.startButton.visibility = View.INVISIBLE
        binding.followBtn.visibility = View.INVISIBLE
        mMap = googleMap
        // 맨 처음 시작, onCreateView에서 위치를 넣은 후, 이곳에서 위치를 옵져버 함.

        // start이후 첫 지점 찍으려면 여기서 latlngs.first()라고 해서 마커 찍어야 할듯
        // 폴리라인 하는 곳
        mainStartViewModel.latLng.observe(viewLifecycleOwner) { latlngs ->
            if (latlngs.isNotEmpty()) {
                if (static){
                    var locate = LatLng(latlngs.last().latitude-0.0013,latlngs.last().longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locate, 17F))
                }
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
    fun runningStart() {
        mMap.clear()
        (activity as MainActivity).changeFragment(1)
        mainStartViewModel.myTime()
        mainStartViewModel.myStep()

        binding.followBtn.visibility = View.VISIBLE
        binding.followBtn.setOnClickListener{
            if(static){
                binding.followBtn.background = ContextCompat.getDrawable(requireContext(),R.drawable.shape_set_btn)
                static = false
            }else{
                binding.followBtn.background = ContextCompat.getDrawable(requireContext(),R.drawable.shape_click_btn)
                static = true
            }

        }

        //빠르게 줌하기 위해 만듦
        if(myNowLong != null && myNowLong != null) {
            val startZoom = LatLng(myNowLati!! - 0.0013, myNowLong!!)
            val startLocate = LatLng(myNowLati!!, myNowLong!!)
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
        binding.textConstraint.visibility = View.VISIBLE

        mainStartViewModel.time.observe(viewLifecycleOwner) { time ->
            binding.runingBox.runTimeText.text = time
        }

        // 거리 계산
        mainStartViewModel.latLng.observe(viewLifecycleOwner){latLngs ->
            if (latLngs.size > 1){

                firstLocate.latitude = latLngs.first().latitude
                firstLocate.longitude = latLngs.first().longitude

                if(latLngs.size == 2) {
                    beforeLocate.latitude = latLngs.first().latitude
                    beforeLocate.longitude = latLngs.first().longitude
                }
                afterLocate.latitude= latLngs.last().latitude
                afterLocate.longitude= latLngs.last().longitude

                var result = beforeLocate.distanceTo(afterLocate).toDouble()
                var startToEndResult = firstLocate.distanceTo(afterLocate).toDouble()
                Log.e("TAG", "result : $result", )
                Log.e("TAG", "startToEndResult : $startToEndResult", )
//                binding.testing.text = result.toString()
                // 제자리 있을때 보정.
                if(result <= 1.0) result = 0.0
                if(startToEndResult <= 1.5) result = 0.0

                binding.runingBox.runDistanceText.text = "${String.format("%.2f",distanceHap + result)} M"
                distanceHap += result

                beforeLocate.latitude = latLngs.last().latitude
                beforeLocate.longitude = latLngs.last().longitude

                // 거리가 올라갈때만 칼로리 계산해주기
                if (result != 0.0){
                    val myCalorie = Calorie().myCalorie()
                    calorieHap += myCalorie
                    binding.runingBox.runCaloreText.text = "${String.format("%.2f",calorieHap)} Kcal"
                }
            }

            mainStartViewModel.step.observe(viewLifecycleOwner) {
                binding.runingBox.runStepText.text = "$it 걸음"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e(javaClass.simpleName, " 내가돌아왔다 ")
    }
}