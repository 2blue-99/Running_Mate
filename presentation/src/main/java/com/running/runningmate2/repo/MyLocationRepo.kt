package com.running.runningmate2.repo

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

class MyLocationRepo {

    fun nowLocation(application : Application, callback: LocationCallback) {

        val locationClient : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)

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


        val request = com.google.android.gms.location.LocationRequest.create().apply {
            this.priority = Priority.PRIORITY_HIGH_ACCURACY
            this.interval = 1500L
            this.fastestInterval = 1500L
        }.also {locationClient.requestLocationUpdates(it, callback, Looper.myLooper())}
    }
}