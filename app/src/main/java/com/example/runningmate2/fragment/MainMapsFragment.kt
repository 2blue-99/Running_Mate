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
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.addPolyline

class MainMapsFragment : Fragment() , OnMapReadyCallback{

    private var _binding: FragmentMapsBinding? = null
    private lateinit var mMap: GoogleMap
    private val mainStartViewModel: MainStartViewModel by viewModels()
    private val binding get() = _binding!!
    private var start : Boolean = false

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
            Log.e(javaClass.simpleName, " Running Stop ", )
            (activity as MainActivity).changeFragment(2)
            start = false
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        mainStartViewModel.location.observe(viewLifecycleOwner){ locations ->
            Log.e(javaClass.simpleName, "locations: $locations", )
            if (locations.isNotEmpty()){
                binding.log.text = "${locations.last().latitude} , ${locations.last().longitude}"
                // 처음 그냥 고정
                if(locations.size == 1) {
                    LatLng(locations.last().latitude, locations.last().longitude).also {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 17F))
                        mainStartViewModel.setLatLng(it)
                    }
                }
                // 위치가 바뀔때만 고정
                else if(locations.get(locations.lastIndex).longitude != locations.get(locations.lastIndex-1).longitude){
                    LatLng(locations.last().latitude, locations.last().longitude).also {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 17F))
                        mainStartViewModel.setLatLng(it)
                    }
                }
            }
        }


        mainStartViewModel._time.observe(viewLifecycleOwner){time ->
            if(time.isNotEmpty()){
                Log.e(javaClass.simpleName, "MainMaps onViewCreated: ${time.last()}")
                binding.timeText.text = time.last()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mainStartViewModel.repeatCallLocation()
        mainStartViewModel.latLng.observe(viewLifecycleOwner) { latlngs ->
            Log.e(javaClass.simpleName, "latLng: $latlngs", )
            if (latlngs.size > 0) {
                // polyline 에 대해 선언 할 코드.
                if(start){
                    mMap.addPolyline {
                        addAll(latlngs)
                        color(Color.RED)
                    }
                }
                // marker 에 대해 선언 할 코드.
                mMap.addMarker {
                    position(latlngs.last())
                }
            }
        }
    }

    private fun runningStart(){
        Log.e(javaClass.simpleName, " Running Start ", )
        (activity as MainActivity).changeFragment(1)
        binding.button.visibility = View.INVISIBLE
        binding.stopButton.visibility = View.VISIBLE
        binding.textConstraint.visibility = View.VISIBLE
        mainStartViewModel.myTime()
        start = true
    }

}