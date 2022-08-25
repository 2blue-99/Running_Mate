package com.example.runningmate2.fragment

import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.runningmate2.R
import com.example.runningmate2.databinding.FragmentMapsBinding
import com.example.runningmate2.fragment.viewModel.MainStartViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.addPolyline

class MapsFragment : Fragment() , OnMapReadyCallback{

    private var _binding: FragmentMapsBinding? = null
    private lateinit var mMap: GoogleMap
    private val mainStartViewModel: MainStartViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
//        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        mainStartViewModel.location.observe(viewLifecycleOwner){ locations ->
            Log.e(javaClass.simpleName, "location: $locations", )
            if (locations.isNotEmpty()){
                LatLng(locations.last().latitude, locations.last().longitude).also {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 17F))
                    mainStartViewModel.setLatLng(it)
                }
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mainStartViewModel.repeatCallLocation()
        mainStartViewModel.latLng.observe(viewLifecycleOwner) { latlngs ->
            Log.e(javaClass.simpleName, "latLng: $latlngs", )
            if (latlngs.size > 0) {
                binding.location.text = latlngs.last().toString()
                // polyline 에 대해 선언 할 코드.
                mMap.addPolyline {
                    addAll(latlngs)
                    color(Color.RED)
                }
                // marker 에 대해 선언 할 코드.
                mMap.addMarker {
                    position(latlngs.last())
                }
            }
        }
    }


}