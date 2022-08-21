package com.example.runningmate2.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.runningmate2.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainStartPage : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var myLatitude:Double = 0.0
    var myLongitude:Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(javaClass.simpleName, "mainFrag: start", )
        super.onCreate(savedInstanceState)
//        requireActivity().setTitle(R.string.app_name)
        arguments?.let {
            myLatitude = it.getDouble("myLatitude")
            myLongitude = it.getDouble("myLongitude")
            Log.e(javaClass.simpleName, "myLatitude myLongitude: $myLatitude, $myLongitude", )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e(javaClass.simpleName, "mainFrag: onCreateView", )
        return inflater.inflate(R.layout.fragment_main_start_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e(javaClass.simpleName, "mainFrag: onViewCreated", )
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        Log.e(javaClass.simpleName, "mainFrag: onMapReady", )
        val myLocation = LatLng(myLatitude, myLongitude)
        mMap = googleMap

        mMap.addMarker(MarkerOptions().position(myLocation).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,18F))
    }

}