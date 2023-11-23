package com.running.data.local.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 2023-11-21
 * pureum
 */
class LocationDataHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationDataHelper {
    private lateinit var callback: LocationCallback
    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private var _data = MutableSharedFlow<Location>()
    val data : SharedFlow<Location> get() = _data

    override val getLocationDataStream = flow {
        startLocationDataStream()
        data.collect {
            emit(it)
        }
    }

    private fun startLocationDataStream() {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 1000L
            fastestInterval = 1000L
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        callback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0.lastLocation?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        _data.emit(it)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            null
        )
    }
    override fun removeLocationDataStream() {
        fusedLocationClient.removeLocationUpdates(callback)
    }
}