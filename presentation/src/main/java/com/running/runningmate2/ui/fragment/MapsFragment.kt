package com.running.runningmate2.ui.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.running.runningmate2.utils.MyApplication
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseFragment
import com.running.runningmate2.bottomSheet.BottomSheet
import com.running.runningmate2.databinding.FragmentMapsBinding
import com.running.runningmate2.utils.BitmapHelper
import com.running.runningmate2.viewModel.fragmentViewModel.MapsViewModel
import com.running.runningmate2.utils.EventObserver
import com.running.runningmate2.utils.MapState
import com.running.runningmate2.utils.TimeHelper
import com.running.runningmate2.utils.WeatherIconHelper
import com.running.runningmate2.viewModel.activityViewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.round
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class MapsFragment : BaseFragment<FragmentMapsBinding>(R.layout.fragment_maps), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var mainMarker: Marker? = null
    private var nowPointMarker: Marker? = null
    private val mapsViewModel: MapsViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var start: Boolean = false
    private var myNowLati: Double? = null
    private var myNowLong: Double? = null
    private var weatherData: DomainWeather? = null
    private var static = false

    override fun initData() {
        static = false
    }

    override fun initUI() {
        binding.loadingText.visibility = View.VISIBLE
        binding.setBtn.visibility = View.INVISIBLE
        binding.btnStartStop.visibility = View.INVISIBLE
        binding.followBtn.visibility = View.INVISIBLE

        // TODO?
        binding.fake.text = "\n\n\n\n"

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun showStartBottomSheet(){
        val bottomSheet = BottomSheet(mapsViewModel.getWeight()) {
            mapsViewModel.setData(it)
        }
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }

    private fun stopRunning(){
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
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
            start = false
            mapsViewModel.stepInit()
        }
    }

    override fun initListener() {
        binding.btnStartStop.setOnClickListener {
//            when(mapsViewModel.getMapState()){
//                MapState.HOME -> showStartBottomSheet()
//                MapState.RUNNING -> stopRunning()
//            }
        }

        //현재 위치로 줌 해주는 버튼
        binding.setBtn.setOnClickListener {
            Toast.makeText(requireContext(), "현 위치로", Toast.LENGTH_SHORT).show()
            val nowLocation = mapsViewModel.location.value?.last()
            when(mapsViewModel.mapState.value){
                MapState.HOME -> {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(nowLocation.latitude, nowLocation.longitude),
                        17F)
                    )
                }
                MapState.RUNNING -> {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(nowLocation.latitude - 0.0006, nowLocation.longitude),
                        17.5F))
                }
                else -> {
                    Toast.makeText(requireContext(), "현 위치를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
        }
    }

    override fun initObserver() {
        mapsViewModel.mapState.observe(viewLifecycleOwner){
            when(it){
                MapState.LOADING -> {
                    binding.loadingText.visibility = View.VISIBLE
                    binding.setBtn.visibility = View.INVISIBLE
                    binding.btnStartStop.visibility = View.INVISIBLE
                    binding.followBtn.visibility = View.INVISIBLE
                }
                MapState.HOME -> {
                    binding.loadingText.visibility = View.VISIBLE
                    binding.setBtn.visibility = View.VISIBLE
                    binding.btnStartStop.visibility = View.VISIBLE
                    binding.followBtn.visibility = View.VISIBLE
                }
                MapState.RUNNING -> {
                    binding.loadingText.visibility = View.VISIBLE
                }
            }
        }

//        mainViewModel.error.observe(viewLifecycleOwner, EventObserver {
//            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
//        })

        mapsViewModel.success.observe(viewLifecycleOwner, EventObserver {
            runningStart()
            binding.btnStartStop.text = "Stop"
            binding.fake.text = "\n"
        })

//        mapsViewModel.weatherData.observe(viewLifecycleOwner) { weather ->
//            weatherData = weather
//        }

        mapsViewModel.weatherData.observe(viewLifecycleOwner) { myData ->

            binding.weatherView.loadingIcon.visibility = View.INVISIBLE
            binding.weatherView.weatherIcon.visibility = View.VISIBLE

            if(myData?.temperatures != null) binding.weatherView.weatherTem.text = "${myData.temperatures.toDouble().let { it.roundToInt() }} ºc"
            else binding.weatherView.weatherTem.text = "loading.."

            if (myData?.humidity != null)  binding.weatherView.humidity.text = "${myData.humidity} %"
            else binding.weatherView.humidity.text = "loading.."

            if(myData?.rainType != null) binding.weatherView.weatherIcon.background = WeatherIconHelper(myData.rainType.toDouble().toInt())
            else binding.weatherView.weatherIcon.background = WeatherIconHelper(-1)
        }

        mapsViewModel.time.observe(viewLifecycleOwner) { time ->
            if (time != null) binding.runingBox.runTimeText.text = time
        }

        mapsViewModel.calorie.observe(viewLifecycleOwner) { calorie ->
            if (calorie.toString().length > 4) binding.runingBox.runCaloreText.text = "${String.format("%.2f", calorie)} Kcal"
            else binding.runingBox.runCaloreText.text = "$calorie Kcal"
        }

        mapsViewModel.distance.observe(viewLifecycleOwner) { distance ->
            if (distance != null) binding.runingBox.runDistanceText.text = "${String.format("%.2f", distance)} M"
        }

        mapsViewModel.step.observe(viewLifecycleOwner) {
            if (it != null) binding.runingBox.runStepText.text = "$it 걸음"
        }
    }
