package com.running.runningmate2.ui.fragment

import android.graphics.Color
import android.location.Location
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
import com.running.runningmate2.utils.TimeHelper
import com.running.runningmate2.utils.WeatherHelper
import com.running.runningmate2.viewModel.activityViewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class MapsFragment : BaseFragment<FragmentMapsBinding>(R.layout.fragment_maps), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var marker: Marker? = null
    private val viewModel: MapsViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var isStatic = false
    private var initMap = false

    override fun initData() {
        viewModel.changeState(MapState.RESUME)
    }
    override fun initUI() {
        viewModel.startLocationStream()
        val mapFragment = childFragmentManager.findFragmentById(R.id.maps_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }
    override fun initListener() {
        // 스타트, 스탑 버튼
        binding.mapsChangeBtn.setOnClickListener {
            viewModel.mapState.value?.let {
                when(it){
                    MapState.RESUME -> {
                        showStartBottomSheet()

                    }
                    MapState.RUNNING -> {
                        runningStop()
                    }
                }
            }
        }
        //현재 위치로 줌 해주는 버튼
        binding.mapsSetLocationBtn.setOnClickListener {
            showShortToast("내 위치로 이동")
            moveNowLocation()
        }
        // 위치 고정 버튼
        binding.mapsFixingBtn.setOnClickListener {
            //미고정 시
            if(!isStatic) {
                isStatic = !isStatic
                moveNowLocation()
                showShortToast("화면 고정 기능 ON")
                changeStaticBtn(R.drawable.shape_click_btn)
            }
            // 고정 시
            else {
                isStatic = !isStatic
                showShortToast("화면 고정 기능 OFF")
                changeStaticBtn(R.drawable.shape_set_btn)
            }
        }

        binding.mapsWeatherBox.root.setOnClickListener {
            if(!viewModel.isWeatherLoading())
                viewModel.getWeatherData()
        }
    }

    override fun initObserver() {
        viewModel.loading.observe(viewLifecycleOwner){ loading ->
            if(loading){
                binding.mapsLoading.root.visibility = View.VISIBLE
                binding.mapsSetLocationBtn.visibility = View.INVISIBLE
                binding.mapsChangeBtn.visibility = View.INVISIBLE
                binding.mapsFixingBtn.visibility = View.INVISIBLE
            }else{
                binding.mapsLoading.root.visibility = View.INVISIBLE
                binding.mapsSetLocationBtn.visibility = View.VISIBLE
                binding.mapsChangeBtn.visibility = View.VISIBLE
                binding.mapsFixingBtn.visibility = View.VISIBLE
            }
        }

        viewModel.mapState.observe(viewLifecycleOwner){
            when(it){
                MapState.RESUME -> {
                    binding.mapsChangeBtn.text = "START"
                }
                MapState.RUNNING -> {
                    binding.mapsChangeBtn.text = "STOP"
                    binding.mapsRunningBox.root.visibility = View.VISIBLE
                }
            }
        }
        //GPS 현 위치 옵져버 / 마커 찍기, 카메라 무빙
        viewModel.location.observe(viewLifecycleOwner) { location ->
            mMap?.let {
                viewModel.getNowLatLng()?.let { LatLng ->
                    when(viewModel.mapState.value){
                        MapState.RESUME -> {
                            if(!initMap) initMap(LatLng)
                            if(isStatic) moveCamera(LatLng, 17F)
                            marker = addMarker(LatLng)
                        }
                        MapState.RUNNING -> {
                            marker = addMarker(LatLng)
                            viewModel.calculateDistance()
                            if(isStatic) moveCamera(LatLng(LatLng.latitude-0.0006, LatLng.longitude), 17.5F)
                            addPolyline(location.first(), location.last())
                        }
                        else -> showShortToast("위치를 불러올 수 없습니다.")
                    }
                }
            }
        }
        // 날씨 정보 옵져버
        viewModel.weatherData.observe(viewLifecycleOwner) { weatherState ->
            when(weatherState){
                is ResourceState.Success ->{
                    binding.mapsWeatherBox.weatherLoading.visibility = View.INVISIBLE
                    binding.mapsWeatherBox.weatherLayout.visibility = View.VISIBLE
                    binding.mapsWeatherBox.weatherClickTxt.visibility = View.INVISIBLE
                    changeWeather(weatherState.data)
                }
                is ResourceState.Error -> {
                    showShortToast("날씨 로딩 실패..")
                    binding.mapsWeatherBox.weatherLoading.visibility = View.INVISIBLE
                    binding.mapsWeatherBox.weatherLayout.visibility = View.INVISIBLE
                    binding.mapsWeatherBox.weatherClickTxt.visibility = View.VISIBLE
                }
                is ResourceState.Loading -> {
                    binding.mapsWeatherBox.weatherLoading.visibility = View.VISIBLE
                    binding.mapsWeatherBox.weatherLayout.visibility = View.INVISIBLE
                    binding.mapsWeatherBox.weatherClickTxt.visibility = View.INVISIBLE
                }
            }
        }

        viewModel.time.observe(viewLifecycleOwner) { time ->
            if (time != null) binding.mapsRunningBox.runningBoxTimeTxt.text = time
        }

        viewModel.calorie.observe(viewLifecycleOwner) { calorie ->
            if (calorie.toString().length > 4) binding.mapsRunningBox.runningBoxCalorieTxt.text = "${String.format("%.2f", calorie)} Kcal"
            else binding.mapsRunningBox.runningBoxCalorieTxt.text = "$calorie Kcal"
        }

        viewModel.distance.observe(viewLifecycleOwner) { distance ->
            if (distance != null) binding.mapsRunningBox.runningBoxDistenceTxt.text = "${String.format("%.2f", distance)} M"
        }

        viewModel.step.observe(viewLifecycleOwner) {
            if (it != null) binding.mapsRunningBox.runningBoxStepTxt.text = "$it 걸음"
        }
    }
    override fun onStop() {
        super.onStop()
        viewModel.stopLocationStream()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun changeWeather(data: DomainWeather){
        binding.mapsWeatherBox.weatherTemTxt.text = "${data.temperatures.toDouble().let { it.roundToInt() }} ºc"
        binding.mapsWeatherBox.weatherHumidityTxt.text = "${data.humidity} %"
        binding.mapsWeatherBox.weatherIcon.background = WeatherHelper.makeIcon(data.rainType.toDouble().toInt())
    }

    private fun runningStop(){
        mainViewModel.insertDB(
            RunningData(
                0,
                TimeHelper.getDay(),
                TimeHelper.getTime(),
                binding.mapsRunningBox.runningBoxTimeTxt.text.toString(),
                binding.mapsRunningBox.runningBoxDistenceTxt.text.toString(),
                binding.mapsRunningBox.runningBoxCalorieTxt.text.toString(),
                binding.mapsRunningBox.runningBoxStepTxt.text.toString()
            )
        )
        viewModel.resetTime()
        viewModel.clearViewModel()
        viewModel.stepInit()
        (activity as MainActivity).changeFragment(2)
    }

    private fun changeStaticBtn(@DrawableRes icon: Int){
        binding.mapsFixingBtn.background = ContextCompat.getDrawable(requireContext(), icon)
    }

    private fun initMap(location: LatLng){
        viewModel.getWeatherData()
        moveCamera(location, 17F)
        initMap = true
    }

    private fun showStartBottomSheet(){
        val startBottomSheet = StartBottomSheet(viewModel.getWeight()) {
            viewModel.changeState(MapState.RUNNING)
            viewModel.saveWeight(it)
            (activity as MainActivity).changeFragment(1)
            moveNowLocation()
            viewModel.startTime()
            viewModel.startStep()
        }
        startBottomSheet.show(parentFragmentManager, startBottomSheet.tag)
    }

    private fun addPolyline(beforeLocation: Location, nowLocation: Location){
        mMap?.addPolyline {
            add(LatLng(beforeLocation.latitude, beforeLocation.longitude), LatLng(nowLocation.latitude, nowLocation.longitude))
            width(20F)
            startCap(RoundCap())
            endCap(RoundCap())
            color(Color.parseColor("#FA785F"))
        }
    }

    private fun moveNowLocation(){
        viewModel.getNowLocation()?.let { location ->
            when(viewModel.mapState.value){
                MapState.RESUME ->
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
        marker?.remove()
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
}