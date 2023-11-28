package com.running.runningmate2.ui.fragment

import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.ktx.addPolyline
import com.running.domain.model.RunningData
import com.running.runningmate2.ui.activity.MainActivity
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseFragment
import com.running.runningmate2.bottomSheet.StartBottomSheet
import com.running.runningmate2.databinding.FragmentMapsBinding
import com.running.runningmate2.utils.BitmapHelper
import com.running.runningmate2.viewModel.fragmentViewModel.MapsViewModel
import com.running.runningmate2.utils.EventObserver
import com.running.runningmate2.utils.MapState
import com.running.runningmate2.utils.TimeHelper
import com.running.runningmate2.utils.WeatherIconHelper
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
    private var isStatic = true
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
                MapState.HOME -> showStartBottomSheet()
                MapState.RUNNING -> {}
                else -> {}
            }
        }

        //현재 위치로 줌 해주는 버튼
        binding.setBtn.setOnClickListener {
            showShortToast("내 위치로 이동")
            viewModel.getLastLocation()?.let { location ->
                when(viewModel.mapState.value){
                    MapState.HOME -> moveCamera(LatLng(location.latitude, location.longitude), 17F)
                    MapState.RUNNING -> moveCamera(LatLng(location.latitude - 0.0006, location.longitude), 17.5F)
                    else -> { }
                }
            }
        }

        // 위치 고정 버튼
        binding.followBtn.setOnClickListener {
            if(!isStatic) {
                isStatic = !isStatic
                Toast.makeText(requireContext(), "화면 고정 기능 ON", Toast.LENGTH_SHORT).show()
                viewModel.fixDisplayBtn.observe(viewLifecycleOwner) { locations ->
                    moveCamera(LatLng(locations.latitude - 0.0003, locations.longitude), 18F)
                }
                binding.followBtn.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.shape_click_btn)
            }


            else {
                isStatic = !isStatic
                Toast.makeText(requireContext(), "화면 고정 기능 OFF", Toast.LENGTH_SHORT).show()
                viewModel.fixDisplayBtn.observe(viewLifecycleOwner) { locations ->
                    moveCamera(LatLng(locations.latitude - 0.0006, locations.longitude), 17.5F)
                }
                viewModel.fixDisplayBtn.removeObservers(viewLifecycleOwner)
                binding.followBtn.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.shape_set_btn)
            }
        }
    }

    override fun initObserver() {
        // UI 수정
        viewModel.mapState.observe(viewLifecycleOwner){
            when(it){
                MapState.LOADING -> {
                    binding.progressBar.root.visibility = View.VISIBLE
                    binding.setBtn.visibility = View.INVISIBLE
                    binding.btnStartStop.visibility = View.INVISIBLE
                    binding.followBtn.visibility = View.INVISIBLE
                }
                MapState.HOME -> {
                    if(initMap) {
                        binding.btnStartStop.text = "START"
                        binding.progressBar.root.visibility = View.INVISIBLE
                        binding.setBtn.visibility = View.VISIBLE
                        binding.btnStartStop.visibility = View.VISIBLE
                        binding.followBtn.visibility = View.VISIBLE
                    }
                }
                MapState.RUNNING -> {
                    binding.btnStartStop.text = "STOP"
                    binding.progressBar.root.visibility = View.INVISIBLE
                    binding.setBtn.visibility = View.VISIBLE
                    binding.btnStartStop.visibility = View.VISIBLE
                    binding.followBtn.visibility = View.VISIBLE
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

        viewModel.success.observe(viewLifecycleOwner, EventObserver {
            runningStart()
            binding.btnStartStop.text = "Stop"
        })

        viewModel.weatherData.observe(viewLifecycleOwner) { myData ->
            binding.weatherView.loadingIcon.visibility = View.INVISIBLE
            binding.weatherView.weatherIcon.visibility = View.VISIBLE

            if(myData?.temperatures != null) binding.weatherView.weatherTem.text = "${myData.temperatures.toDouble().let { it.roundToInt() }} ºc"
            else binding.weatherView.weatherTem.text = "loading.."

            if (myData?.humidity != null)  binding.weatherView.humidity.text = "${myData.humidity} %"
            else binding.weatherView.humidity.text = "loading.."

            if(myData?.rainType != null) binding.weatherView.weatherIcon.background = WeatherIconHelper(myData.rainType.toDouble().toInt())
            else binding.weatherView.weatherIcon.background = WeatherIconHelper(-1)
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
        //GPS 현 위치 옵져버
        viewModel.location.observe(viewLifecycleOwner) { location ->
            mMap?.let {
                LatLng(location.last().latitude, location.last().longitude).let { LatLng ->
                    when(viewModel.mapState.value){
                        MapState.HOME -> {
                            addMarker(LatLng)
                            if(isStatic){
                                isStatic = false
                                moveCamera(LatLng(location.last().latitude, location.last().longitude), 17F)
                            }
                        }
                        MapState.RUNNING -> {
                            viewModel.setLatLng(LatLng)
                            if(isStatic){
                                moveCamera(LatLng(location.last().latitude, location.last().longitude), 17F)
                            }
                            nowPointMarker?.remove()
                            nowPointMarker = addMarker(LatLng)
                            addPolyline(location.first(), location.last())
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun showStartBottomSheet(){
        val startBottomSheet = StartBottomSheet(viewModel.getWeight()) {
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

    private fun moveCamera(location: LatLng, zoom: Float){
        if(initMap)
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
        else {
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
            initMap = true
        }
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

    private fun runningStart() {
        mMap?.clear()
        (activity as MainActivity).changeFragment(1)
        viewModel.myTime()
        viewModel.myStep()

        //이동고정 버튼
        binding.followBtn.visibility = View.VISIBLE

        AnimationUtils.loadAnimation(requireContext(), R.anim.blink)

        //빠르게 줌하기 위해 사용
        if (myNowLong != null && myNowLong != null) {
            val startZoom = LatLng(myNowLati!! - 0.0006, myNowLong!!)
            val startLocate = LatLng(myNowLati!!, myNowLong!!)
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(startZoom, 17.5F))
            nowPointMarker = mMap?.addMarker(
                MarkerOptions()
                    .position(startLocate)
                    .title("현재 위치")
                    .alpha(0.9F)
                    .icon(
                        BitmapHelper(
                            requireContext(),
                            R.drawable.ic_twotone_mylocate
                        )
                    )
            )
        }
        binding.textConstraint.visibility = View.VISIBLE
    }
}