//    override fun onResume() {
//        super.onResume()
//        binding.btnStartStop.visibility = View.INVISIBLE
//    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapsViewModel.location.observe(viewLifecycleOwner) { response ->
            when(val location = response.last()){
                is ResourceState.Success -> {
                    // 성공 처리
                    mapsViewModel.getWeatherData(location.data)
//                    binding.weatherView.weatherTem

                    binding.setBtn.visibility = View.VISIBLE

                    if()



//                    myNowLati = locations.last().latitude
//                    myNowLong = locations.last().longitude

                    LatLng(locations.last().latitude, locations.last().longitude).also {
                        if (start) {
                            mapsViewModel.setLatLng(it)
                        } else {
                            if (locations.size == 1) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 17F))
                            }
                            mMap.clear()
                            mainMarker = mMap.addMarker(
                                MarkerOptions()
                                    .position(LatLng(it.latitude, it.longitude))
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
                }
                else -> {
                    // 실패, 로딩 처리
                    binding.loadingText.visibility = View.INVISIBLE
                    binding.btnStartStop.visibility = View.VISIBLE
                }
            }
        }

        mapsViewModel.latLng.observe(viewLifecycleOwner) { latlngs ->
            if (latlngs.isNotEmpty()) {
                nowPointMarker?.remove()
                nowPointMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(latlngs.last().latitude, latlngs.last().longitude))
                        .title("pureum")
                        .alpha(0.9F)
                        //여기에 내위치 마커 만들기
                        .icon(
                            BitmapHelper(
                                requireContext(),
                                R.drawable.ic_twotone_mylocate
                            )
                        )
                )
                if (latlngs.size > 1) {
                    val beforeLocate = Location(LocationManager.NETWORK_PROVIDER)
                    val afterLocate = Location(LocationManager.NETWORK_PROVIDER)
                    beforeLocate.latitude = latlngs[latlngs.lastIndex - 1].latitude
                    beforeLocate.longitude = latlngs[latlngs.lastIndex - 1].longitude
                    afterLocate.latitude = latlngs[latlngs.lastIndex].latitude
                    afterLocate.longitude = latlngs[latlngs.lastIndex].longitude
                    val result = beforeLocate.distanceTo(afterLocate).toDouble()
                    if (result >= 0) {
                        mMap.addPolyline {
                            add(latlngs[latlngs.lastIndex - 1], latlngs[latlngs.lastIndex])
                            width(20F)
                            startCap(RoundCap())
                            endCap(RoundCap())
                            color(Color.parseColor("#FA785F"))
                        }
                    }
                }
            }
        }
    }
    private fun runningStart() {
        start = true
        mMap.clear()
        (activity as MainActivity).changeFragment(1)
        mapsViewModel.myTime()
        mapsViewModel.myStep()

        //이동고정 버튼
        binding.followBtn.visibility = View.VISIBLE
        binding.followBtn.setOnClickListener {
            if (!static) {
                Toast.makeText(requireContext(), "화면 고정 기능 ON", Toast.LENGTH_SHORT).show()
                mapsViewModel.fixDisplayBtn.observe(viewLifecycleOwner) { locations ->
                    val myLocation =
                        LatLng(locations.latitude - 0.0003, locations.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18F))
                }
                binding.followBtn.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.shape_click_btn)

                static = true
            } else {
                Toast.makeText(requireContext(), "화면 고정 기능 OFF", Toast.LENGTH_SHORT).show()
                mapsViewModel.fixDisplayBtn.observe(viewLifecycleOwner) { locations ->
                    val myLocation =
                        LatLng(locations.latitude - 0.0006, locations.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17.5F))
                }
                mapsViewModel.fixDisplayBtn.removeObservers(viewLifecycleOwner)
                binding.followBtn.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.shape_set_btn)
                static = false
            }
        }
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)

        //빠르게 줌하기 위해 사용
        if (myNowLong != null && myNowLong != null) {
            val startZoom = LatLng(myNowLati!! - 0.0006, myNowLong!!)
            val startLocate = LatLng(myNowLati!!, myNowLong!!)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startZoom, 17.5F))
            nowPointMarker = mMap.addMarker(
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

    override fun onStop() {
        super.onStop()
//        mapsViewModel.removeLocation()
    }
}