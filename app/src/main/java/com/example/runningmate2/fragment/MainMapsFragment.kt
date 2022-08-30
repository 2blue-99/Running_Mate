package com.example.runningmate2.fragment

import android.graphics.Color
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
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
import java.lang.Exception

class MainMapsFragment : Fragment() , OnMapReadyCallback{

    private var _binding: FragmentMapsBinding? = null
    private lateinit var mMap: GoogleMap
//    private lateinit var marker: Marker
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
        binding.button.setOnClickListener{
            runningStart()
        }

        binding.stopButton.setOnClickListener{
            (activity as MainActivity).changeFragment(2)
            start = false
        }

        //현재 위치로 줌 해주는 버튼
        binding.setBtn.setOnClickListener{
            if(start){
                mainStartViewModel.latLng.observe(viewLifecycleOwner){locations->
                    var myLocation = LatLng(locations.last().latitude - 0.0013, locations.last().longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17F))
                }
            }else{
                mainStartViewModel.latLng.observe(viewLifecycleOwner){locations->
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.last(), 17F))
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

        mainStartViewModel.location.observe(viewLifecycleOwner) { locations ->

            if(locations.isNotEmpty()) {
                // 첫 위치만 화면 고정.
                if(locations.size == 1){
                    Log.e(javaClass.simpleName, " first locations : $locations", )
                    LatLng(locations.last().latitude, locations.last().longitude).also {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 17F))
                        mainStartViewModel.setLatLng(it)
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
        mainStartViewModel.mainLocation()
        mainStartViewModel.mainLocation.observe(viewLifecycleOwner) { mainLocation ->
            Log.e(javaClass.simpleName, "latLng: $mainLocation", )

            if (mainLocation) {
                // polyline 에 대해 선언 할 코드.
                if(start){
                    mMap.addPolyline {
                        addAll(mainLocation)
                        color(Color.RED)
                    }
                    mMap.addMarker {
                        position(mainLocation)
                    }
                }else{
                    mMap.clear()
                    mMap.addMarker {
                        position(mainLocation)
//                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.jeahyon))
                        alpha(0.9F)
                    }
                }
            }
        }
//        mainStartViewModel.latLng.observe(viewLifecycleOwner) { latlngs ->
//            Log.e(javaClass.simpleName, "latLng: $latlngs", )
//
//            if (latlngs.size > 0) {
//                // polyline 에 대해 선언 할 코드.
//                if(start){
//                    mMap.addPolyline {
//                        addAll(latlngs)
//                        color(Color.RED)
//                    }
//                    mMap.addMarker {
//                        position(latlngs.last())
//                    }
//                }else{
//                    mMap.clear()
//                    mMap.addMarker {
//                        position(latlngs.last())
////                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
//                        icon(BitmapDescriptorFactory.fromResource(R.drawable.jeahyon))
//                        alpha(0.9F)
//                    }
//                }
//            }
//
//        }
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