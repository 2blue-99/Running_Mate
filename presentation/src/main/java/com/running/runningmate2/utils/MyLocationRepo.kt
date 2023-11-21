package com.running.runningmate2.utils

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

object MyLocationRepo {

    lateinit var locationClient: FusedLocationProviderClient
    lateinit var callback: LocationCallback

    fun nowLocation(application : Application, locationCallback: LocationCallback) {
        callback = locationCallback
        locationClient = LocationServices.getFusedLocationProviderClient(application)
        locationClient.removeLocationUpdates(locationCallback)

        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission || !isGpsEnabled) {
            return
        }


        val request = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 1500L
            fastestInterval = 1500L
        }.also {locationClient.requestLocationUpdates(it, locationCallback, Looper.myLooper())}
    }

    fun removeLocationClient(){
        locationClient.removeLocationUpdates(callback)
    }
}