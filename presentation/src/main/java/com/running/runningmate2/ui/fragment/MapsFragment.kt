package com.running.runningmate2.ui.fragment

import android.app.Activity
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.ktx.addPolyline
import com.running.domain.SavedData.DomainWeather
import com.running.domain.model.RunningData
import com.running.domain.state.ResourceState
import com.running.runningmate2.ui.activity.MainActivity
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseFragment
import com.running.runningmate2.bottomSheet.StartBottomSheet
import com.running.runningmate2.databinding.FragmentMapsBinding
import com.running.runningmate2.utils.BitmapHelper
import com.running.runningmate2.viewModel.fragmentViewModel.MapsViewModel
import com.running.runningmate2.utils.MapState
import com.running.runningmate2.utils.MyApplication
import com.running.runningmate2.utils.TimeHelper
import com.running.runningmate2.utils.WeatherHelper
import com.running.runningmate2.viewModel.activityViewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class MapsFragment : BaseFragment<FragmentMapsBinding>(R.layout.fragment_maps), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var nowPointMarker: Marker? = null
    private val viewModel: MapsViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var myNowLati: Double? = null
    private var myNowLong: Double? = null
    private var isStatic = false
    private var initMap = false

    override fun initData() {}
    override fun initUI() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }
    override fun initListener() {
        // 스타트, 스탑 버튼
        binding.btnStartStop.setOnClickListener {
            when(viewModel.mapState.value){
                MapState.HOME -> {
                    showStartBottomSheet()
                    //viewModel.changeState(MapState.RUNNING)
                }
                MapState.RUNNING -> {
                    //viewModel.changeState(MapState.END)
                }
                else -> {}
            }
        }

        //현재 위치로 줌 해주는 버튼
        binding.setBtn.setOnClickListener {
            showShortToast("내 위치로 이동")
            moveNowLocation()
        }

        // 위치 고정 버튼
        binding.followBtn.setOnClickListener {
            //미고정 시
            if(!isStatic) {
                isStatic = !isStatic
                moveNowLocation()
                Toast.makeText(requireContext(), "화면 고정 기능 ON", Toast.LENGTH_SHORT).show()
                changeStaticBtn(R.drawable.shape_click_btn)
            }
            // 고정 시
            else {
                isStatic = !isStatic
                Toast.makeText(requireContext(), "화면 고정 기능 OFF", Toast.LENGTH_SHORT).show()
                changeStaticBtn(R.drawable.shape_set_btn)
            }
        }

        binding.weatherView.root.setOnClickListener {
            if(!viewModel.loading)
                viewModel.getWeatherData()
        }
    }

    override fun initObserver() {
        // UI 수정
        viewModel.mapState.observe(viewLifecycleOwner){
            Log.e("TAG", "initObserver: mapState $it", )
            when(it){
                MapState.LOADING -> {
                    binding.progressBar.root.visibility = View.VISIBLE
                    binding.setBtn.visibility = View.INVISIBLE
                    binding.btnStartStop.visibility = View.INVISIBLE
                    binding.followBtn.visibility = View.INVISIBLE
                }
                MapState.HOME -> {
                    binding.btnStartStop.text = "START"
                    binding.progressBar.root.visibility = View.INVISIBLE
                    binding.setBtn.visibility = View.VISIBLE
                    binding.btnStartStop.visibility = View.VISIBLE
                    binding.followBtn.visibility = View.VISIBLE
                }
                MapState.RUNNING -> {
                    binding.btnStartStop.text = "STOP"
                    binding.progressBar.root.visibility = View.INVISIBLE
                    binding.setBtn.visibility = View.VISIBLE
                    binding.btnStartStop.visibility = View.VISIBLE
                    binding.followBtn.visibility = View.VISIBLE
                    binding.runingBox.root.visibility = View.VISIBLE
                }
                MapState.END -> {
                    val result = RunningData(
                        0,
                        TimeHelper().getDay(),
                        TimeHelper().getTime(),
                        binding.runingBox.runTimeText.text.toString(),
                        binding.runingBox.runDistanceText.text.toString(),
                        binding.runingBox.runCaloreText.text.toString(),
                        binding.runingBox.runStepText.text.toString()
                    )
                    mainViewModel.insertDB(result)
                    (activity as MainActivity).changeFragment(2)
                    viewModel.stepInit()
                }
            }
        }
        //GPS 현 위치 옵져버 / 마커 찍기, 카메라 무빙
        viewModel.location.observe(viewLifecycleOwner) { location ->
            mMap?.let {
                LatLng(location.last().latitude, location.last().longitude).let { LatLng ->
                    when(viewModel.mapState.value){
                        MapState.HOME -> {
                            addMarker(LatLng)
                            if(!initMap) initMap(LatLng)
                            if(isStatic) moveCamera(LatLng, 17F)

                        }
                        MapState.RUNNING -> {
                            viewModel.setLatLng(LatLng)
                            if(isStatic) moveCamera(LatLng, 17F)
                            nowPointMarker?.remove()
                            nowPointMarker = addMarker(LatLng)
                            addPolyline(location.first(), location.last())
                        }
                        MapState.LOADING -> {

                        }
                        else -> {
                            showShortToast("위치를 불러올 수 없습니다.")
                        }
                    }
                }
            }
        }
        // 날씨 정보 옵져버
        viewModel.weatherData.observe(viewLifecycleOwner) { weatherResourceState ->
            when(weatherResourceState){
                is ResourceState.Success ->{
                    binding.weatherView.loadingIcon.visibility = View.INVISIBLE

                    binding.weatherView.weatherLayout.visibility = View.VISIBLE
                    changeWeather(weatherResourceState.data)
                }
                is ResourceState.Error -> {
                    showShortToast("날씨 호출에 실패했습니다..")
                    binding.weatherView.loadingIcon.visibility = View.VISIBLE
                    binding.weatherView.weatherLayout.visibility = View.INVISIBLE
                }
                is ResourceState.Loading -> {
                    binding.weatherView.loadingIcon.visibility = View.VISIBLE
                    binding.weatherView.weatherLayout.visibility = View.INVISIBLE
                }
            }
        }

        viewModel.time.observe(viewLifecycleOwner) { time ->
            if (time != null) binding.runingBox.runTimeText.text = time
        }

        viewModel.calorie.observe(viewLifecycleOwner) { calorie ->
            if (calorie.toString().length > 4) binding.runingBox.runCaloreText.text = "${String.format("%.2f", calorie)} Kcal"
            else binding.runingBox.runCaloreText.text = "$calorie Kcal"
        }

        viewModel.distance.observe(viewLifecycleOwner) { distance ->
            if (distance != null) binding.runingBox.runDistanceText.text = "${String.format("%.2f", distance)} M"
        }

        viewModel.step.observe(viewLifecycleOwner) {
            if (it != null) binding.runingBox.runStepText.text = "$it 걸음"
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun changeWeather(data: DomainWeather){
        binding.weatherView.weatherTem.text = "${data.temperatures.toDouble().let { it.roundToInt() }} ºc"
        binding.weatherView.humidity.text = "${data.humidity} %"
        binding.weatherView.weatherIcon.background = WeatherHelper.makeIcon(data.rainType.toDouble().toInt())
    }

    private fun changeStaticBtn(@DrawableRes icon: Int){
        binding.followBtn.background = ContextCompat.getDrawable(requireContext(), icon)
    }

    private fun initMap(location: LatLng){
        viewModel.getWeatherData()
        moveCamera(location, 17F)
        initMap = true
    }

    private fun showStartBottomSheet(){
        val startBottomSheet = StartBottomSheet(viewModel.getWeight()) {
            //입력 바텀시트가 내려간 후,
            //바텀 시트 내려가기
            viewModel.saveWeight(it)
            //거리 계산
            viewModel.changeState(MapState.RUNNING)
            (activity as MainActivity).changeFragment(1)
//            viewModel.getNowLatLng()?.let { initMap(it) }
            moveNowLocation()
            //칼로리 계산
            //화면 확대, 화면 정중앙으로 오기
            //측정 컴포넌트 표시

            viewModel.startTime()
            viewModel.startStep()



            viewModel.setData(it)


        }
        startBottomSheet.show(parentFragmentManager, startBottomSheet.tag)
    }

    private fun addPolyline(beforeLocation: Location, nowLocation: Location){
        val beforeLocate = Location(LocationManager.NETWORK_PROVIDER)
        val afterLocate = Location(LocationManager.NETWORK_PROVIDER)
        beforeLocate.latitude = beforeLocation.latitude
        beforeLocate.longitude = beforeLocation.longitude
        afterLocate.latitude = nowLocation.latitude
        afterLocate.longitude = nowLocation.longitude

        mMap?.addPolyline {
            add(LatLng(beforeLocate.latitude, beforeLocate.longitude), LatLng(nowLocation.latitude, nowLocation.longitude))
            width(20F)
            startCap(RoundCap())
            endCap(RoundCap())
            color(Color.parseColor("#FA785F"))
        }
    }

    private fun moveNowLocation(){
        viewModel.getNowLocation()?.let { location ->
            when(viewModel.mapState.value){
                MapState.HOME ->
                    moveCamera(LatLng(location.latitude, location.longitude), 17F)
                MapState.RUNNING ->
                    moveCamera(LatLng(location.latitude - 0.0006, location.longitude), 17.5F)
                else -> {}
            }
        }
    }

    private fun moveCamera(location: LatLng, zoom: Float){
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
    }
    private fun addMarker(location: LatLng): Marker? {
        mMap?.clear()
        return mMap?.addMarker(
            MarkerOptions()
                .position(location)
                .title("location")
                .alpha(0.9F)
                .icon(
                    BitmapHelper(
                        requireContext(),
                        R.drawable.ic_twotone_mylocate
                    )
                )
        )
    }

//    private fun runningStart() {
//        mMap?.clear()
//        (activity as MainActivity).changeFragment(1)
//
//        //이동고정 버튼
//        binding.followBtn.visibility = View.VISIBLE
//
//        AnimationUtils.loadAnimation(requireContext(), R.anim.blink)
//
//        //빠르게 줌하기 위해 사용
//        if (myNowLong != null && myNowLong != null) {
//            val startZoom = LatLng(myNowLati!! - 0.0006, myNowLong!!)
//            val startLocate = LatLng(myNowLati!!, myNowLong!!)
//            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(startZoom, 17.5F))
//            nowPointMarker = mMap?.addMarker(
//                MarkerOptions()
//                    .position(startLocate)
//                    .title("현재 위치")
//                    .alpha(0.9F)
//                    .icon(
//                        BitmapHelper(
//                            requireContext(),
//                            R.drawable.ic_twotone_mylocate
//                        )
//                    )
//            )
//        }
//        binding.textConstraint.visibility = View.VISIBLE
//    }
}