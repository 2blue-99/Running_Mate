package com.running.data.local.location

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 2023-11-21
 * pureum
 */
class LocationDataHelperImpl @Inject constructor(
    @ApplicationContext private val application: Application
): LocationDataHelper {

    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var callback: LocationCallback
    private var location: Location = Location(null)


    override fun getLocationDataStream(): Flow<Location> {
        startLocationDataStream()
        return flow {
            Log.e("TAG", "getLocationDataStream: ",)
            emit(location)
            delay(1500L)
        }
    }

    private fun startLocationDataStream(){
        Log.e("TAG", "startLocationData: ", )
        object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                p0.lastLocation?.let {
                    location = it
                    Log.e("TAG", "onLocationResult: $it", )
                }
            }
        }.also { settingLocationStream(it) }
    }


    private fun settingLocationStream(locationCallback: LocationCallback) {
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

        LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 1500L
            fastestInterval = 1500L
        }.also {locationClient.requestLocationUpdates(it, locationCallback, Looper.myLooper())}
    }

    override fun removeLocationDataStream() {
        locationClient.removeLocationUpdates(callback)
    }
}