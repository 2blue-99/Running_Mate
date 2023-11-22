package com.running.domain.usecase

import android.location.Location
import com.running.domain.repo.LocationDataRepo
import kotlinx.coroutines.flow.Flow

/**
 * 2023-11-22
 * pureum
 */
class LocationUseCase(
    private val locationDataRepo: LocationDataRepo
) {
    fun getLocationDataStream(): Flow<Location> =
        locationDataRepo.getLocationDataStream
    fun removeLocationDataStream() =
        locationDataRepo.removeLocationDataStream()
}