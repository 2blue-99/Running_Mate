package com.running.runningmate2.ui.fragment

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.running.runningmate2.ui.activity.MainActivity
import com.running.runningmate2.utils.MyApplication
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseFragment
import com.running.runningmate2.bottomSheet.BottomSheet
import com.running.runningmate2.databinding.FragmentMapsBinding
import com.running.runningmate2.viewModel.fragmentViewModel.MapsViewModel
import com.running.runningmate2.utils.EventObserver
import com.running.runningmate2.viewModel.activityViewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.round
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

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
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var weatherData: DomainWeather? = null
    private var static = false

    override fun initData() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true && it[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
                mapsViewModel.repeatCallLocation()
            else {
                Toast.makeText(
                    requireContext(),
                    "위치 이용에 동의를 하셔야 이용 할 수 있습니다. :)",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        static = false
    }

    override fun initUI() {
        binding.loadingText.visibility = View.VISIBLE
        binding.setBtn.visibility = View.INVISIBLE

        binding.startButton.visibility = View.INVISIBLE
        binding.followBtn.visibility = View.INVISIBLE

        binding.fake.text = "\n\n\n\n"
    }

    override fun initListener() {
        binding.startButton.setOnClickListener {
            if (binding.startButton.text == "Start") {
                val bottomSheet = BottomSheet(mainViewModel.getWeight()) {
                    mainViewModel.setData(it)
                }
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                //stop버튼
            } else {
                mapsViewModel.removeLocation()
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    val nowTime =
                        "${LocalDate.now()} ${LocalTime.now().hour}:${LocalTime.now().minute}"
                    val dayOfWeek =
                        when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK).toString()) {
                            "1" -> "일"
                            "2" -> "월"
                            "3" -> "화"
                            "4" -> "수"
                            "5" -> "목"
                            "6" -> "금"
                            else -> "토"
                        }
                    val datas = RunningData(
                        0,
                        dayOfWeek,
                        nowTime,
                        binding.runingBox.runTimeText.text.toString(),
                        binding.runingBox.runDistanceText.text.toString(),
                        binding.runingBox.runCaloreText.text.toString(),
                        binding.runingBox.runStepText.text.toString()
                    )
                    mainViewModel.insertDB(datas)
                    (activity as MainActivity).changeFragment(2)
                    start = false
                    mapsViewModel.stepInit()
                }
            }
        }

        //현재 위치로 줌 해주는 버튼
        binding.setBtn.setOnClickListener {
            //시작 버튼 눌렀을 때
            if (start) {
                Toast.makeText(requireContext(), "현 위치로", Toast.LENGTH_SHORT).show()
                mapsViewModel.setNowBtn.observe(viewLifecycleOwner) { locations ->
                    val myLocation = LatLng(locations.latitude - 0.0006, locations.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17.5F))
                    mapsViewModel.setNowBtn.removeObservers(viewLifecycleOwner)
                }
                //시작 안 했을 때
            } else {
                Toast.makeText(requireContext(), "현 위치로", Toast.LENGTH_SHORT).show()
                mapsViewModel.setNowBtn.observe(viewLifecycleOwner) { locations ->
                    val myLocation = LatLng(locations.latitude, locations.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17F))
                    mapsViewModel.setNowBtn.removeObservers(viewLifecycleOwner)
                }
            }
        }
    }
    override fun initObserver() {
        mainViewModel.error.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })

        mainViewModel.success.observe(viewLifecycleOwner, EventObserver {
            runningStart()
            binding.startButton.text = "Stop"
            binding.fake.text = "\n"
        })

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        mainViewModel.getWeatherData.observe(viewLifecycleOwner) { weather ->
            weatherData = weather
        }

        mainViewModel.getWeatherData.observe(viewLifecycleOwner) { myData ->
            binding.weatherView.loadingIcon.visibility = View.INVISIBLE
            binding.startButton.visibility = View.VISIBLE
            binding.weatherView.weatherIcon.visibility = View.VISIBLE
            if (myData?.temperatures == null) {
                binding.weatherView.weatherTem.text = "loading.."
            } else
                binding.weatherView.weatherTem.text =
                    "${myData.temperatures.toDouble()?.let { round(it) }} ºc"

            if (myData?.humidity == null) {
                binding.weatherView.humidity.text = "loading.."
            } else
                binding.weatherView.humidity.text = "${myData.humidity} %"

            when (myData?.rainType?.toDouble()?.toInt()) {
                //0 없음, 1 장대비, 2367 눈, 5 비
                0 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(
                    MyApplication.getApplication(),
                    R.drawable.ic___weather_suncloude
                )

                1 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(
                    MyApplication.getApplication(),
                    R.drawable.ic___weather__strongrain
                )

                2 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(
                    MyApplication.getApplication(),
                    R.drawable.ic__weather_snow
                )

                3 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(
                    MyApplication.getApplication(),
                    R.drawable.ic__weather_snow
                )

                5 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(
                    MyApplication.getApplication(),
                    R.drawable.ic___weather_rain
                )

                6 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(
                    MyApplication.getApplication(),
                    R.drawable.ic__weather_snow
                )

                7 -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(
                    MyApplication.getApplication(),
                    R.drawable.ic__weather_snow
                )

                else -> binding.weatherView.weatherIcon.background = ContextCompat.getDrawable(
                    MyApplication.getApplication(),
                    R.drawable.ic___weather_suncloude
                )
            }
        }

        mapsViewModel.time.observe(viewLifecycleOwner) { time ->
            if (time != null) {
                binding.runingBox.runTimeText.text = time
            }
        }

        mapsViewModel.calorie.observe(viewLifecycleOwner) { calorie ->
            if (calorie.toString().length > 4)
                binding.runingBox.runCaloreText.text = "${String.format("%.2f", calorie)} Kcal"
            else
                binding.runingBox.runCaloreText.text = "$calorie Kcal"

        }

        mapsViewModel.distance.observe(viewLifecycleOwner) { distance ->
            if (distance != null) {
                binding.runingBox.runDistanceText.text = "${String.format("%.2f", distance)} M"
            }
        }

        mapsViewModel.step.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.runingBox.runStepText.text = "$it 걸음"
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (view != null) {
            mapsViewModel.location.observe(viewLifecycleOwner) { locations ->
                if (locations.size > 0 && weatherData == null) {
                    mainViewModel.getWeatherData(locations.first())
                    binding.weatherView.weatherTem
                }

                if (locations.isNotEmpty()) {
                    binding.loadingText.visibility = View.INVISIBLE

                    if (!start) {
                        binding.startButton.visibility = View.VISIBLE
                    }

                    binding.setBtn.visibility = View.VISIBLE

                    myNowLati = locations.last().latitude
                    myNowLong = locations.last().longitude

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
                                    .title("pureum")
                                    .alpha(0.9F)
                                    .icon(
                                        bitmapDescriptorFromVector(
                                            requireContext(),
                                            R.drawable.ic_twotone_mylocate
                                        )
                                    )
                            )
                        }
                    }
                }
            }
        }

        if (view != null) {
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
                                bitmapDescriptorFromVector(
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
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
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
                        bitmapDescriptorFromVector(
                            requireContext(),
                            R.drawable.ic_twotone_mylocate
                        )
                    )
            )
        }
        binding.textConstraint.visibility = View.VISIBLE
    }
}