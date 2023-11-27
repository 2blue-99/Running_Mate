package com.running.domain.usecase

import android.location.Location
import android.util.Log
import com.running.domain.repo.LocationDataRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 2023-11-22
 * pureum
 */
class LocationUseCase(
    private val locationDataRepo: LocationDataRepo
) {
    fun getLocationDataStream(): Flow<Location> = flow {
        while (true){
            emit(locationDataRepo.startLocationDataStream()})
            delay(1500L)
        }
    }
}