package com.example.runningmate2.fragment
import android.graphics.Color
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
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
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.addPolyline

class MainMapsFragment : Fragment() , OnMapReadyCallback{

    private var _binding: FragmentMapsBinding? = null
    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    private val mainStartViewModel: MainStartViewModel by viewModels()
    private val binding get() = _binding!!
    private var start : Boolean = false
//    private var nowLocation : Any = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val view = binding.root

        // start 버튼
        binding.button.setOnClickListener{
            runningStart()
        }

        // stop 버튼
        binding.stopButton.setOnClickListener{
            (activity as MainActivity).changeFragment(2)
            start = false
        }

        //현재 위치로 줌 해주는 버튼
        binding.setBtn.setOnClickListener{
            //시작 버튼 눌렀을 때
            if(start){
                mainStartViewModel.location.observe(viewLifecycleOwner){locations->
                    var myLocation = LatLng(locations.last().latitude - 0.0013, locations.last().longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17F))
                }
            //시작 안 했을 때
            }else{
                mainStartViewModel.location.observe(viewLifecycleOwner){locations->
                    val myLocation = LatLng(locations.last().latitude, locations.last().longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17F))
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        // location 값은 언제부터 넣어지나요
        mainStartViewModel.location.observe(viewLifecycleOwner) { locations ->
            if(locations.isNotEmpty()) {
                // 첫 위치만 화면 고정.
                if(locations.size == 1){
                    Log.e(javaClass.simpleName, " first locations : $locations", )
                    LatLng(locations.last().latitude, locations.last().longitude).also {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 17F))
//                        mMap.addMarker{
//                            position(it)
//                            icon(BitmapDescriptorFactory.fromResource(R.drawable.jeahyon))
//                            alpha(0.9F)
//                        }
                        //start가 됐을때 리스트에 위치 입력
                        if(start){
                            mainStartViewModel.setLatLng(it)
                        }

                    }
                }else{
//                    binding.log.text = locations.last().latitude.toString() + " / " + locations.last().longitude.toString()
                    LatLng(locations.last().latitude, locations.last().longitude).also {
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 17F))
                        mainStartViewModel.setLatLng(it)
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mainStartViewModel.repeatCallLocation()
        mainStartViewModel.nowLocation.observe(viewLifecycleOwner) { nowLocations ->

            Log.e(javaClass.simpleName, "location: $nowLocations", )

            if (nowLocations != null) {

                marker?.remove()

                // start버튼이 눌러졌을 경우
                if(start){
                    // 폴리라인에 리스트가 필요하기때문에 latLng를 옵져버
                    mainStartViewModel.latLng.observe(viewLifecycleOwner){latlngs ->
                        if (latlngs.size > 0) {
                            mMap.addPolyline {
                                addAll(latlngs)
                                color(Color.RED)
                            }
                        }
                    }

                    // 현재 위치를 마커 찍어야하기 때문에 단일값 nowLocation 옵져버
                    marker = mMap.addMarker (
                        MarkerOptions()
                            .position(nowLocations)
                            .title("pureum")
                            .alpha(0.9F)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.jeahyon))
//                        position(nowLocations)
//                        icon(BitmapDescriptorFactory.fromResource(R.drawable.jeahyon))
//                        alpha(0.9F)
                    )

                //start 버튼 안눌렸을 경우
                }else{
                    mMap.clear()
                    mMap.addMarker {
                        position(nowLocations)
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.jeahyon))
                        alpha(0.9F)
                    }
                }
            }

        }
    }

    private fun runningStart(){
        Log.e(javaClass.simpleName, " Running Start ", )

        (activity as MainActivity).changeFragment(1)

        mainStartViewModel.latLng.observe(viewLifecycleOwner){locations->
            if (locations.isNotEmpty()) {
                Log.e(javaClass.simpleName, " @@@@@@ now locations : $locations",)
                var myLocation =
                    LatLng(locations.last().latitude - 0.0013, locations.last().longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17F))
            }
        }

        binding.button.visibility = View.INVISIBLE
        binding.stopButton.visibility = View.VISIBLE
        binding.textConstraint.visibility = View.VISIBLE

        mainStartViewModel.myTime()
        mainStartViewModel.time.observe(viewLifecycleOwner){time ->
            binding.timeText.text = time
        }

        start = true
    }

}